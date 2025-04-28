package kivo.millennium.millind.machine.FusionChamber;

import kivo.millennium.millind.block.device.AbstractRecipeMachineBE;
import kivo.millennium.millind.capability.CapabilityCache;
import kivo.millennium.millind.capability.MillenniumFluidStorage;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import kivo.millennium.millind.init.MillenniumFluids;
import kivo.millennium.millind.recipe.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;


public class FusionChamberBE extends AbstractRecipeMachineBE<FusionRecipe> {
    public static final int SLOT_COUNT = 2;
    public static final int INPUT_SLOT = 1;
    public static final int INPUT_FLUID = 0;
    public static final int OUTPUT_FLUID = 1;

    private int fluidIntakeCooldown = 0;
    private static final int FLUID_INTAKE_INTERVAL = 20; // 每隔 20 ticks (1 秒) 尝试吸取流体
    private static final int FLUID_INTAKE_AMOUNT = 100; // 每次尝试吸取的流体数量 (以毫升为单位)

    public FusionChamberBE(BlockPos pWorldPosition, BlockState pBlockState) {
        super(MillenniumBlockEntities.FUSION_CHAMBER_BE.get(), FusionRecipe.Type.INSTANCE, pWorldPosition, pBlockState, new CapabilityCache.Builder()
                .withFluid(2, 12000)
                .withEnergy(200000, 10000)
                .withItems(2)
                .withProgress()
        );
        this.getFluidTank().setForInput(0).setForOutput(1);
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
                if (true) {
                    FluidStack requestedFluid = new FluidStack(handler.getFluidInTank(0), FLUID_INTAKE_AMOUNT);
                    // 模拟抽取，判断相邻方块是否有足够的流体
                    int filledAmount = handler.drain(requestedFluid, IFluidHandler.FluidAction.SIMULATE).getAmount();
                    if (filledAmount > 0) {
                        // 实际抽取流体并填充到输入流体槽
                        FluidStack drained = handler.drain(new FluidStack(requestedFluid.getFluid(), filledAmount), IFluidHandler.FluidAction.EXECUTE);
                        getFluidTank().addFluidToTank(0, drained, IFluidHandler.FluidAction.EXECUTE);
                        setChanged(); // 标记方块实体已更改
                    }
                }
            });
        }
    }

    @Override
    protected ProxyContainer getInputs() {
        return new ProxyContainer()
                .addProxy(getFluidTank(), INPUT_FLUID)
                .addProxy(getItemHandler(), INPUT_SLOT);
    }

    @Override
    protected ProxyContainer getOutputs() {
        return new ProxyContainer().addProxy(getFluidTank(), OUTPUT_FLUID);
    }

    @Override
    protected boolean isInputValid() {
        return !(getItemHandler().getStackInSlot(INPUT_SLOT).isEmpty() || getFluidTank().getFluidInTank(INPUT_FLUID).isEmpty());
    }

    public MillenniumFluidStorage getFluidTank(){
        return this.cache.getFluidCapability();
    }
}