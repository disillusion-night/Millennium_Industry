package kivo.millennium.millind.block.device.HydraulicPress;

import kivo.millennium.millind.block.device.AbstractMachineBE;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import kivo.millennium.millind.recipe.CrushingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class HydraulicPressBE extends AbstractMachineBE {
    public static int SLOT_COUNT = 4;
    public static int BATTERY_SLOT = 0;
    public static int INPUT1_SLOT = 1;
    public static int INPUT2_SLOT = 2;
    public static int OUTPUT_SLOT = 3;
    private final int energyUsagePerTick = 200;
    private final int maxLitTime = 10;
    private int progress = 0;
    private int totalTime = 100;
    private int litTime = 0;

    public HydraulicPressBE(BlockPos pPos, BlockState pBlockState) {
        super(MillenniumBlockEntities.HYDRAULIC_PRESS_BE.get(), pPos, pBlockState, SLOT_COUNT);
        this.MAX_TRANSFER_RATE = 1000;
    }

    @Override
    protected void tickServer() {
        if (itemHandler == null || energyStorage == null) return;

        ItemStack inputStack1 = itemHandler.getStackInSlot(INPUT1_SLOT);
        ItemStack inputStack2 = itemHandler.getStackInSlot(INPUT2_SLOT);
        ItemStack outputStack = itemHandler.getStackInSlot(OUTPUT_SLOT);
        boolean canStartCrushing = false;

        if (!inputStack1.isEmpty()) {
            Optional<CrushingRecipe> recipe = level.getRecipeManager().getRecipeFor(CrushingRecipe.Type.INSTANCE, new SimpleContainer(inputStack1, inputStack2), level);

            if (recipe.isPresent()) {
                ItemStack recipeOutput = recipe.get().assemble(getInputs(), level.registryAccess());
                if (canCrush(outputStack, recipeOutput) && energyStorage.getEnergyStored() >= energyUsagePerTick) {
                    canStartCrushing = true;
                }

                if (canStartCrushing) {
                    if (!getBlockState().getValue(HydraulicPressBL.POWERED)) {
                        level.setBlock(getBlockPos(), getBlockState().setValue(HydraulicPressBL.POWERED, true), 3);
                    }
                    progress++;
                    energyStorage.costEnergy(energyUsagePerTick);
                    setChanged(level, getBlockPos(), getBlockState());

                    if (progress >= totalTime) {
                        crushItem(recipeOutput);
                        resetProgress();
                        setChanged(level, getBlockPos(), getBlockState());
                    }
                } else {
                    level.setBlock(getBlockPos(), getBlockState().setValue(HydraulicPressBL.POWERED, false), 3);
                }
            }
        } else {
            //resetProgress();
            level.setBlock(getBlockPos(), getBlockState().setValue(HydraulicPressBL.POWERED, false), 3);
        }

    }

    protected SimpleContainer getInputs(){
        return new SimpleContainer();
    }


    @Override
    protected void onContentChange(int slot) {
        if (slot == INPUT1_SLOT || slot == INPUT2_SLOT) {
            ItemStack inputStack1 = itemHandler.getStackInSlot(INPUT1_SLOT);
            ItemStack inputStack2 = itemHandler.getStackInSlot(INPUT2_SLOT);
            if (inputStack1.isEmpty() || inputStack2.isEmpty()) {
                resetProgress();
            }
        }
    }

    private boolean isLit() {
        return getBlockState().getValue(HydraulicPressBL.POWERED);
    }

    private int getProgressPercent() {
        return (int) (((float) progress / totalTime) * 100);
    }

    public int getProgressAndLit() {
        if (isLit()) {
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
        ItemStack outputStack = itemHandler.getStackInSlot(OUTPUT_SLOT);

        if (outputStack.isEmpty()) {
            itemHandler.setStackInSlot(OUTPUT_SLOT, recipeOutput.copy());
        } else if (outputStack.is(recipeOutput.getItem())) {
            outputStack.grow(recipeOutput.getCount());
        }

        itemHandler.extractItem(INPUT1_SLOT, 1, false);
        itemHandler.extractItem(INPUT2_SLOT, 1, false);
    }

    private void resetProgress() {
        progress = 0;
    }

    @Override
    protected void saveData(CompoundTag pTag) {
        super.saveData(pTag);
        pTag.putInt("progress", progress);
    }

    @Override
    public void loadData(CompoundTag pTag) {
        super.loadData(pTag);
        progress = pTag.getInt("progress");
    }
}