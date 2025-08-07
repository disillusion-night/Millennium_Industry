package kivo.millennium.milltek.pipe.network;

import kivo.millennium.milltek.init.MillenniumBlockEntities;
import kivo.millennium.milltek.init.MillenniumBlocks;
import kivo.millennium.milltek.init.MillenniumLevelNetworkType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import kivo.millennium.milltek.storage.MillenniumGasStorage;
import kivo.millennium.milltek.init.MillenniumCapabilities;

public class GasPipeBE extends PipeBE<GasPipeNetwork> {
    public GasPipeBE(BlockPos pos, BlockState state) {
        super(MillenniumBlockEntities.GAS_PIPE_BE.get(), MillenniumLevelNetworkType.GAS_PIPE_NETWORK.get(), pos, state, 100000);
    }

    @Override
    protected GasPipeBL getBlock() {
        return MillenniumBlocks.GAS_PIPE.get();
    }

    @Override
    public Capability<?> getCapabilityType() {
        return MillenniumCapabilities.GAS;
    }
}

