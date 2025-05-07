package kivo.millennium.milltek.machine.Crusher;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import kivo.millennium.milltek.block.laser.HorizontalMachineBL;
import kivo.millennium.milltek.container.Device.CrusherMenu;
import kivo.millennium.milltek.init.MillenniumBlockEntities;

public class CrusherBL extends HorizontalMachineBL<CrusherBE> {

    public CrusherBL() {
        super(Properties.of().lightLevel(STATE_TO_LIGHT_LEVEL_FUNCTION));
    }

    @Override
    public CrusherBE newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CrusherBE(pPos, pState);
    }

    @Override
    protected BlockEntityType<CrusherBE> blockEntityType() {
        return MillenniumBlockEntities.Crusher_BE.get();
    }

    @Nullable
    @Override
    protected AbstractContainerMenu createContainerMenu(int containerId, Inventory playerInventory, BlockPos pos, Player player) {
        return new CrusherMenu(containerId, player, pos);
    }
}