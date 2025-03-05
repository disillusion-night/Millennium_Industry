package kivo.millennium.millind.block.laser;

import kivo.millennium.millind.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class NetherStarLaserBL extends BaseLaserBL{
    public NetherStarLaserBL() {
        super(Properties.of().noOcclusion().destroyTime(40.0F));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new NetherStarLaserBE(pPos, pState);
    }

    @javax.annotation.Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createLaserTicker(pLevel, pBlockEntityType, MillenniumBlockEntities.NETHER_STAR_LASER_BE.get());
    }

    @Override
    public void setPowered(Level pLevel, BlockPos pPos, BlockState pState, boolean powered) {

    }
}
