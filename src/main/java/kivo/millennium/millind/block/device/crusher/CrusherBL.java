package kivo.millennium.millind.block.device.crusher;

import kivo.millennium.millind.block.device.AbstractMachineBE;
import kivo.millennium.millind.block.laser.HorizontalDeviceBL;
import kivo.millennium.millind.container.Device.CrusherContainer;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CrusherBL extends HorizontalDeviceBL {

    public CrusherBL() {
        super(Properties.of().lightLevel(state -> {
            if(state.getValue(POWERED)) return 15;
            return 0;
        }));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CrusherBE(pPos, pState);
    }

    @Override
    protected BlockEntityType<? extends AbstractMachineBE> blockEntityType() {
        return MillenniumBlockEntities.Crusher_BE.get();
    }

    @Nullable
    @Override
    protected AbstractContainerMenu createContainerMenu(int containerId, Inventory playerInventory, BlockPos pos, Player player) {
        return new CrusherContainer(containerId, player, pos);
    }
}