package kivo.millennium.millind.block.device.FusionChamber;

import kivo.millennium.millind.Main;
import kivo.millennium.millind.block.device.AbstractMachineBE;
import kivo.millennium.millind.block.device.AbstractRecipeMachineBE;
import kivo.millennium.millind.capability.CapabilityCache;
import kivo.millennium.millind.capability.MillenniumFluidStorage;
import kivo.millennium.millind.fluid.MoltenIronFL;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import kivo.millennium.millind.init.MillenniumFluids;
import kivo.millennium.millind.recipe.ExtendedContainer;
import kivo.millennium.millind.recipe.FusionRecipe;
import kivo.millennium.millind.recipe.RecipeComponent;
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

import java.util.List;
import java.util.Optional;

import static kivo.millennium.millind.block.device.MillenniumBlockProperty.WORKING;


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

        this.getFluidTank().addFluidToTank(0, new FluidStack(MillenniumFluids.MOLTEN_IRON.get(), 10000), IFluidHandler.FluidAction.EXECUTE);
    }

    @Override
    public void tickServer() {
        handleFluidIntake();
        super.tickServer();
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
    protected ExtendedContainer getInputs() {
        ExtendedContainer container = new ExtendedContainer(getItemHandler().getStackInSlot(INPUT_SLOT));
        container.setFluid(0,getFluidTank().getFluidInTank(INPUT_FLUID));
        return container;
    }

    @Override
    protected boolean isInputValid() {
        return !(getItemHandler().getStackInSlot(INPUT_SLOT).isEmpty() || getFluidTank().getFluidInTank(INPUT_FLUID).isEmpty());
    }

    @Override
    protected boolean canProcess(List<? extends RecipeComponent> recipeOutputs) {
        if (recipeOutputs.isEmpty()) {
            return false;
        }

        if (getFluidTank().getFluidInTank(OUTPUT_FLUID).isEmpty()) {
            return true;
        }

        if (!(getFluidTank().getFluidInTank(OUTPUT_FLUID).getFluid() == recipeOutputs.get(0).asFluidComponent().getFluidStack().getFluid())) {
            return false;
        }

        return getFluidTank().getFluidAmount(OUTPUT_FLUID) + recipeOutputs.get(0).asFluidComponent().getFluidStack().getAmount() <= getFluidTank().getTankCapacity(OUTPUT_FLUID);

    }

    @Override
    protected void processItem(ExtendedContainer container, FusionRecipe recipe, int energycost) {
        getItemHandler().extractItem(1, 1, false);
        getFluidTank().drainFluidFromTank(INPUT_FLUID, container.getFluid(0).getAmount());
        getFluidTank().addFluidToTank(OUTPUT_FLUID, recipe.getResultFluid(), IFluidHandler.FluidAction.EXECUTE);
        setChanged();
    }

    public MillenniumFluidStorage getFluidTank(){
        return this.cache.getFluidCapability();
    }
}