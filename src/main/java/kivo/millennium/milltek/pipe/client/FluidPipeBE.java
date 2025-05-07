package kivo.millennium.milltek.pipe.client;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import kivo.millennium.milltek.block.device.AbstractMachineBE;
import kivo.millennium.milltek.capability.CapabilityType;
import kivo.millennium.milltek.capability.IMillenniumStorage;
import kivo.millennium.milltek.capability.MetalTankFluidHandler;
import kivo.millennium.milltek.capability.MillenniumFluidStorage;
import kivo.millennium.milltek.init.MillenniumBlockEntities;

import static kivo.millennium.milltek.pipe.client.EPipeState.CONNECT;
import static kivo.millennium.milltek.pipe.client.EPipeState.getPropertyForDirection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class FluidPipeBE extends BlockEntity {
    private static List<Direction> directions = List.of(Direction.NORTH, Direction.UP, Direction.EAST, Direction.SOUTH,Direction.DOWN, Direction.WEST);

    private FluidTank fluidTank;

    private LazyOptional<IFluidHandler> fluidHandlerLazy = LazyOptional.of(() -> this.fluidTank);

    public FluidPipeBE(BlockPos pos, BlockState state) {
        super(MillenniumBlockEntities.FLUID_PIPE_BE.get(), pos, state);
        this.fluidTank = new FluidTank(10000);
    }

    // 每 tick 执行的逻辑，由 AbstractDeviceBL 的 Ticker 调用
    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, FluidPipeBE pBlockEntity) {
        if (pLevel.isClientSide()) {
            return; // 客户端不做逻辑处理
        }

        pBlockEntity.tickServer(); // 调用服务端的 tick 逻辑
    }

    public void tickServer() {
        if (level == null || level.isClientSide) {
            return;
        }

        if (fluidTank.getFluidAmount() > 0) {
            directions.forEach(direction -> {
                switch (getBlockState().getValue(getPropertyForDirection(direction))) {
                    case CONNECT, OUTPUT -> {
                        BlockPos targetPos = getBlockPos().relative(direction);
                        BlockEntity targetBE = level.getBlockEntity(targetPos);
                        if (targetBE != null) {
                            targetBE.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).ifPresent(targetHandler -> {
                                FluidStack fluidToTransfer = fluidTank.getFluid().copy();
                                int filledAmount = targetHandler.fill(fluidToTransfer, IFluidHandler.FluidAction.SIMULATE);
                                if (filledAmount > 0) {
                                    FluidStack drained = fluidTank.drain(filledAmount, IFluidHandler.FluidAction.EXECUTE);
                                    targetHandler.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                                }
                            });
                        }
                    }
                    case INSERT -> {
                        BlockPos sourcePos = getBlockPos().relative(direction);
                        BlockEntity sourceBE = level.getBlockEntity(sourcePos);
                        if (sourceBE != null) {
                            sourceBE.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).ifPresent(sourceHandler -> {
                                FluidStack spaceInTank = new FluidStack(fluidTank.getFluid().getFluid(), fluidTank.getCapacity() - fluidTank.getFluidAmount());
                                if (spaceInTank.getAmount() > 0) {
                                    FluidStack drained = sourceHandler.drain(spaceInTank, IFluidHandler.FluidAction.SIMULATE);
                                    if (!drained.isEmpty()) {
                                        int filled = fluidTank.fill(drained.copy(), IFluidHandler.FluidAction.SIMULATE);
                                        if (filled > 0) {
                                            FluidStack actuallyDrained = sourceHandler.drain(new FluidStack(drained.getFluid(), filled), IFluidHandler.FluidAction.EXECUTE);
                                            fluidTank.fill(actuallyDrained, IFluidHandler.FluidAction.EXECUTE);
                                        }
                                    }
                                }
                            });
                        }
                    }
                    case NONE, DISCONNECTED -> {
                        // 不进行任何操作
                    }
                }
            });
        } else {
            // 如果管道是空的，尝试从 INSERT 连接的方块中吸取流体
            directions.forEach(direction -> {
                if (getBlockState().getValue(getPropertyForDirection(direction)) == EPipeState.INSERT) {
                    BlockPos sourcePos = getBlockPos().relative(direction);
                    BlockEntity sourceBE = level.getBlockEntity(sourcePos);
                    if (sourceBE != null) {
                        sourceBE.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).ifPresent(sourceHandler -> {
                            int space = fluidTank.getCapacity() - fluidTank.getFluidAmount();
                            if (space > 0) {
                                FluidStack drained = sourceHandler.drain(space, IFluidHandler.FluidAction.SIMULATE);
                                if (!drained.isEmpty()) {
                                    int filled = fluidTank.fill(drained.copy(), IFluidHandler.FluidAction.EXECUTE);
                                    sourceHandler.drain(new FluidStack(drained.getFluid(), filled), IFluidHandler.FluidAction.EXECUTE);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        CompoundTag fluid = new CompoundTag();
        fluidTank.writeToNBT(fluid);
        tag.put(CapabilityType.FLUID.toString(), fluid);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        fluidTank.readFromNBT(tag.getCompound(CapabilityType.FLUID.toString()));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandlerLazy.cast();
        }
        return super.getCapability(cap, side);
    }
}

