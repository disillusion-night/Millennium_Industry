package kivo.millennium.milltek.block.container.fluid;

import kivo.millennium.milltek.block.container.base.AbstractContainerBE;
import kivo.millennium.milltek.block.property.EFaceMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.Nullable;

public abstract class FluidContainerBE extends AbstractContainerBE {
    protected LazyOptional<IFluidHandler> fluidHandler = LazyOptional.empty();

    public FluidContainerBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.fluidHandler = LazyOptional.of(() -> new ContainerFluidTank(getMaxFluidAmount(), getMaxInputRate(), getMaxOutputRate()));
    }

    /**
     * 获取本方块实体的最大流体容量（可被子类覆写）
     */
    public static int getMaxFluidAmount() {
        return 10000; // 默认值，子类可覆写
    }

    /**
     * 获取本方块实体的最大流体输入速率（可被子类覆写）
     */
    public static int getMaxInputRate() {
        return 1000; // 默认值，子类可覆写
    }

    /**
     * 获取本方块实体的最大流体输出速率（可被子类覆写）
     */
    public static int getMaxOutputRate() {
        return 1000; // 默认值，子类可覆写
    }

    protected int getTankCapacity() {
        return 10000;
    }

    // 子类需实现fluidHandler初始化

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER && fluidHandler.isPresent()) {
            EFaceMode mode = getFaceMode(side);
            if (mode == EFaceMode.DISCONNECT) {
                return LazyOptional.empty();
            }
            return fluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void handlePull(Direction dir) {
        Level lvl = this.level;
        if (lvl == null || lvl.isClientSide())
            return;
        IFluidHandler self = fluidHandler.orElseGet(() -> null);
        if (self == null)
            return;
        BlockEntity neighbor = lvl.getBlockEntity(worldPosition.relative(dir));
        if (neighbor != null) {
            neighbor.getCapability(ForgeCapabilities.FLUID_HANDLER, dir.getOpposite()).ifPresent(handler -> {
                int amount = 1000; // 可由子类提供最大流速
                var drain = handler.drain(amount, FluidAction.SIMULATE);
                if (!drain.isEmpty() && drain.getAmount() > 0) {
                    int filled = self.fill(drain, FluidAction.EXECUTE);
                    if (filled > 0) {
                        handler.drain(filled, FluidAction.EXECUTE);
                    }
                }
            });
        }
    }

    @Override
    protected void handlePush(Direction dir) {
        Level lvl = this.level;
        if (lvl == null || lvl.isClientSide())
            return;
        IFluidHandler self = fluidHandler.orElseGet(() -> null);
        if (self == null)
            return;
        BlockEntity neighbor = lvl.getBlockEntity(worldPosition.relative(dir));
        if (neighbor != null) {
            neighbor.getCapability(ForgeCapabilities.FLUID_HANDLER, dir.getOpposite()).ifPresent(handler -> {
                int amount = 1000; // 可由子类提供最大流速
                for (int tank = 0; tank < self.getTanks(); tank++) {
                    var stack = self.getFluidInTank(tank);
                    if (!stack.isEmpty() && stack.getAmount() > 0) {
                        var toSend = stack.copy();
                        toSend.setAmount(Math.min(amount, stack.getAmount()));
                        int filled = handler.fill(toSend, FluidAction.EXECUTE);
                        if (filled > 0) {
                            self.drain(filled, FluidAction.EXECUTE);
                        }
                    }
                }
            });
        }
    }
}
