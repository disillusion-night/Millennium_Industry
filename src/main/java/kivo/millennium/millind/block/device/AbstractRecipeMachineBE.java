package kivo.millennium.millind.block.device;

import kivo.millennium.millind.Main;
import kivo.millennium.millind.capability.CapabilityCache;
import kivo.millennium.millind.recipe.ExtendedContainer;
import kivo.millennium.millind.recipe.GenericRecipe;
import kivo.millennium.millind.recipe.RecipeComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.List;
import java.util.Optional;

import static kivo.millennium.millind.block.device.MillenniumBlockProperty.WORKING;

public abstract class AbstractRecipeMachineBE<R extends GenericRecipe> extends AbstractMachineBE {
    public CapabilityCache cache;
    public static int BATTERY_SLOT = 0;
    private boolean isWorking;
    private int totalTime;
    private final RecipeType<R> RecipeType;

    public <T extends AbstractRecipeMachineBE<R>> AbstractRecipeMachineBE(BlockEntityType<?> pType, RecipeType<R> pRecipeType, BlockPos pWorldPosition, BlockState pBlockState, CapabilityCache.Builder builder) {
        super(pType, pWorldPosition, pBlockState, builder.withProgress());
        this.cache = builder.build(this::setCapabilityChanged);
        this.RecipeType = pRecipeType;
    }

    @Override
    protected void tickServer() {
        handleEnergyAcceptFromBattery();
        if (isInputValid()) {
            Optional<R> recipeO = level.getRecipeManager().getRecipeFor(RecipeType, getInputs(), level);

            if (recipeO.isPresent()) {
                R recipe = recipeO.get();
                boolean canStart = false;
                List<? extends RecipeComponent> recipeOutput = recipe.getRecipeOutputs();
                if (canProcess(recipeOutput) && getEnergyStorage().getEnergyStored() >= getEnergyCost()) {
                    canStart = true;
                }

                if (canStart) {
                    startWorking();
                    totalTime = recipe.getTime();
                    addProgress();


                    if (getProgress() >= totalTime) {
                        getEnergyStorage().costEnergy(recipe.getEnergyCost());
                        processItem(getInputs(), recipe, recipe.getEnergyCost());
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

    private void addProgress(){
        cache.addProgress(1);
    }

    private int getProgress(){
        return cache.getProgress();
    }
    private void resetProgress() {
        cache.setProgress(0);
    }

    private void startWorking(){
        if (!isWorking){
            level.setBlock(getBlockPos(),getBlockState().setValue(WORKING, true), 3);
            isWorking = true;
        }
    }

    private void stopWorking(){
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

    protected abstract ExtendedContainer getInputs();

    protected abstract boolean isInputValid();

    protected int getEnergyCost(){
        return 0;
    }

    protected abstract boolean canProcess(List<? extends RecipeComponent> recipeOutputs);

    protected abstract void processItem(ExtendedContainer container, R recipe, int energycost);

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
}