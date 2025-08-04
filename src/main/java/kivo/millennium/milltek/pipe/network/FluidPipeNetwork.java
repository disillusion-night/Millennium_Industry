package kivo.millennium.milltek.pipe.network;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import kivo.millennium.milltek.init.MillenniumLevelNetworkType;
import java.util.UUID;
import java.util.List;
import javax.annotation.Nullable;

import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;

public class FluidPipeNetwork extends AbstractLevelNetwork implements ICapabilityProvider {
    private final PipeFluidStorage fluidStorage;
    private final LazyOptional<IFluidHandler> fluidHandlerLazyOptional;
    private static final boolean DEBUG_TICK_LOG = false;
    private static final Logger logger = LogUtils.getLogger();

    public FluidPipeNetwork(UUID uuid) {
        super(MillenniumLevelNetworkType.FLUID_PIPE_NETWORK.get(), uuid);
        this.fluidStorage = new PipeFluidStorage(100000);
        this.fluidHandlerLazyOptional = LazyOptional.of(() -> fluidStorage);
    }

    public FluidPipeNetwork(CompoundTag tag) {
        super(MillenniumLevelNetworkType.FLUID_PIPE_NETWORK.get(), tag);
        this.fluidStorage = new PipeFluidStorage(tag.getCompound("fluid"));
        this.fluidHandlerLazyOptional = LazyOptional.of(() -> fluidStorage);
    }

    public PipeFluidStorage getFluidStorage() {
        return fluidStorage;
    }

    public void setCapacity(int capacity) {
        this.fluidStorage.setCapacity(capacity);
        setDirty();
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.put("fluid", fluidStorage.serializeNBT());
    }

    @Override
    public boolean canMerge(AbstractLevelNetwork other) {
        return other instanceof FluidPipeNetwork;
    }

    @Override
    protected void mergeCapabilities(AbstractLevelNetwork other) {
        if (other instanceof FluidPipeNetwork fluidPipeNetwork) {
            this.fluidStorage.merge(fluidPipeNetwork.getFluidStorage());
            if (DEBUG_TICK_LOG) {
                logger.info("[FluidPipeNetwork] Merged fluid storage from " + other.getUUID());
            }
        }
    }

    @Override
    protected void handleInput(ServerLevel level, List<TargetContext> inputTargets) {
        // 简单平均分配流体输入
        int n = inputTargets.size();
        if (n == 0) return;
        int totalCapacity = this.fluidStorage.getFluidInTank(0).getAmount();
        int avgCapacity = totalCapacity / n;
        for (TargetContext ctx : inputTargets) {
            BlockEntity blockEntity = level.getBlockEntity(ctx.pos.relative(ctx.direction));
            if (blockEntity != null) {
                LazyOptional<IFluidHandler> fluidCap = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, ctx.direction.getOpposite());
                fluidCap.ifPresent(fluidHandler -> {
                    int fluidToReceive = Math.min(ctx.pipeData.maxInputPerTick, avgCapacity);
                    int received = this.fluidStorage.fill(fluidHandler.drain(fluidToReceive, EXECUTE), EXECUTE);
                    if (DEBUG_TICK_LOG && received > 0) {
                        logger.info("[FluidPipeNetwork] Received " + received + "mb fluid from " + ctx.direction + " at " + ctx.pos);
                    }
                });
            }
        }
    }

    @Override
    protected void handleOutput(ServerLevel level, List<TargetContext> outputTargets) {
        int n = outputTargets.size();
        if (n == 0) return;
        int totalFluid = this.fluidStorage.getFluidInTank(0).getAmount();
        int avgFluid = totalFluid / n;
        for (TargetContext ctx : outputTargets) {
            BlockEntity blockEntity = level.getBlockEntity(ctx.pos.relative(ctx.direction));
            if (blockEntity instanceof PipeBE<?>) continue;
            if (blockEntity != null) {
                LazyOptional<IFluidHandler> fluidCap = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, ctx.direction.getOpposite());
                fluidCap.ifPresent(fluidHandler -> {
                    int fluidToSend = Math.min(ctx.pipeData.maxOutputPerTick, avgFluid);
                    int sent = fluidHandler.fill(new FluidStack(getFluidStorage().getFluidInTank(0).getFluid(), getFluidStorage().drain(fluidToSend, EXECUTE).getAmount()), EXECUTE);
                    if (DEBUG_TICK_LOG && sent > 0) {
                        logger.info("[FluidPipeNetwork] Sent " + sent + "mb fluid to " + ctx.direction + " at " + ctx.pos);
                    }
                });
            }
        }
    }
    @Override
    protected void distributeCapas(AbstractLevelNetwork subNetwork, float ratio) {
        if (!(subNetwork instanceof FluidPipeNetwork fluidSub)) return;
        // 只分配主存储第0槽的流体
        FluidStack mainFluid = this.fluidStorage.getFluidInTank(0);
        if (mainFluid.isEmpty() || ratio <= 0) return;
        int toMove = (int) (mainFluid.getAmount() * ratio);
        if (toMove <= 0) return;
        FluidStack extracted = this.fluidStorage.drain(toMove, EXECUTE);
        fluidSub.getFluidStorage().fill(extracted, EXECUTE);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandlerLazyOptional.cast();
        }
        return LazyOptional.empty();
    }
}
