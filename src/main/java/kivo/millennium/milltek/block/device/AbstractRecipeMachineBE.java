package kivo.millennium.milltek.block.device;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import static kivo.millennium.milltek.block.device.MillenniumBlockProperty.WORKING;

import java.util.List;
import java.util.Optional;

import kivo.millennium.milltek.Main;
import kivo.millennium.milltek.capability.CapabilityCache;
import kivo.millennium.milltek.recipe.*;

public abstract class AbstractRecipeMachineBE<R extends GenericRecipe> extends AbstractMachineBE {
    public CapabilityCache cache;
    public static int BATTERY_SLOT = 0;
    private boolean isWorking;
    private int totalTime;
    private final RecipeType<R> RecipeType;

    public <T extends AbstractRecipeMachineBE<R>> AbstractRecipeMachineBE(BlockEntityType<T> pType, RecipeType<R> pRecipeType, BlockPos pWorldPosition, BlockState pBlockState, CapabilityCache.Builder builder) {
        super(pType, pWorldPosition, pBlockState, builder.withProgress());
        this.cache = builder.build(this::setCapabilityChanged);
        this.RecipeType = pRecipeType;
    }

    @Override
    public void tickServer() {
        handleEnergyAcceptFromBattery();
        if (isInputValid()) {
            ProxyContainer inputs = getInputs();

            Optional<R> recipeO = level.getRecipeManager().getRecipeFor(RecipeType, inputs, level);

            if (recipeO.isPresent()) {
                R recipe = recipeO.get();
                boolean canStart = false;
                if (recipe.canProcess(getOutputs()) && getEnergyStorage().getEnergyStored() >= getEnergyCost(recipeO.get())) {
                    canStart = true;
                }

                if (canStart) {
                    startWorking();
                    totalTime = recipe.getTime();
                    addProgress();


                    if (getProgress() >= totalTime) {
                        getEnergyStorage().costEnergy(recipe.getEnergyCost());
                        process(recipe, recipe.getEnergyCost());
                        resetProgress();
                    }
                    setChanged();
                } else {
                    resetProgress();
                    stopWorking();
                }
            }
        } else {
            resetProgress();
            stopWorking();
        }

    }

    protected void addProgress(){
        cache.addProgress(1);
    }

    protected int getProgress(){
        return cache.getProgress();
    }
    protected void resetProgress() {
        cache.setProgress(0);
    }

    protected void setTotalTime(int totalTime){
        this.totalTime = totalTime;
    }

    protected int getTotalTime(){
         return this.totalTime;
    }

    protected void startWorking(){
        if (!isWorking){
            level.setBlock(getBlockPos(),getBlockState().setValue(WORKING, true), 3);
            isWorking = true;
        }
    }

    protected void stopWorking(){
        if (isWorking){
            level.setBlock(getBlockPos(),getBlockState().setValue(WORKING, false), 3);
            isWorking = false;
        }

    }

    public boolean isWorking() {
        return isWorking;
    }

    public int getProgressPercent() {
        return (int) (((float) getProgress() / totalTime) * 100);
    }

    protected abstract ProxyContainer getInputs();

    protected abstract ProxyContainer getOutputs();

    protected abstract boolean isInputValid();

    protected int getEnergyCost(R recipe){
        return recipe.getEnergyCost();  
    }

    protected void process(R recipe,int energycost) {
        ProxyContainer container = getOutputs();
        recipe.process(getInputs(), container, energycost);
    }

    protected void handleEnergyAcceptFromBattery(){
        getItemHandler().getStackInSlot(BATTERY_SLOT).getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {
            if (getEnergyStorage().canReceive()) {
                int received = getEnergyStorage().receiveEnergy(energy.extractEnergy(getEnergyStorage().getMaxEnergyStored() - getEnergyStorage().getEnergyStored(), false), false);
                if (received > 0) {
                    setChanged();
                }
            }
        });
    }

    public int getProgressAndLit() {
        if (isWorking()) {
            return getProgressPercent() << 1 | 1;
        } else {
            return getProgressPercent() << 1;
        }
    }

}