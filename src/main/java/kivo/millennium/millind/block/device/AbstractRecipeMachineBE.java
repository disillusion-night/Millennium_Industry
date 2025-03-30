package kivo.millennium.millind.block.device;

import kivo.millennium.millind.block.IWorkingMachine;
import kivo.millennium.millind.capability.CapabilityCache;
import kivo.millennium.millind.capability.CapabilityType;
import kivo.millennium.millind.capability.MillenniumEnergyStorage;
import kivo.millennium.millind.capability.MillenniumItemStorage;
import kivo.millennium.millind.recipe.CrushingRecipe;
import kivo.millennium.millind.recipe.ExtendedContainer;
import kivo.millennium.millind.recipe.GenericRecipe;
import kivo.millennium.millind.recipe.RecipeComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static kivo.millennium.millind.block.device.MillenniumBlockProperty.WORKING;

public abstract class AbstractRecipeMachineBE<R extends GenericRecipe> extends AbstractMachineBE {
    public CapabilityCache cache;
    private boolean isWorking;
    private int totalTime;

    public<T extends AbstractRecipeMachineBE<R>> AbstractRecipeMachineBE(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState, CapabilityCache.Builder builder) {
        super(pType, pWorldPosition, pBlockState, builder.withProgress());
        this.cache = builder.build(this::setCapabilityChanged);
    }

    @Override
    protected void tickServer() {

        if (isInputValid()) {
            Optional<R> recipe = getRecipe(getInputs());

            if (recipe.isPresent()) {
                boolean canStart = false;
                List<? extends RecipeComponent> recipeOutput = getRecipe(getInputs()).get().getRecipeOutputs();
                if (canProcess(getOutputs(), recipeOutput) && getEnergyStorage().getEnergyStored() >= getEnergyCost()) {
                    canStart = true;
                }

                if (canStart) {
                    if (!isWorking) {
                        startWorking();
                    }
                    addProgress();
                    costEnergy();

                    if (getProgress() >= totalTime) {
                        processItem(getInputs(), recipeOutput);
                        resetProgress();
                    }
                    setChanged();
                } else {
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
        isWorking = true;
        level.setBlock(getBlockPos(),getBlockState().setValue(WORKING, true), 3);
    }

    private void stopWorking(){
        isWorking = false;
        level.setBlock(getBlockPos(),getBlockState().setValue(WORKING, false), 3);

    }

    public boolean isWorking() {
        return isWorking;
    }

    public int getProgressPercent() {
        return (int) (((float) getProgress() / totalTime) * 100);
    }


    protected abstract ExtendedContainer getInputs();

    protected abstract ExtendedContainer getOutputs();

    protected abstract boolean isInputValid();

    protected abstract Optional<R> getRecipe(ExtendedContainer container);

    protected int getEnergyCost(){
        return 0;
    }

    protected int costEnergy(){
        return 0;
    }

    protected boolean canProcess(ExtendedContainer outputs, List<? extends RecipeComponent> recipeOutputs){
        return true;
    }

    private void costInput(){

    }


    private void processItem(ExtendedContainer container, List<? extends RecipeComponent> recipeOutputs) {

        costInput();
    }

}