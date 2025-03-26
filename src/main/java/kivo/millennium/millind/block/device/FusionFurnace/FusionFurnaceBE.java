package kivo.millennium.millind.block.device.FusionFurnace;

import kivo.millennium.millind.Main;
import kivo.millennium.millind.block.device.AbstractMachineBE;
import kivo.millennium.millind.capability.DeviceEnergyStorage;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import kivo.millennium.millind.init.MillenniumFluids;
import kivo.millennium.millind.recipe.ExtendedContainer;
import kivo.millennium.millind.recipe.FusionRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public class FusionFurnaceBE extends AbstractMachineBE {
    public static final int SLOT_COUNT = 2;
    public static final int BATTERY_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    private int progress = 0;
    private int totalTime = 100;
    private FluidStack currentRecipeOutput = FluidStack.EMPTY;
    private static final int FLUID_CAPACITY_IN = 20000; // 毫升
    private static final int FLUID_CAPACITY_OUT = 12000; // 毫升

    // 输出流体槽
    private final FusionFurnaceTank fluidTank = new FusionFurnaceTank(FLUID_CAPACITY_OUT);

    @Override
    public int getSlotCount() {
        return SLOT_COUNT;
    }

    protected DeviceEnergyStorage createEnergyStorage() {
        return new DeviceEnergyStorage(200000, MAX_TRANSFER_RATE);
    }

    private LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> fluidTank);

    private FusionRecipe currentRecipe = null;

    private int fluidIntakeCooldown = 0;
    private static final int FLUID_INTAKE_INTERVAL = 20; // 每隔 20 ticks (1 秒) 尝试吸取流体
    private static final int FLUID_INTAKE_AMOUNT = 100; // 每次尝试吸取的流体数量 (以毫升为单位)

    public FusionFurnaceBE(BlockPos pWorldPosition, BlockState pBlockState) {
        super(MillenniumBlockEntities.FUSION_FURNACE_BE.get(), pWorldPosition, pBlockState);
    }

    @Override
    public void tickServer() {
        handleFluidIntake();
        this.fluidTank.setFluidIn(new FluidStack(MillenniumFluids.MOLTEN_CRYOLITE.get(), 10000));
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
            ExtendedContainer container = new ExtendedContainer(inputStack);
            container.setFluid(0, this.fluidTank.getFluidIn());
            Optional<FusionRecipe> recipe = level.getRecipeManager().getRecipeFor(FusionRecipe.Type.INSTANCE, container, level);

            recipe.ifPresent(fusionRecipe -> {
                FluidStack recipeOutput = fusionRecipe.getResultFluid();
                int recipeMeltingTime = fusionRecipe.getTime();

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
        if (this.currentRecipeOutput.isEmpty() || (this.currentRecipeOutput.isFluidEqual(recipeOutput) && this.fluidTank.getFluidAmountIn() + recipeOutput.getAmount() <= this.fluidTank.getCapacityOut())) {
            return true;
        }
        return false;
    }

    private void meltItem(FluidStack recipeOutput) {
        this.itemHandler.extractItem(0, 1, false); // 移除一个输入物品
        this.fluidTank.addToOut(new FluidStack(recipeOutput.getFluid(), recipeOutput.getAmount()), IFluidHandler.FluidAction.EXECUTE);
        this.currentRecipeOutput = FluidStack.EMPTY; // 重置当前配方输出
        this.setChanged();
    }

    private void resetProgress() {
        this.progress = 0;
        this.totalTime = 0;
        this.currentRecipeOutput = FluidStack.EMPTY;
        this.setChanged();
    }

    // 处理流体吸取逻辑
    private void handleFluidIntake() {
        if (fluidIntakeCooldown > 0) {
            fluidIntakeCooldown--;
            return;
        }

        fluidIntakeCooldown = FLUID_INTAKE_INTERVAL;

        // 遍历周围所有方向
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);
            BlockEntity neighborBE = level.getBlockEntity(neighborPos);
            if (neighborBE == null) break;
            // 获取相邻方块的流体处理 Capability
            neighborBE.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).ifPresent(handler -> {
                // 检查当前是否有需要吸取的流体
                if (currentRecipe != null && currentRecipe.getInputFluid() != null) {
                    FluidStack requestedFluid = new FluidStack(currentRecipe.getInputFluid().getFluidStack(), FLUID_INTAKE_AMOUNT);
                    // 模拟抽取，判断相邻方块是否有足够的流体
                    int filledAmount = handler.drain(requestedFluid, IFluidHandler.FluidAction.SIMULATE).getAmount();
                    if (filledAmount > 0) {
                        // 实际抽取流体并填充到输入流体槽
                        FluidStack drained = handler.drain(new FluidStack(requestedFluid.getFluid(), filledAmount), IFluidHandler.FluidAction.EXECUTE);
                        fluidTank.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                        setChanged(); // 标记方块实体已更改
                    }
                }
            });
        }
    }
    private int getProgressPercent() {
        return (int) (((float) progress / totalTime) * 100);
    }

    private boolean isLit() {
        return getBlockState().getValue(FusionFurnaceBL.POWERED);
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
            return fluidHandler.cast();
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

    public FusionFurnaceTank getFluidTank() {
        return fluidTank;
    }

    public DeviceEnergyStorage getEnergyStorage() {
        return energyStorage;
    }
}