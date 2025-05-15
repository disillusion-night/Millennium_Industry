package kivo.millennium.milltek.pipe.client;

import kivo.millennium.milltek.init.MillenniumBlockEntities;
import kivo.millennium.milltek.init.MillenniumLevelNetworkType;
import kivo.millennium.milltek.pipe.client.network.FluidPipeNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

import java.util.List;

public class CopperPipeBE extends PipeBE<FluidPipeNetwork> {
    public CopperPipeBE(BlockPos pPos, BlockState pBlockState) {
        super(MillenniumBlockEntities.COPPER_PIPE_BE.get(), MillenniumLevelNetworkType.FLUID_PIPE_NETWORK.get(), pPos,
                pBlockState);
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
        if (level == null || level.isClientSide)
            return;
        int perNode = maxInputPerTick / inputTargets.size();
        // 计算每个节点的输入量

        for (IOEntry entry : inputTargets) {
            entry.be.getCapability(ForgeCapabilities.FLUID_HANDLER, entry.direction.getOpposite())
                    .ifPresent(handler -> {
                        var drained = handler.drain(perNode, FluidAction.SIMULATE);
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
        if (level == null || level.isClientSide)
            return;
        int perNode = maxOutputPerTick / outputTargets.size();
        // 计算每个节点的输出量
        for (IOEntry entry : outputTargets) {
            entry.be.getCapability(ForgeCapabilities.FLUID_HANDLER, entry.direction.getOpposite())
                    .ifPresent(handler -> {
                        var toSend = network.getTank().drain(perNode, FluidAction.SIMULATE);
                        if (!toSend.isEmpty()) {
                            int accepted = handler.fill(toSend, FluidAction.EXECUTE);
                            if (accepted > 0) {
                                network.getTank().drain(accepted, FluidAction.EXECUTE);
                            }
                        }
                    });
        }
    }
}
