package kivo.millennium.millind.pipe.client;

import kivo.millennium.millind.init.MillenniumLevelNetwork;
import kivo.millennium.millind.pipe.client.network.FluidLevelNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import static kivo.millennium.millind.pipe.client.EPipeState.getPropertyForDirection;

public class IronPipeBlock extends AbstractPipeBL<FluidLevelNetwork> {

    private static final double PIPE_WIDTH = .5;

    public IronPipeBlock() {
        super(Properties.of().strength(2.0f));
    }

    @Override
    public double getDefaultWidth() {
        return PIPE_WIDTH;
    }

    @Override
    public MillenniumLevelNetwork.LevelNetworkType<FluidLevelNetwork> getAssociatedNetworkType() {
        return MillenniumLevelNetwork.FLUID_LEVEL_NETWORK.get();
    }

    @Override
    protected boolean connectionTest(BlockGetter level, BlockPos neighborPos, BlockState neighborState, Direction facing) {
        if (neighborState.getBlock() instanceof IronPipeBlock && neighborState.getValue(getPropertyForDirection(facing.getOpposite())) != EPipeState.DISCONNECTED) {
            return true;
        }
        BlockEntity be = level.getBlockEntity(neighborPos);
        if(be != null) {
            return be.getCapability(ForgeCapabilities.FLUID_HANDLER, facing.getOpposite()).isPresent();
        } else {
            return false;
        }
    }

    @Override
    public boolean isSamePipe(Block target) {
        return target instanceof IronPipeBlock;
    }
}