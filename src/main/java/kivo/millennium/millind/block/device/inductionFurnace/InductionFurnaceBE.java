package kivo.millennium.millind.block.device.inductionFurnace;

import kivo.millennium.millind.block.device.AbstractDeviceBE;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class InductionFurnaceBE extends AbstractDeviceBE {
    public static int SLOT_COUNT = 3;
    public static int BATTERY_SLOT = 0;
    public static int INPUT_SLOT = 1;
    public static int OUTPUT_SLOT = 2;
    private final int energyUsagePerTick = 200;
    private final int maxLitTime = 10;
    private int smeltingProgress = 0;
    private int smeltingTotalTime = 100;
    private int litTime = 0;

    public InductionFurnaceBE(BlockPos pPos, BlockState pBlockState) {
        super(MillenniumBlockEntities.INDUCTION_FURNACE_BE.get(), pPos, pBlockState, SLOT_COUNT);
    }

    @Override
    protected void tickServer() {
        if (itemHandler == null || energyStorage == null) return;

        ItemStack inputStack = itemHandler.getStackInSlot(INPUT_SLOT);
        ItemStack outputStack = itemHandler.getStackInSlot(OUTPUT_SLOT);
        boolean canStartSmelting = false;

        if (!inputStack.isEmpty()) {
            Optional<BlastingRecipe> recipe = level.getRecipeManager().getRecipeFor(RecipeType.BLASTING, new SimpleContainer(inputStack), level);

            if (recipe.isPresent()) {
                ItemStack recipeOutput = recipe.get().assemble(new SimpleContainer(inputStack), level.registryAccess());
                if (canSmelt(outputStack, recipeOutput) && energyStorage.getEnergyStored() >= energyUsagePerTick) {
                    canStartSmelting = true;
                }

                if (canStartSmelting) {
                    if(!getBlockState().getValue(InductionFurnaceBL.POWERED)){
                      level.setBlock(getBlockPos(),getBlockState().setValue(InductionFurnaceBL.POWERED, true), 3);
                    }
                    smeltingProgress++;
                    energyStorage.costEnergy(energyUsagePerTick);
                    setChanged(level, getBlockPos(), getBlockState());

                    if (smeltingProgress >= smeltingTotalTime) {
                        smeltItem(recipeOutput);
                        resetSmelting();
                        if(itemHandler.getStackInSlot(INPUT_SLOT).isEmpty())
                            level.setBlock(getBlockPos(),getBlockState().setValue(InductionFurnaceBL.POWERED, false), 3);
                        setChanged(level, getBlockPos(), getBlockState());
                    }
                } else {
                    level.setBlock(getBlockPos(),getBlockState().setValue(InductionFurnaceBL.POWERED, false), 3);
                }
            }
        }

    }

    private boolean isLit(){
        return getBlockState().getValue(InductionFurnaceBL.POWERED);
    }

    private int getProgressPercent(){
        return (int) (((float) smeltingProgress / smeltingTotalTime) * 100);
    }

    public int getProgressAndLit(){
        if (isLit()){
            return getProgressPercent() << 1 | 1;
        }else {
            return getProgressPercent() << 1;
        }
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
        ItemStack outputStack = itemHandler.getStackInSlot(OUTPUT_SLOT);

        if (outputStack.isEmpty()) {
            itemHandler.setStackInSlot(OUTPUT_SLOT, recipeOutput.copy());
        } else if (outputStack.is(recipeOutput.getItem())) {
            outputStack.grow(recipeOutput.getCount());
        }

        itemHandler.extractItem(INPUT_SLOT, 1, false);
    }

    private void resetSmelting() {
        smeltingProgress = 0;
    }

    @Override
    protected void saveData(CompoundTag pTag) {
        super.saveData(pTag);
        pTag.putInt("smeltingProgress", smeltingProgress);
    }

    @Override
    public void loadData(CompoundTag pTag) {
        super.loadData(pTag);
        smeltingProgress = pTag.getInt("smeltingProgress");
    }

    public int getSmeltingProgress() {
        return smeltingProgress;
    }

    public int getSmeltingTotalTime() {
        return smeltingTotalTime;
    }
}
