package kivo.millennium.millind.block.laser;

import kivo.millennium.millind.block.device.AbstractDeviceBE;
import kivo.millennium.millind.block.device.AbstractDeviceBL;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SolarGeneratorBL extends AbstractDeviceBL {
    public SolarGeneratorBL() {
        super(Properties.of());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SolarGeneratorBE(pPos, pState);
    }

    @Override
    protected BlockEntityType<? extends AbstractDeviceBE> blockEntityType() {
        return null;
    }

    @Override
    protected AbstractContainerMenu createContainerMenu(int containerId, Inventory playerInventory, BlockPos pos, Player player) {
        return null;
    }
}
