package kivo.millennium.millind.block.multiblock.controller;

import kivo.millennium.millind.multiblock.MultiBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BaseIOBlock implements EntityBlock, IMultiPart {
    protected MultiBlock multiBlock;

    public void setMultiBlock(MultiBlock multiBlock) {
        this.multiBlock = multiBlock;
    }

    public MultiBlock getMultiBlock() {
        return multiBlock;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return null;
    }

    @Override
    public boolean isConnected() {
        return this.multiBlock != null;
    }
}
