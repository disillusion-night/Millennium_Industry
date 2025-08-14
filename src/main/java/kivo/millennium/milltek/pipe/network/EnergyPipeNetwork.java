package kivo.millennium.milltek.pipe.network;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import kivo.millennium.milltek.storage.PipeEnergyStorage;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import kivo.millennium.milltek.init.MillenniumLevelNetworkType;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyPipeNetwork extends AbstractLevelNetwork implements ICapabilityProvider {
    private final PipeEnergyStorage energyStorage;
    private final LazyOptional<IEnergyStorage> energyHandlerLazyOptional;
    private static final boolean DEBUG_TICK_LOG = false; // 控制是否打印调试信息
    private static final Logger logger = LogUtils.getLogger();

    // 新建网络（分配新UUID）
    public EnergyPipeNetwork(UUID uuid) {
        super(MillenniumLevelNetworkType.ENERGY_PIPE_NETWORK.get(), uuid);
        this.energyStorage = new PipeEnergyStorage(100000); // 默认容量
        this.energyHandlerLazyOptional = LazyOptional.of(() -> energyStorage);
    }

    // NBT反序列化
    public EnergyPipeNetwork(CompoundTag tag) {
        super(MillenniumLevelNetworkType.ENERGY_PIPE_NETWORK.get(), tag);
        this.energyStorage = new PipeEnergyStorage(tag.getCompound("energy"));
        this.energyHandlerLazyOptional = LazyOptional.of(() -> energyStorage);
    }

    public PipeEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public void setCapacity(int capacity) {
        this.energyStorage.setCapacity(capacity);
        setDirty();
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.put("energy", energyStorage.serializeNBT());
    }

    @Override
    public boolean canMerge(AbstractLevelNetwork other) {
        return other instanceof EnergyPipeNetwork;
    }

    @Override
    protected void mergeCapabilities(AbstractLevelNetwork other) {
        if (other instanceof EnergyPipeNetwork energyPipeNetwork) {
            this.energyStorage.merge(energyPipeNetwork.getEnergyStorage());
            if (DEBUG_TICK_LOG) {
                logger.info("[EnergyPipeNetwork] Merged energy storage from " + other.getUUID());
            }
        }
    }

    @Override
    protected void handleInput(List<TargetContext> inputTargets) {
        int n = inputTargets.size();
        if (n == 0) return;
        int totalCapacity = this.energyStorage.getMaxEnergyStored() - this.energyStorage.getEnergyStored();
        int avgCapacity = totalCapacity / n;
        for (TargetContext ctx : inputTargets) {
            BlockEntity blockEntity = getLevel().getBlockEntity(ctx.pos.relative(ctx.direction));
            if (blockEntity != null) {
                LazyOptional<IEnergyStorage> energyCap = blockEntity.getCapability(ForgeCapabilities.ENERGY, ctx.direction.getOpposite());
                energyCap.ifPresent(energyStorage -> {
                    if (energyStorage.getEnergyStored() > 0) {
                        int energyToReceive = Math.min(Math.min(energyStorage.getEnergyStored(), ctx.pipeData.maxInputPerTick), avgCapacity);
                        int receivedEnergy = this.energyStorage.receiveEnergy(energyToReceive, false);
                        if (receivedEnergy > 0) {
                            energyStorage.extractEnergy(receivedEnergy, false);
                            if (DEBUG_TICK_LOG) {
                                logger.info("[EnergyPipeNetwork] Received " + receivedEnergy + " energy from " + ctx.direction + " at " + ctx.pos);
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void handleOutput(List<TargetContext> outputTargets) {
        int n = outputTargets.size();
        if (n == 0) return;
        int totalEnergy = this.energyStorage.getEnergyStored();
        int avgEnergy = totalEnergy / n;
        for (TargetContext ctx : outputTargets) {
            BlockEntity blockEntity = getLevel().getBlockEntity(ctx.pos.relative(ctx.direction));
            if (blockEntity instanceof PipeBE<?>) continue;
            if (blockEntity != null) {
                if (DEBUG_TICK_LOG) {
                    logger.info("[EnergyPipeNetwork] Found block entity at " + ctx.pos.relative(ctx.direction) + ": " + blockEntity);
                }
                LazyOptional<IEnergyStorage> energyCap = blockEntity.getCapability(ForgeCapabilities.ENERGY, ctx.direction.getOpposite());
                energyCap.ifPresent(energyStorage -> {
                    if (this.energyStorage.getEnergyStored() > 0) {
                        int energyToExtract = Math.min(Math.min(this.energyStorage.getEnergyStored(), ctx.pipeData.maxOutputPerTick), avgEnergy);
                        int extractedEnergy = energyStorage.receiveEnergy(energyToExtract, false);
                        if (extractedEnergy > 0) {
                            this.energyStorage.extractEnergy(extractedEnergy, false);
                            if (DEBUG_TICK_LOG) {
                                logger.info("[EnergyPipeNetwork] Sent " + extractedEnergy + " energy to " + ctx.direction + " at " + ctx.pos);
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void distributeCapas(AbstractLevelNetwork subNetwork, float ratio) {
        if (!(subNetwork instanceof EnergyPipeNetwork energySub)) return;
        int mainEnergy = this.energyStorage.getEnergyStored();
        if (mainEnergy <= 0 || ratio <= 0) return;
        int toMove = (int) (mainEnergy * ratio);
        if (toMove <= 0) return;
        int extracted = this.energyStorage.extractEnergy(toMove, false);
        energySub.getEnergyStorage().receiveEnergy(extracted, false);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyHandlerLazyOptional.cast();
        }
        return LazyOptional.empty();
    }

}
