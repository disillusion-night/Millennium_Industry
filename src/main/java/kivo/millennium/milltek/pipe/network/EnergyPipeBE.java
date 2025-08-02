package kivo.millennium.milltek.pipe.network;

import java.util.List;

import kivo.millennium.milltek.init.MillenniumBlockEntities;
import kivo.millennium.milltek.init.MillenniumBlocks;
import kivo.millennium.milltek.init.MillenniumLevelNetworkType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraft.world.level.Level;

public class EnergyPipeBE extends PipeBE<EnergyPipeNetwork> {
    public EnergyPipeBE(BlockPos pos, BlockState state) {
        super(MillenniumBlockEntities.ENERGY_PIPE_BE.get(), MillenniumLevelNetworkType.ENERGY_PIPE_NETWORK.get(), pos,
                state, 10000);
    }


    @Override
    protected EnergyPipeBL getBlock() {
        return MillenniumBlocks.ENERGY_PIPE.get();
    }

    @Override
    protected Capability<?> getCapabilityType() {
        return ForgeCapabilities.ENERGY;
    }
}
