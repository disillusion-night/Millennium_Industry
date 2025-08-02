package kivo.millennium.milltek.block.laser;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class NetherStarLaserBL extends BaseLaserBL{
    public NetherStarLaserBL() {
        super(Properties.of().noOcclusion().destroyTime(40.0F).lightLevel(blockstate -> {
            if (blockstate.getValue(WORKING)) return 15;
            else return 0;
        }));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new NetherStarLaserBE(pPos, pState);
    }
}
