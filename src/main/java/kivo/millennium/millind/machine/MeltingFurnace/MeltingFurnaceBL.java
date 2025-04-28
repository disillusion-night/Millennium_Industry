package kivo.millennium.millind.machine.MeltingFurnace;

import kivo.millennium.millind.block.laser.HorizontalMachineBL;
import kivo.millennium.millind.container.Device.MeltingFurnaceMenu;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MeltingFurnaceBL extends HorizontalMachineBL<MeltingFurnaceBE> {
    public MeltingFurnaceBL() {
        super(Properties.of().destroyTime(40.0F).sound(SoundType.METAL).lightLevel(STATE_TO_LIGHT_LEVEL_FUNCTION));
    }

    @Override
    public MeltingFurnaceBE newBlockEntity(BlockPos pPos, BlockState pState) {
        return new MeltingFurnaceBE(pPos, pState);
    }

    @Override
    protected BlockEntityType<MeltingFurnaceBE> blockEntityType() {
        return MillenniumBlockEntities.MELTING_FURNACE_BE.get();
    }

    @Override
    protected AbstractContainerMenu createContainerMenu(int containerId, Inventory playerInventory, BlockPos pos, Player player) {
        return new MeltingFurnaceMenu(containerId, player, pos);
    }
}
