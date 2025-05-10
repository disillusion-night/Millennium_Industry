package kivo.millennium.milltek.pipe.client.network;

import kivo.millennium.milltek.init.MillenniumLevelNetworkType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class FluidPipeNetwork extends AbstractLevelNetwork {
    private final FluidTank sharedTank;

    public FluidPipeNetwork(int id) {
        super(MillenniumLevelNetworkType.FLUID_PIPE_NETWORK.get(), id);
        this.sharedTank = new FluidTank(100000); // 共享流体存储池
    }

    public FluidTank getSharedTank() {
        return sharedTank;
    }

    @Override
    protected void tickServer(Level level) {
        // 从输入目标吸取流体
        for (AbstractNetworkTarget input : inputs) {
            if (input instanceof BlockEntityNetworkTarget target) {
                IFluidHandler handler = target.getFluidStorage(level);
                if (handler != null) {
                    FluidStack drained = handler.drain(sharedTank.getCapacity() - sharedTank.getFluidAmount(), IFluidHandler.FluidAction.EXECUTE);
                    sharedTank.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                }
            }
        }

        // 将流体输出到输出目标
        for (AbstractNetworkTarget output : outputs) {
            if (output instanceof BlockEntityNetworkTarget target) {
                IFluidHandler handler = target.getFluidStorage(level);
                if (handler != null) {
                    FluidStack toTransfer = sharedTank.drain(sharedTank.getFluidAmount(), IFluidHandler.FluidAction.SIMULATE);
                    int filled = handler.fill(toTransfer, IFluidHandler.FluidAction.EXECUTE);
                    sharedTank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                }
            }
        }
    }

    @Override
    public void writeToNBT(CompoundTag compoundTag) {
        super.writeToNBT(compoundTag);
        compoundTag.put("sharedTank", sharedTank.writeToNBT(new CompoundTag()));
    }

    @Override
    public AbstractLevelNetwork readFromNBT(CompoundTag compoundTag) {
        compoundTag = compoundTag.getCompound("sharedTank");
        sharedTank.readFromNBT(compoundTag);
        return super.readFromNBT(compoundTag);
    }
}
