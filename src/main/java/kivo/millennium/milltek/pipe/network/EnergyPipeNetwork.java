package kivo.millennium.milltek.pipe.network;

import java.util.UUID;

import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import kivo.millennium.milltek.init.MillenniumLevelNetworkType;
import static kivo.millennium.milltek.pipe.network.EPipeState.CONNECT;
import static kivo.millennium.milltek.pipe.network.EPipeState.PULL;
import static kivo.millennium.milltek.pipe.network.EPipeState.PUSH;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyPipeNetwork extends AbstractLevelNetwork implements ICapabilityProvider {
    // 可变容量能量存储
    private final VariableEnergyStorage energyStorage;
    private final LazyOptional<IEnergyStorage> energyHandlerLazyOptional;
    private static final boolean DEBUG_TICK_LOG = true; // 控制是否打印调试信息
    private static final Logger logger = LogUtils.getLogger();

    // 新建网络（分配新UUID）
    public EnergyPipeNetwork(UUID uuid) {
        super(MillenniumLevelNetworkType.ENERGY_PIPE_NETWORK.get(), uuid);
        this.energyStorage = new VariableEnergyStorage(100000); // 默认容量
        this.energyHandlerLazyOptional = LazyOptional.of(() -> energyStorage);
    }

    // NBT反序列化
    public EnergyPipeNetwork(CompoundTag tag) {
        super(MillenniumLevelNetworkType.ENERGY_PIPE_NETWORK.get(), tag);
        int capacity = tag.contains("capacity") ? tag.getInt("capacity") : 100000;
        this.energyStorage = new VariableEnergyStorage(capacity);
        this.energyStorage.deserializeNBT(tag.getCompound("energy"));
        this.energyHandlerLazyOptional = LazyOptional.of(() -> energyStorage);
    }

    public VariableEnergyStorage getEnergyStorage() {
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
        tag.putInt("capacity", energyStorage.getMaxEnergyStored());
    }

    @Override
    public boolean canMerge(AbstractLevelNetwork other) {
        return other instanceof EnergyPipeNetwork;
    }

    @Override
    public void handleNetworkInput(ServerLevel level) {
        for (BlockPos pos : pipeDataHashMap.keySet()) {
            PipeData pipeData = pipeDataHashMap.get(pos);


            // 处理六个方向的能量输入逻辑
            for (Direction direction : Direction.values()) {
                if (pipeData.getStateFromDirection(direction) != PULL) {
                    continue;
                }
                BlockEntity blockEntity = level.getBlockEntity(pos.relative(direction));
                if (blockEntity != null) {
                    LazyOptional<IEnergyStorage> energyCap = blockEntity.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite());
                    energyCap.ifPresent(energyStorage -> {
                        if (energyStorage.getEnergyStored() > 0) {
                            int energyToReceive = Math.min(energyStorage.getEnergyStored(), pipeData.maxInputPerTick);
                            int receivedEnergy = this.energyStorage.receiveEnergy(energyToReceive, false);
                            if (receivedEnergy > 0) {
                                energyStorage.extractEnergy(receivedEnergy, false);
                                if (DEBUG_TICK_LOG) {
                                    logger.info("[EnergyPipeNetwork] Received " + receivedEnergy + " energy from " + direction + " at " + pos);
                                }
                            }
                        }
                    });

                }
            }
        }
    }

    @Override
    public void handleNetworkOutput(ServerLevel level) {
        for (BlockPos pos : pipeDataHashMap.keySet()) {
            PipeData pipeData = pipeDataHashMap.get(pos);

            // 处理六个方向的能量输出逻辑
            for (Direction direction : Direction.values()) {
                if (pipeData.getStateFromDirection(direction) != PUSH && pipeData.getStateFromDirection(direction) != CONNECT) {
                    continue;
                }
                BlockEntity blockEntity = level.getBlockEntity(pos.relative(direction));
                if (blockEntity instanceof PipeBE<?>) continue;
                if (blockEntity != null) {
                    //打印发现的实体
                    if (DEBUG_TICK_LOG) {
                        logger.info("[EnergyPipeNetwork] Found block entity at " + pos.relative(direction) + ": " + blockEntity);
                    }
                    LazyOptional<IEnergyStorage> energyCap = blockEntity.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite());
                    energyCap.ifPresent(energyStorage -> {
                        if (this.energyStorage.getEnergyStored() > 0) {
                            int energyToExtract = Math.min(this.energyStorage.getEnergyStored(), 100000);
                            int extractedEnergy = energyStorage.receiveEnergy(energyToExtract, false);
                            if (extractedEnergy > 0) {
                                this.energyStorage.extractEnergy(extractedEnergy, false);
                                if (DEBUG_TICK_LOG) {
                                    logger.info("[EnergyPipeNetwork] Sent " + extractedEnergy + " energy to " + direction + " at " + pos);
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public EnergyPipeNetwork merge(ServerLevel level, AbstractLevelNetwork other) {
        return performMerge(level, (EnergyPipeNetwork) other);
    }

    @Override
    protected void mergeCapabilities(AbstractLevelNetwork other) {
        if (!(other instanceof EnergyPipeNetwork energyNetwork)) {
            logger.warn("[EnergyPipeNetwork] Cannot merge capabilities with non-energy network: " + other.getUUID());
            return;
        }
        
        // 合并能量存储数据
        int totalCapacity = Math.max(this.energyStorage.getMaxEnergyStored(), energyNetwork.energyStorage.getMaxEnergyStored());
        int totalEnergy = this.energyStorage.getEnergyStored() + energyNetwork.energyStorage.getEnergyStored();
        
        this.energyStorage.setCapacity(totalCapacity);
        // 通过接收能量的方式设置总能量
        this.energyStorage.extractEnergy(this.energyStorage.getEnergyStored(), false); // 清空当前能量
        this.energyStorage.receiveEnergy(Math.min(totalEnergy, totalCapacity), false); // 设置新能量
        
        if (logger.isDebugEnabled()) {
            logger.debug("[EnergyPipeNetwork] Merged energy storage: capacity={}, energy={}", totalCapacity, Math.min(totalEnergy, totalCapacity));
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyHandlerLazyOptional.cast();
        }
        return LazyOptional.empty();
    }

    /**
     * 可变容量、可序列化的能量存储实现
     */
    public static class VariableEnergyStorage extends EnergyStorage {
        private int customCapacity;

        public VariableEnergyStorage(int capacity) {
            super(capacity);
            this.customCapacity = capacity;
        }

        public VariableEnergyStorage(int capacity, int maxTransfer) {
            super(capacity, maxTransfer);
            this.customCapacity = capacity;
        }

        public VariableEnergyStorage(int capacity, int maxReceive, int maxExtract) {
            super(capacity, maxReceive, maxExtract);
            this.customCapacity = capacity;
        }

        public void setCapacity(int capacity) {
            if (capacity <= 0) {
                throw new IllegalArgumentException("Capacity must be positive");
            }
            
            this.customCapacity = capacity;
            // 如果当前能量超过新容量，裁剪到新容量
            if (this.energy > capacity) {
                this.energy = capacity;
            }
        }

        @Override
        public int getMaxEnergyStored() {
            return this.customCapacity;
        }

        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("energy", this.getEnergyStored());
            tag.putInt("capacity", this.customCapacity);
            tag.putInt("maxReceive", this.maxReceive);
            tag.putInt("maxExtract", this.maxExtract);
            return tag;
        }

        public void deserializeNBT(CompoundTag tag) {
            int energy = tag.getInt("energy");
            this.customCapacity = tag.getInt("capacity");
            this.maxReceive = tag.contains("maxReceive") ? tag.getInt("maxReceive") : this.maxReceive;
            this.maxExtract = tag.contains("maxExtract") ? tag.getInt("maxExtract") : this.maxExtract;
            // 设置能量值
            this.energy = Math.min(energy, this.customCapacity);
        }
    }
}
