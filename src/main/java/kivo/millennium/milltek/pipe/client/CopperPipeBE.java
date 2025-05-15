package kivo.millennium.milltek.pipe.client;

import kivo.millennium.milltek.init.MillenniumBlockEntities;
import kivo.millennium.milltek.init.MillenniumBlocks;
import kivo.millennium.milltek.init.MillenniumLevelNetworkType;
import kivo.millennium.milltek.pipe.client.network.FluidPipeNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

import javax.annotation.Nullable;
import java.util.List;

public class CopperPipeBE extends PipeBE<FluidPipeNetwork> {
    public CopperPipeBE(BlockPos pPos, BlockState pBlockState) {
        super(MillenniumBlockEntities.COPPER_PIPE_BE.get(), MillenniumLevelNetworkType.FLUID_PIPE_NETWORK.get(), pPos, pBlockState);
    }

    @Override
    protected FluidPipeNetwork getNetwork() {
        return super.getNetwork();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
    }

    @Override
    protected void handleInputToNetwork(FluidPipeNetwork network, List<IOEntry> inputTargets) {
        if (inputTargets.isEmpty()) return;
        int perNode = maxInputPerTick / inputTargets.size();
        for (IOEntry entry : inputTargets) {
            entry.be.getCapability(ForgeCapabilities.FLUID_HANDLER, entry.direction.getOpposite())
                    .ifPresent(handler -> {
                        FluidStack drained = handler.drain(perNode, FluidAction.SIMULATE);
                        if (!drained.isEmpty()) {
                            int filled = network.getTank().fill(drained, FluidAction.EXECUTE);
                            if (filled > 0) {
                                handler.drain(filled, FluidAction.EXECUTE);
                            }
                        }
                    });
        }
    }

    @Override
    protected void handleOutputFromNetwork(FluidPipeNetwork network, List<IOEntry> outputTargets) {
        if (outputTargets.isEmpty()) return;
        int perNode = maxOutputPerTick / outputTargets.size();
        for (IOEntry entry : outputTargets) {
            entry.be.getCapability(ForgeCapabilities.FLUID_HANDLER, entry.direction.getOpposite())
                    .ifPresent(handler -> {
                        FluidStack toSend = network.getTank().drain(perNode, FluidAction.SIMULATE);
                        if (!toSend.isEmpty()) {
                            int accepted = handler.fill(toSend, FluidAction.EXECUTE);
                            if (accepted > 0) {
                                network.getTank().drain(accepted, FluidAction.EXECUTE);
                            }
                        }
                    });
        }
    }

    @Override
    protected void redistributeNetworkContent(List<FluidPipeNetwork> newNetworks, List<List<PipeBE<FluidPipeNetwork>>> groups) {
        // 简单策略：将原网络的流体平均分配到新网络
        FluidPipeNetwork oldNetwork = getNetwork();
        if (oldNetwork == null || newNetworks.isEmpty()) return;

        int totalFluid = oldNetwork.getTank().getFluidAmount();
        int perNetwork = totalFluid / newNetworks.size();
        int remaining = totalFluid % newNetworks.size();

        for (int i = 0; i < newNetworks.size(); i++) {
            FluidPipeNetwork net = newNetworks.get(i);
            int toFill = perNetwork + (i == 0 ? remaining : 0); // 剩余流体给第一个网络
            if (toFill > 0) {
                FluidStack stack = oldNetwork.getTank().getFluid().copy();
                stack.setAmount(toFill);
                net.getTank().fill(stack, FluidAction.EXECUTE);
            }
        }
        // 清空原网络流体
        oldNetwork.getTank().drain(totalFluid, FluidAction.EXECUTE);
    }

    @Override
    public <T1> LazyOptional<T1> getCapability(Capability<T1> cap, @Nullable Direction side) {
        if (getNetwork() != null) {
            if (cap == ForgeCapabilities.FLUID_HANDLER) {
                // 返回网络的流体能力
                return getNetwork().getCapability(cap, side);
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected Block getBlock() {
        return MillenniumBlocks.COPPER_PIPE.get();
    }
}
