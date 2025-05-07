package kivo.millennium.milltek.machine.PipeBooster;

import kivo.millennium.milltek.block.device.AbstractMachineBE;
import kivo.millennium.milltek.capability.CapabilityCache;
import kivo.millennium.milltek.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PipeBoosterBE extends AbstractMachineBE {
    public PipeBoosterBE(BlockPos pWorldPosition, BlockState pBlockState) {
        super(MillenniumBlockEntities.PIPE_BOOSTER_BE.get(), pWorldPosition, pBlockState, new CapabilityCache.Builder().withEnergy(114514, 4000));
    }

    @Override
    public void tickServer() {

    }

    @Override
    public boolean isWorking() {
        return false;
    }
}
