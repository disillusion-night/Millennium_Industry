package kivo.millennium.millind.block.device;

import kivo.millennium.millind.block.device.crusher.CrusherBL;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import kivo.millennium.millind.recipe.CrushingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public abstract class DeviceWithRecipeBE<R extends Recipe> extends AbstractMachineBE {
    public static int SLOT_COUNT = 3;
    public static int BATTERY_SLOT = 0;
    public static int INPUT_SLOT = 1;
    public static int OUTPUT_SLOT = 2;
    private final int energyUsagePerTick = 200;
    private final int maxLitTime = 10;
    private int progress = 0;
    private int totalTime = 100;
    private int litTime = 0;
    protected Optional<R> workingRecipe;
    private Item workingItem = null;


    public DeviceWithRecipeBE(BlockPos pPos, BlockState pBlockState) {
        super(MillenniumBlockEntities.Crusher_BE.get(), pPos, pBlockState);
        this.MAX_TRANSFER_RATE = 1000;
    }
    @Override
    protected void tickServer() {
        if (itemHandler == null || energyStorage == null) return;

        ItemStack inputStack = itemHandler.getStackInSlot(INPUT_SLOT);
        ItemStack outputStack = itemHandler.getStackInSlot(OUTPUT_SLOT);
        boolean canStartCrushing = false;

        if (!inputStack.isEmpty()) {
            Optional<CrushingRecipe> recipe = level.getRecipeManager().getRecipeFor(CrushingRecipe.Type.INSTANCE, new SimpleContainer(inputStack), level);

            if (recipe.isPresent()) {
                ItemStack recipeOutput = recipe.get().assemble(new SimpleContainer(inputStack), level.registryAccess());
                if (canProcess(outputStack, recipeOutput) && energyStorage.getEnergyStored() >= energyUsagePerTick) {
                    canStartCrushing = true;
                }

                if (canStartCrushing) {
                    if (!getBlockState().getValue(CrusherBL.POWERED)) {
                        level.setBlock(getBlockPos(), getBlockState().setValue(AbstractDeviceBL.POWERED, true), 3);
                    }
                    progress++;
                    energyStorage.costEnergy(energyUsagePerTick);
                    setChanged(level, getBlockPos(), getBlockState());

                    if (progress >= totalTime) {
                        processItem(recipeOutput);
                        resetProgress();
                        setChanged(level, getBlockPos(), getBlockState());
                    }
                } else {
                    level.setBlock(getBlockPos(), getBlockState().setValue(AbstractDeviceBL.POWERED, false), 3);
                }
            }
        } else {
            resetProgress();
            level.setBlock(getBlockPos(), getBlockState().setValue(AbstractDeviceBL.POWERED, false), 3);
        }

    }
    /*
    protected Optional<T extends Recipe> getRecipe(){

    }*/

    @Override
    protected void onContentChange(int slot) {
        if (slot == INPUT_SLOT) {
            ItemStack inputStack = itemHandler.getStackInSlot(INPUT_SLOT);
            if (workingItem != inputStack.getItem()) {
                resetProgress();
                workingItem = inputStack.getItem();
            }
        }
    }

    private boolean isLit() {
        return getBlockState().getValue(CrusherBL.POWERED);
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

    private boolean canProcess(ItemStack outputStack, ItemStack recipeOutput) {
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

    private void processItem(ItemStack recipeOutput) {
        ItemStack outputStack = itemHandler.getStackInSlot(OUTPUT_SLOT);

        if (outputStack.isEmpty()) {
            itemHandler.setStackInSlot(OUTPUT_SLOT, recipeOutput.copy());
        } else if (outputStack.is(recipeOutput.getItem())) {
            outputStack.grow(recipeOutput.getCount());
        }

        itemHandler.extractItem(INPUT_SLOT, 1, false);
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
