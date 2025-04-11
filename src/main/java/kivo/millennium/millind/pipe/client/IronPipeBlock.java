package kivo.millennium.millind.pipe.client;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class IronPipeBlock extends AbstractPipeBL {

    private static final double PIPE_WIDTH = .5;

    public IronPipeBlock() {
        super(Properties.of().strength(2.0f));
    }

    @Override
    protected double getDefaultWidth() {
        return PIPE_WIDTH;
    }

    @Override
    protected boolean connectionTest(BlockState state, Direction facing) {
        // 检查相邻方块是否是我们的管道
        if (state.getBlock() instanceof AbstractPipeBL) {
            return true;
        }
        // 检查相邻方块是否具有非空的流体状态 (简化的液体储存能力判断)
        FluidState fluidState = state.getFluidState();
        return !fluidState.isEmpty() && fluidState.getType() != net.minecraft.world.level.material.Fluids.EMPTY;
    }
}