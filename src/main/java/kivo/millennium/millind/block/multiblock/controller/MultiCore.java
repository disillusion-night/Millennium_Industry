package kivo.millennium.millind.block.multiblock.controller;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

public abstract class MultiCore extends BlockEntity {
    public MultiCore(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }


    @Override
    public abstract  <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side);

    @Override
    public abstract void load(CompoundTag tag);

    @Override
    public abstract void saveAdditional(CompoundTag tag);

    @Override
    public CompoundTag getUpdateTag() {
        // 实现标签更新，用于同步数据给客户端
        return this.saveWithoutMetadata();
    }
}
