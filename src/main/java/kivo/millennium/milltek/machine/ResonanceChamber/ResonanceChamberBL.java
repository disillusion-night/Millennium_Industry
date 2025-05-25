package kivo.millennium.milltek.machine.ResonanceChamber;

import kivo.millennium.milltek.block.laser.HorizontalMachineBL;
import kivo.millennium.milltek.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ResonanceChamberBL extends HorizontalMachineBL<ResonanceChamberBE> {
    public ResonanceChamberBL() {
        super(Properties.of().destroyTime(40.0F).sound(SoundType.METAL).lightLevel(STATE_TO_LIGHT_LEVEL_FUNCTION));
    }

    @Override
    public ResonanceChamberBE newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ResonanceChamberBE(pPos, pState);
    }

    @Override
    protected BlockEntityType<ResonanceChamberBE> blockEntityType() {
        return MillenniumBlockEntities.RESONANCE_CHAMBER_BE.get();
    }

    @Override
    protected AbstractContainerMenu createContainerMenu(int containerId, Inventory playerInventory, BlockPos pos, Player player) {
        return new ResonanceChamberMenu(containerId, player, pos);
    }
}
