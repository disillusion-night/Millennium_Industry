package kivo.millennium.millind.block.inductionFurnace;

import kivo.millennium.millind.block.AbstractDeviceBL;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;

public class InductionFurnaceBL extends AbstractDeviceBL {
    protected InductionFurnaceBL() {
        super(Properties.of().destroyTime(40.0F).sound(SoundType.METAL).lightLevel(blockState -> {
            if(blockState.getValue(POWERED)){
                return 15;
            }else {
                return 0;
            }
        }));
    }

    @Override
    protected void openContainer(Level pLevel, BlockPos pPos, Player pPlayer) {

    }
}
