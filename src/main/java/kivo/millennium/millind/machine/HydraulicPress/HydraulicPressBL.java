package kivo.millennium.millind.machine.HydraulicPress;

import kivo.millennium.millind.block.device.AbstractMachineBE;
import kivo.millennium.millind.block.laser.HorizonalMachineBL;
import kivo.millennium.millind.container.Device.HydraulicPressMenu;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class HydraulicPressBL extends HorizonalMachineBL {

    public HydraulicPressBL() {
        super(Properties.of().lightLevel(state -> {
            if(state.getValue(WORKING)) return 15;
            return 0;
        }));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new HydraulicPressBE(pPos, pState);
    }

    @Override
    protected BlockEntityType<? extends AbstractMachineBE> blockEntityType() {
        return MillenniumBlockEntities.HYDRAULIC_PRESS_BE.get();
    }

    @Nullable
    @Override
    protected AbstractContainerMenu createContainerMenu(int containerId, Inventory playerInventory, BlockPos pos, Player player) {
        return new HydraulicPressMenu(containerId, player, pos);
    }
}