package kivo.millennium.milltek.pipe.client.network;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import kivo.millennium.milltek.init.MillenniumLevelNetworkType;

import java.util.UUID;

import javax.annotation.Nullable;

public class EnergyPipeNetwork extends AbstractLevelNetwork implements ICapabilityProvider {
    // 可变容量能量存储
    private final VariableEnergyStorage energyStorage;
    private final LazyOptional<IEnergyStorage> energyHandlerLazyOptional;

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
    public EnergyPipeNetwork readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        energyStorage.deserializeNBT(tag.getCompound("energy"));
        energyStorage.setCapacity(tag.getInt("capacity"));
        return this;
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
            this.customCapacity = capacity;
            // 迁移能量到新容量
            int energy = this.getEnergyStored();
            if (energy > capacity) {
                this.receiveEnergy(capacity - energy, false); // 直接裁剪
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
            // 通过反射或重建对象设置能量（此处假设有setEnergy方法，否则需重建对象）
            try {
                java.lang.reflect.Field f = EnergyStorage.class.getDeclaredField("energy");
                f.setAccessible(true);
                f.setInt(this, Math.min(energy, this.customCapacity));
            } catch (Exception e) {
                // ignore
            }
        }
    }
}
