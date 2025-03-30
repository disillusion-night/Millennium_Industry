package kivo.millennium.millind.block.device.FusionChamber;

import kivo.millennium.millind.block.device.AbstractMachineBE;
import kivo.millennium.millind.capability.CapabilityCache;
import kivo.millennium.millind.capability.MillenniumFluidStorage;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import kivo.millennium.millind.init.MillenniumFluids;
import kivo.millennium.millind.recipe.ExtendedContainer;
import kivo.millennium.millind.recipe.FusionRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public class FusionChamberBE extends AbstractMachineBE {
    public static final int SLOT_COUNT = 2;
    public static final int BATTERY_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int INPUT_FLUID = 0;
    public static final int OUTPUT_FLUID = 1;
    private int totalTime = 100;
    private FluidStack currentRecipeOutput = FluidStack.EMPTY;
    private static final int FLUID_CAPACITY_IN = 20000; // 毫升
    private static final int FLUID_CAPACITY_OUT = 12000; // 毫升

    // 输出流体槽
    private final MillenniumFluidStorage fluidTank = new MillenniumFluidStorage(2,FLUID_CAPACITY_IN, FLUID_CAPACITY_OUT);

    private LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> fluidTank);

    private FusionRecipe currentRecipe = null;

    private int fluidIntakeCooldown = 0;
    private static final int FLUID_INTAKE_INTERVAL = 20; // 每隔 20 ticks (1 秒) 尝试吸取流体
    private static final int FLUID_INTAKE_AMOUNT = 100; // 每次尝试吸取的流体数量 (以毫升为单位)

    public FusionChamberBE(BlockPos pWorldPosition, BlockState pBlockState) {
        super(MillenniumBlockEntities.FUSION_CHAMBER_BE.get(), pWorldPosition, pBlockState, new CapabilityCache.Builder()
                .withFluid(2, 12000)
                .withEnergy(200000, 10000)
                .withItems(2)
        );
        this.fluidTank.setFluidInTank(INPUT_FLUID, new FluidStack(MillenniumFluids.MOLTEN_CRYOLITE.get(), 10000));
    }

    @Override
    public void tickServer() {
        handleFluidIntake();
        // 1. 从电池槽充电
        ItemStack batteryStack = getItemHandler().getStackInSlot(BATTERY_SLOT);
        batteryStack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {
            if (getEnergyStorage().canReceive()) {
                int received = getEnergyStorage().receiveEnergy(energy.extractEnergy(getEnergyStorage().getMaxEnergyStored() - getEnergyStorage().getEnergyStored(), false), false);
                if (received > 0) {
                    setChanged();
                }
            }
        });

        ItemStack inputStack = getItemHandler().getStackInSlot(INPUT_SLOT);

        if (!inputStack.isEmpty()) {
            ExtendedContainer container = new ExtendedContainer(inputStack);
            container.setFluid(0, fluidTank.getFluidInTank(INPUT_FLUID));
            Optional<FusionRecipe> recipe = level.getRecipeManager().getRecipeFor(FusionRecipe.Type.INSTANCE, container, level);

            recipe.ifPresent(fusionRecipe -> {
                FluidStack recipeOutput = fusionRecipe.getResultFluid();
                int recipeMeltingTime = fusionRecipe.getTime();

                if (canProcess(recipeOutput)) {
                    this.totalTime = recipeMeltingTime; // 从配方中获取熔融时间
                    this.currentRecipeOutput = recipeOutput;
                    addProgress();
                    if (getProgress() >= this.totalTime) {
                        resetProgress();
                        this.fusionItem(fusionRecipe.getInputFluid().getFluidStack().getAmount(), recipeOutput);
                    }
                    setChanged();
                } else {
                    resetProgress();
                }
            });
        } else {
            resetProgress();
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

    /*

     */
    private boolean canProcess(FluidStack recipeOutput) {
        if (this.currentRecipeOutput.isEmpty() || (this.currentRecipeOutput.isFluidEqual(recipeOutput) && this.fluidTank.getFluidAmount(OUTPUT_FLUID) + recipeOutput.getAmount() <= this.fluidTank.getTankCapacity(OUTPUT_FLUID))) {
            return true;
        }
        return false;
    }

    private void fusionItem(int cost, FluidStack recipeOutput) {
        getItemHandler().extractItem(0, 1, false);
        this.fluidTank.drainFluidFromTank(INPUT_FLUID, cost);
        this.fluidTank.addFluidToTank(OUTPUT_FLUID, new FluidStack(recipeOutput.getFluid(), recipeOutput.getAmount()), IFluidHandler.FluidAction.EXECUTE);
        this.currentRecipeOutput = FluidStack.EMPTY; // 重置当前配方输出
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

    public int getProgressPercent() {
        return (int) (((float) getProgress() / totalTime) * 100);
    }

    public boolean isWorking() {
        return getBlockState().getValue(FusionChamberBL.WORKING);
    }

    public MillenniumFluidStorage getFluidTank(){
        return this.cache.getFluidCapability();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }
}