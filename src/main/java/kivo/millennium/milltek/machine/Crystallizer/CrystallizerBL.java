package kivo.millennium.milltek.machine.Crystallizer;

import kivo.millennium.milltek.block.laser.HorizontalMachineBL;
import kivo.millennium.milltek.container.Device.CrystallizerMenu;
import kivo.millennium.milltek.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CrystallizerBL extends HorizontalMachineBL<CrystallizerBE> {
    public CrystallizerBL() {
        super(Properties.of().destroyTime(40.0F).sound(SoundType.METAL).lightLevel(STATE_TO_LIGHT_LEVEL_FUNCTION));
    }

    @Override
    public CrystallizerBE newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CrystallizerBE(pPos, pState);
    }

    @Override
    protected BlockEntityType<CrystallizerBE> blockEntityType() {
        return MillenniumBlockEntities.CRYSTALLIZER_BE.get();
    }

    @Override
    protected AbstractContainerMenu createContainerMenu(int containerId, Inventory playerInventory, BlockPos pos, Player player) {
        return new CrystallizerMenu(containerId, player, pos);
    }
}
