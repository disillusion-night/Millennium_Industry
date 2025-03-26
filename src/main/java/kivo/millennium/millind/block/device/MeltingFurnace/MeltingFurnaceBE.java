package kivo.millennium.millind.block.device.MeltingFurnace;

import kivo.millennium.millind.Main;
import kivo.millennium.millind.block.device.AbstractMachineBE;
import kivo.millennium.millind.block.device.crusher.CrusherBL;
import kivo.millennium.millind.capability.DeviceEnergyStorage;
import kivo.millennium.millind.datagen.MillenniumRecipeProvider;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import kivo.millennium.millind.init.MillenniumRecipes;
import kivo.millennium.millind.recipe.ExtendedContainer;
import kivo.millennium.millind.recipe.ItemComponent;
import kivo.millennium.millind.recipe.MeltingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MeltingFurnaceBE extends AbstractMachineBE {
    public static final int SLOT_COUNT = 2;
    public static final int BATTERY_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    private int progress = 0;
    private int totalTime = 100;
    private FluidStack currentRecipeOutput = FluidStack.EMPTY;
    private static final int FLUID_CAPACITY = 12000; // 毫升

    private final FluidTank fluidTank = new FluidTank(FLUID_CAPACITY) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    @Override
    public int getSlotCount() {
        return SLOT_COUNT;
    }

    protected DeviceEnergyStorage createEnergyStorage() {
        return new DeviceEnergyStorage(200000, MAX_TRANSFER_RATE);
    }

    private LazyOptional<IFluidHandler> fluidHandlerLazy = LazyOptional.of(() -> fluidTank);

    public MeltingFurnaceBE(BlockPos pWorldPosition, BlockState pBlockState) {
        super(MillenniumBlockEntities.MELTING_FURNACE_BE.get(), pWorldPosition, pBlockState);
    }

    @Override
    public void tickServer() {
        // 1. 从电池槽充电
        ItemStack batteryStack = itemHandler.getStackInSlot(BATTERY_SLOT);
        batteryStack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {
            if (energyStorage.canReceive()) {
                int received = energyStorage.receiveEnergy(energy.extractEnergy(energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored(), false), false);
                if (received > 0) {
                    setChanged();
                }
            }
        });

        ItemStack inputStack = this.itemHandler.getStackInSlot(INPUT_SLOT);

        if (!inputStack.isEmpty()) {
            Optional<MeltingRecipe> recipe = level.getRecipeManager().getRecipeFor(MeltingRecipe.Type.INSTANCE, new ExtendedContainer(inputStack), level);

            recipe.ifPresent(meltingRecipe -> {
                FluidStack recipeOutput = meltingRecipe.getResultFluid();
                int recipeMeltingTime = meltingRecipe.getTime();

                if (this.canProcess(recipeOutput)) {
                    this.totalTime = recipeMeltingTime; // 从配方中获取熔融时间
                    this.currentRecipeOutput = recipeOutput;
                    this.progress++;
                    this.setChanged();
                    if (this.progress >= this.totalTime) {
                        this.progress = 0;
                        this.meltItem(recipeOutput);
                    }
                } else {
                    this.resetProgress();
                }
            });
        } else {
            this.resetProgress();
        }
    }

    private boolean canProcess(FluidStack recipeOutput) {
        if (this.currentRecipeOutput.isEmpty() || (this.currentRecipeOutput.isFluidEqual(recipeOutput) && this.fluidTank.getFluidAmount() + recipeOutput.getAmount() <= this.fluidTank.getCapacity())) {
            return true;
        }
        return false;
    }

    private void meltItem(FluidStack recipeOutput) {
        this.itemHandler.extractItem(0, 1, false); // 移除一个输入物品
        this.fluidTank.fill(new FluidStack(recipeOutput.getFluid(), recipeOutput.getAmount()), IFluidHandler.FluidAction.EXECUTE);
        this.currentRecipeOutput = FluidStack.EMPTY; // 重置当前配方输出
        this.setChanged();
    }

    private void resetProgress() {
        this.progress = 0;
        this.totalTime = 0;
        this.currentRecipeOutput = FluidStack.EMPTY;
        this.setChanged();
    }

    private int getProgressPercent() {
        return (int) (((float) progress / totalTime) * 100);
    }

    private boolean isLit() {
        return getBlockState().getValue(MeltingFurnaceBL.POWERED);
    }

    public int getProgressAndLit() {
        if (isLit()) {
            return getProgressPercent() << 1 | 1;
        } else {
            return getProgressPercent() << 1;
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandlerLazy.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void saveData(CompoundTag pTag) {
        super.saveData(pTag);
        fluidTank.writeToNBT(pTag);
    }

    @Override
    public void loadData(CompoundTag pTag) {
        super.loadData(pTag);
        if (pTag.contains("fluid")){
            fluidTank.readFromNBT(pTag.getCompound("fluid"));
        }
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    public DeviceEnergyStorage getEnergyStorage() {
        return energyStorage;
    }
}