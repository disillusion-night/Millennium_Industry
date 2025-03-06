package kivo.millennium.millind.block.laser;

import kivo.millennium.millind.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class NetherStarLaserBE extends BaseLaserBE{

    public NetherStarLaserBE(BlockPos pPos, BlockState pBlockState) {
        super(MillenniumBlockEntities.NETHER_STAR_LASER_BE.get(), pPos, pBlockState);
    }


}
