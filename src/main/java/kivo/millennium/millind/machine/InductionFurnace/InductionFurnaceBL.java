package kivo.millennium.millind.machine.InductionFurnace;

import kivo.millennium.millind.block.laser.HorizontalMachineBL;
import kivo.millennium.millind.container.Device.InductionFurnaceMenu;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class InductionFurnaceBL extends HorizontalMachineBL<InductionFurnaceBE> {

    public InductionFurnaceBL() {
        super(Properties.of().destroyTime(40.0F).sound(SoundType.METAL).lightLevel(STATE_TO_LIGHT_LEVEL_FUNCTION));
    }

    @Override
    public InductionFurnaceBE newBlockEntity(BlockPos pPos, BlockState pState) {
        return new InductionFurnaceBE(pPos, pState);
    }

    @Override
    protected BlockEntityType<InductionFurnaceBE> blockEntityType() {
        return MillenniumBlockEntities.INDUCTION_FURNACE_BE.get();
    }

    @Override
    protected AbstractContainerMenu createContainerMenu(int containerId, Inventory playerInventory, BlockPos pos, Player player) {
        return new InductionFurnaceMenu(containerId, player, pos);
    }
}
