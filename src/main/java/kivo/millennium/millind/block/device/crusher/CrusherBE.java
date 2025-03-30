package kivo.millennium.millind.block.device.crusher;

import kivo.millennium.millind.block.device.AbstractMachineBE;
import kivo.millennium.millind.capability.CapabilityCache;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import kivo.millennium.millind.recipe.CrushingRecipe;
import kivo.millennium.millind.recipe.ExtendedContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

import static kivo.millennium.millind.block.device.MillenniumBlockProperty.WORKING;

public class CrusherBE extends AbstractMachineBE {
    public static int SLOT_COUNT = 3;
    public static int BATTERY_SLOT = 0;
    public static int INPUT_SLOT = 1;
    public static int OUTPUT_SLOT = 2;
    private final int energyUsagePerTick = 200;
    private int totalTime = 100;
    private boolean isWorking;


    public CrusherBE(BlockPos pPos, BlockState pBlockState) {
        super(MillenniumBlockEntities.Crusher_BE.get(), pPos, pBlockState, new CapabilityCache.Builder()
                .withEnergy(100000, 2000)
                .withItems(3)
                .withProgress()
        );
    }

    @Override
    protected void tickServer() {
        ItemStack inputStack = getItemHandler().getStackInSlot(INPUT_SLOT);
        ItemStack outputStack = getItemHandler().getStackInSlot(OUTPUT_SLOT);
        boolean canStartCrushing = false;

        if (!inputStack.isEmpty()) {
            Optional<CrushingRecipe> recipe = level.getRecipeManager().getRecipeFor(CrushingRecipe.Type.INSTANCE, new ExtendedContainer(inputStack), level);

            if (recipe.isPresent()) {
                ItemStack recipeOutput = recipe.get().assemble(new ExtendedContainer(inputStack), level.registryAccess());
                if (canCrush(outputStack, recipeOutput) && getEnergyStorage().getEnergyStored() >= energyUsagePerTick) {
                    canStartCrushing = true;
                }

                if (canStartCrushing) {
                    if (!isWorking) {
                        startWorking();
                    }
                    addProgress();
                    getEnergyStorage().costEnergy(energyUsagePerTick);

                    if (getProgress() >= totalTime) {
                        crushItem(recipeOutput);
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


    private boolean canCrush(ItemStack outputStack, ItemStack recipeOutput) {
        if (recipeOutput.isEmpty()) {
            return false;
        }

        if (outputStack.isEmpty()) {
            return true;
        }

        if (!outputStack.is(recipeOutput.getItem())) {
            return false;
        }

        return outputStack.getCount() + recipeOutput.getCount() <= outputStack.getMaxStackSize();
    }

    private void crushItem(ItemStack recipeOutput) {
        ItemStack outputStack = getItemHandler().getStackInSlot(OUTPUT_SLOT);

        if (outputStack.isEmpty()) {
            getItemHandler().setStackInSlot(OUTPUT_SLOT, recipeOutput.copy());
        } else if (outputStack.is(recipeOutput.getItem())) {
            outputStack.grow(recipeOutput.getCount());
        }

        getItemHandler().extractItem(INPUT_SLOT, 1, false);
    }

}