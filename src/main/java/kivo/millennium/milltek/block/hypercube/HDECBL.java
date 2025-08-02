package kivo.millennium.milltek.block.hypercube;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class HDECBL extends HyperCubeBL {
    public HDECBL() {
        super(Properties.of().noOcclusion().destroyTime(20));
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new HDECBE(pPos, pState);
    }
}
