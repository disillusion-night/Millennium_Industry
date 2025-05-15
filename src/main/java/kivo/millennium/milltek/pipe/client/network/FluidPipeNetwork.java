package kivo.millennium.milltek.pipe.client.network;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import kivo.millennium.milltek.init.MillenniumLevelNetworkType;

import java.util.UUID;

import javax.annotation.Nullable;

public class FluidPipeNetwork extends AbstractLevelNetwork implements ICapabilityProvider {
    private final FluidTank tank;
    private final LazyOptional<IFluidHandler> fluidHandlerLazyOptional;

    // 用于新建网络（分配新UUID）
    public FluidPipeNetwork(UUID uuid) {
        super(MillenniumLevelNetworkType.FLUID_PIPE_NETWORK.get(), uuid);
        this.tank = new FluidTank(100000);
        this.fluidHandlerLazyOptional = LazyOptional.of(() -> tank);
    }

    // 用于NBT反序列化
    public FluidPipeNetwork(CompoundTag tag) {
        super(MillenniumLevelNetworkType.FLUID_PIPE_NETWORK.get(), tag);
        this.tank = new FluidTank(tag.contains("capacity") ? tag.getInt("capacity") : 100000);
        tank.readFromNBT(tag.getCompound("fluid"));
        this.fluidHandlerLazyOptional = LazyOptional.of(() -> tank);
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

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandlerLazyOptional.cast();
        }
        return LazyOptional.empty();
    }
}