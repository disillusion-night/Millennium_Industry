package kivo.millennium.milltek.pipe.client;

import kivo.millennium.milltek.init.MillenniumBlockEntities;
import kivo.millennium.milltek.init.MillenniumLevelNetworkType;
import kivo.millennium.milltek.pipe.client.network.FluidPipeNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CopperPipeBE extends PipeBE<FluidPipeNetwork> {
    public CopperPipeBE(BlockPos pPos, BlockState pBlockState) {
        super(MillenniumBlockEntities.COPPER_PIPE_BE.get(), MillenniumLevelNetworkType.FLUID_PIPE_NETWORK.get(), pPos, pBlockState);
    }

    @Override
    protected FluidPipeNetwork getNetwork() {
        return super.getNetwork();
    }

    @Override
    protected void setPipeID(int id) {
        super.setPipeID(id);
    }

    @Override
    protected void connectToNetwork() {
        super.connectToNetwork();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
    }

}
