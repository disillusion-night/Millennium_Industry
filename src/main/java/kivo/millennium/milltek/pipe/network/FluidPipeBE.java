package kivo.millennium.milltek.pipe.network;

import kivo.millennium.milltek.init.MillenniumBlockEntities;
import kivo.millennium.milltek.init.MillenniumBlocks;
import kivo.millennium.milltek.init.MillenniumLevelNetworkType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class FluidPipeBE extends PipeBE<FluidPipeNetwork> {
    public FluidPipeBE(BlockPos pos, BlockState state) {
        super(MillenniumBlockEntities.FLUID_PIPE_BE.get(), MillenniumLevelNetworkType.FLUID_PIPE_NETWORK.get(), pos, state, 10000);
    }

    @Override
    protected FluidPipeBL getBlock() {
        return MillenniumBlocks.FLUID_PIPE.get();
    }

    @Override
    protected Capability<?> getCapabilityType() {
        return ForgeCapabilities.FLUID_HANDLER;
    }
}

