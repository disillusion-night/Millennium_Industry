package kivo.millennium.millind.block.device.HydraulicPress;

import kivo.millennium.millind.block.device.AbstractMachineBE;
import kivo.millennium.millind.capability.CapabilityCache;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import kivo.millennium.millind.recipe.CrushingRecipe;
import kivo.millennium.millind.recipe.ExtendedContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

import static kivo.millennium.millind.block.device.MillenniumBlockProperty.WORKING;

public class HydraulicPressBE extends AbstractMachineBE {
    public static final int SLOT_COUNT = 4;
    public static int BATTERY_SLOT = 0;
    public static int INPUT1_SLOT = 1;
    public static int INPUT2_SLOT = 2;
    public static int OUTPUT_SLOT = 3;
    private final int energyUsagePerTick = 200;
    private int totalTime = 100;
    private boolean isWorking;

    public HydraulicPressBE(BlockPos pPos, BlockState pBlockState) {
        super(MillenniumBlockEntities.HYDRAULIC_PRESS_BE.get(), pPos, pBlockState, new CapabilityCache.Builder()
                .withEnergy(100000, 2000)
                .withItems(4));
    }

    @Override
    protected void tickServer(){
        ItemStack inputStack1 = getItemHandler().getStackInSlot(INPUT1_SLOT);
        ItemStack inputStack2 = getItemHandler().getStackInSlot(INPUT2_SLOT);
        ItemStack outputStack = getItemHandler().getStackInSlot(OUTPUT_SLOT);
        boolean canStartCrushing = false;

        if (!inputStack1.isEmpty()) {
            Optional<CrushingRecipe> recipe = level.getRecipeManager().getRecipeFor(CrushingRecipe.Type.INSTANCE, new ExtendedContainer(inputStack2), level);

            if (recipe.isPresent()) {
                ItemStack recipeOutput = recipe.get().assemble(new ExtendedContainer(inputStack2), level.registryAccess());
                if (canCrush(outputStack, recipeOutput) && getEnergyStorage().getEnergyStored() >= energyUsagePerTick) {
                    canStartCrushing = true;
                }

                if (canStartCrushing) {
                    if(!isWorking){
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

    public int getProgressPercent() {
        return (int) (((float) getProgress() / totalTime) * 100);
    }


    private void startWorking(){
        isWorking = true;
        level.setBlock(getBlockPos(),getBlockState().setValue(WORKING, true), 3);
    }

    private void stopWorking(){
        isWorking = false;
        level.setBlock(getBlockPos(),getBlockState().setValue(WORKING, false), 3);

    }
    /*

    protected SimpleContainer getInputs(){
        return new SimpleContainer();
    }

    /*
    @Override
    protected void onContentChange(int slot) {
        if (slot == INPUT1_SLOT || slot == INPUT2_SLOT) {
            ItemStack inputStack1 = getItemHandler().getStackInSlot(INPUT1_SLOT);
            ItemStack inputStack2 = itemHandler.getStackInSlot(INPUT2_SLOT);
            if (inputStack1.isEmpty() || inputStack2.isEmpty()) {
                resetProgress();
            }
        }
    }*/


    public boolean isWorking() {
        return getBlockState().getValue(HydraulicPressBL.WORKING);
    }


    public int getProgressAndLit() {
        if (isWorking()) {
            return getProgressPercent() << 1 | 1;
        } else {
            return getProgressPercent() << 1;
        }
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

        getItemHandler().extractItem(INPUT1_SLOT, 1, false);
        getItemHandler().extractItem(INPUT2_SLOT, 1, false);
    }
}