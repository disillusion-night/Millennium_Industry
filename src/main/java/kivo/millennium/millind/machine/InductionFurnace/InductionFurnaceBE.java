package kivo.millennium.millind.machine.InductionFurnace;

import kivo.millennium.millind.block.IWorkingMachine;
import kivo.millennium.millind.block.device.AbstractMachineBE;
import kivo.millennium.millind.capability.CapabilityCache;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

import static kivo.millennium.millind.block.device.MillenniumBlockProperty.WORKING;

public class InductionFurnaceBE extends AbstractMachineBE implements IWorkingMachine {
    public static final int SLOT_COUNT = 3;
    public static int BATTERY_SLOT = 0;
    public static int INPUT_SLOT = 1;
    public static int OUTPUT_SLOT = 2;
    private final int energyUsagePerTick = 200;
    private int smeltingTotalTime = 100;
    private boolean isWorking;

    public InductionFurnaceBE(BlockPos pPos, BlockState pBlockState) {
        super(MillenniumBlockEntities.INDUCTION_FURNACE_BE.get(), pPos, pBlockState, new CapabilityCache.Builder()
                .withItems(3)
                .withEnergy(100000, 2000)
                .withProgress()
        );
    }

    @Override
    protected void tickServer() {
        ItemStack inputStack = getItemHandler().getStackInSlot(INPUT_SLOT);
        ItemStack outputStack = getItemHandler().getStackInSlot(OUTPUT_SLOT);
        boolean canStartSmelting = false;

        if (!inputStack.isEmpty()) {
            Optional<BlastingRecipe> recipe = level.getRecipeManager().getRecipeFor(RecipeType.BLASTING, new SimpleContainer(inputStack), level);

            if (recipe.isPresent()) {
                ItemStack recipeOutput = recipe.get().assemble(new SimpleContainer(inputStack), level.registryAccess());
                if (canSmelt(outputStack, recipeOutput) && getEnergyStorage().getEnergyStored() >= energyUsagePerTick) {
                    canStartSmelting = true;
                }

                if (canStartSmelting) {
                    if(!isWorking){
                        startWorking();
                    }

                    addProgress();
                    getEnergyStorage().costEnergy(energyUsagePerTick);

                    if (getProgress() >= smeltingTotalTime) {
                        smeltItem(recipeOutput);
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
        isWorking = true;
        level.setBlock(getBlockPos(),getBlockState().setValue(WORKING, true), 3);
    }

    private void stopWorking(){
        isWorking = false;
        level.setBlock(getBlockPos(),getBlockState().setValue(WORKING, false), 3);

    }

    public boolean isWorking(){
        return isWorking;
    }


    public int getProgressPercent(){
        return (int) (((float) getProgress() / smeltingTotalTime) * 100);
    }

    private boolean canSmelt(ItemStack outputStack, ItemStack recipeOutput) {
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

    private void smeltItem(ItemStack recipeOutput) {
        ItemStack outputStack = getItemHandler().getStackInSlot(OUTPUT_SLOT);

        if (outputStack.isEmpty()) {
            getItemHandler().setStackInSlot(OUTPUT_SLOT, recipeOutput.copy());
        } else if (outputStack.is(recipeOutput.getItem())) {
            outputStack.grow(recipeOutput.getCount());
        }

        getItemHandler().extractItem(INPUT_SLOT, 1, false);
    }

}
