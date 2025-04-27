package kivo.millennium.millind.machine.HydraulicPress;

import kivo.millennium.millind.block.laser.HorizontalMachineBL;
import kivo.millennium.millind.container.Device.HydraulicPressMenu;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class HydraulicPressBL extends HorizontalMachineBL<HydraulicPressBE> {

    public HydraulicPressBL() {
        super(Properties.of().lightLevel(STATE_TO_LIGHT_LEVEL_FUNCTION));
    }

    @Override
    public HydraulicPressBE newBlockEntity(BlockPos pPos, BlockState pState) {
        return new HydraulicPressBE(pPos, pState);
    }

    @Override
    protected BlockEntityType<HydraulicPressBE> blockEntityType() {
        return MillenniumBlockEntities.HYDRAULIC_PRESS_BE.get();
    }

    @Nullable
    @Override
    protected AbstractContainerMenu createContainerMenu(int containerId, Inventory playerInventory, BlockPos pos, Player player) {
        return new HydraulicPressMenu(containerId, player, pos);
    }
}