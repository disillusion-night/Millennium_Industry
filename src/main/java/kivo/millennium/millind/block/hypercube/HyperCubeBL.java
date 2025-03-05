package kivo.millennium.millind.block.hypercube;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class HyperCubeBL extends BaseEntityBlock {

    public HyperCubeBL(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public abstract BlockEntity newBlockEntity(BlockPos pPos, BlockState pState);
}
