package kivo.millennium.millind.block.multiblock.controller;

import kivo.millennium.millind.init.MillenniumBlockEntities;
import kivo.millennium.millind.multiblock.MultiBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BaseIOBlockEntity extends BlockEntity {
    public BaseIOBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(MillenniumBlockEntities.HMI_BE.get(), pPos, pBlockState);
    }


    protected MultiBlock multiBlock;

    public void setMultiBlock(MultiBlock multiBlock) {
        this.multiBlock = multiBlock;
    }

    public MultiBlock getMultiBlock() {
        return multiBlock;
    }
}
