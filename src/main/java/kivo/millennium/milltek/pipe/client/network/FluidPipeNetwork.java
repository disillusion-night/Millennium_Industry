package kivo.millennium.milltek.pipe.client.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import kivo.millennium.milltek.init.MillenniumLevelNetworkType;
import kivo.millennium.milltek.init.MillenniumLevelNetworkType.LevelNetworkType;

import java.util.UUID;

public class FluidPipeNetwork extends AbstractLevelNetwork {
    private final FluidTank tank;

    // 用于新建网络（分配新UUID）
    public FluidPipeNetwork(UUID uuid) {
        super(MillenniumLevelNetworkType.FLUID_PIPE_NETWORK.get(), uuid);
        this.tank = new FluidTank(100000);
    }

    // 用于NBT反序列化
    public FluidPipeNetwork(CompoundTag tag) {
        super(MillenniumLevelNetworkType.FLUID_PIPE_NETWORK.get(), tag);
        this.tank = new FluidTank(tag.contains("capacity") ? tag.getInt("capacity") : 100000);
        tank.readFromNBT(tag.getCompound("fluid"));
    }

    public FluidTank getTank() {
        return tank;
    }

    public void setCapacity(int capacity) {
        tank.setCapacity(capacity);
        setDirty();
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.put("fluid", tank.writeToNBT(new CompoundTag()));
        tag.putInt("capacity", tank.getCapacity());
    }

    @Override
    public FluidPipeNetwork readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        tank.readFromNBT(tag.getCompound("fluid"));
        tank.setCapacity(tag.getInt("capacity"));
        return this;
    }
}