package kivo.millennium.millind.block.laser;

import kivo.millennium.millind.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class NetherStarLaserBE extends BaseLaserBE{

    public NetherStarLaserBE(BlockPos pPos, BlockState pBlockState) {
        super(MillenniumBlockEntities.NETHER_STAR_LASER_BE.get(), pPos, pBlockState);
    }


}
