package kivo.millennium.millind.pipe.client;

import kivo.millennium.millind.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

import static kivo.millennium.millind.pipe.client.EPipeState.getPropertyForDirection;

public class IronPipeBlock extends AbstractPipeBL{

    private static final double PIPE_WIDTH = .5;

    public IronPipeBlock() {
        super(Properties.of().strength(2.0f));
    }

    @Override
    public double getDefaultWidth() {
        return PIPE_WIDTH;
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

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new FluidPipeBE(pPos, pState);
    }

    @Override
    protected BlockEntityType<FluidPipeBE> blockEntityType() {
        return MillenniumBlockEntities.FLUID_PIPE_BE.get();
    }
}