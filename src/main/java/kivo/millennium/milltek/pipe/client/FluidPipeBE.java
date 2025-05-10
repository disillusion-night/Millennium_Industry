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
    private static final List<Direction> directions = List.of(Direction.NORTH, Direction.UP, Direction.EAST,
            Direction.SOUTH, Direction.DOWN, Direction.WEST);

    private final FluidTank fluidTank;
    private final LazyOptional<IFluidHandler> fluidHandlerLazy;

    public FluidPipeBE(BlockPos pos, BlockState state) {
        super(MillenniumBlockEntities.FLUID_PIPE_BE.get(), pos, state);
        this.fluidTank = new FluidTank(10000);
        this.fluidHandlerLazy = LazyOptional.of(() -> this.fluidTank);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FluidPipeBE blockEntity) {
        if (level.isClientSide()) {
            return; // 客户端不处理逻辑
        }
        blockEntity.tickServer();
    }

    public void tickServer() {
        if (level == null || level.isClientSide) {
            return;
        }

        // 先尝试从相邻方块吸取流体
        directions.forEach(direction -> {
            EPipeState pipeState = getBlockState().getValue(getPropertyForDirection(direction));
            if (pipeState == EPipeState.INSERT) {
                pullFluidFrom(direction);
            }
        });

        // 然后尝试将流体推送到相邻方块
        if (fluidTank.getFluidAmount() > 0) {
            directions.forEach(direction -> {
                EPipeState pipeState = getBlockState().getValue(getPropertyForDirection(direction));
                if (pipeState == EPipeState.CONNECT || pipeState == EPipeState.OUTPUT) {
                    transferFluidTo(direction);
                }
            });
        }
    }

    private void transferFluidTo(Direction direction) {
        BlockPos targetPos = getBlockPos().relative(direction);
        BlockEntity targetBE = level.getBlockEntity(targetPos);
        if (targetBE != null) {
            targetBE.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite())
                    .ifPresent(targetHandler -> {
                        FluidStack fluidToTransfer = fluidTank.getFluid().copy();
                        int filledAmount = targetHandler.fill(fluidToTransfer, IFluidHandler.FluidAction.SIMULATE);
                        if (filledAmount > 0) {
                            FluidStack drained = fluidTank.drain(filledAmount, IFluidHandler.FluidAction.EXECUTE);
                            targetHandler.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                        }
                    });
        }
    }

    private void pullFluidFrom(Direction direction) {
        BlockPos sourcePos = getBlockPos().relative(direction);
        BlockEntity sourceBE = level.getBlockEntity(sourcePos);
        if (sourceBE != null) {
            sourceBE.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite())
                    .ifPresent(sourceHandler -> {
                        int space = fluidTank.getCapacity() - fluidTank.getFluidAmount();
                        if (space > 0) {
                            FluidStack drained = sourceHandler.drain(space, IFluidHandler.FluidAction.SIMULATE);
                            if (!drained.isEmpty()) {
                                int filled = fluidTank.fill(drained.copy(), IFluidHandler.FluidAction.EXECUTE);
                                sourceHandler.drain(new FluidStack(drained.getFluid(), filled),
                                        IFluidHandler.FluidAction.EXECUTE);
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
