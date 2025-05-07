package kivo.millennium.milltek.machine.PipeBooster;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import kivo.millennium.milltek.block.device.AbstractMachineBE;
import kivo.millennium.milltek.block.laser.DirectionalMachineBL;

public class PipeBoosterBL extends DirectionalMachineBL<PipeBoosterBE> {
    public PipeBoosterBL() {
        super(Properties.of().lightLevel(STATE_TO_LIGHT_LEVEL_FUNCTION));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return null;
    }

    @Override
    protected BlockEntityType<? extends AbstractMachineBE> blockEntityType() {
        return null;
    }

    @Override
    protected AbstractContainerMenu createContainerMenu(int containerId, Inventory playerInventory, BlockPos pos, Player player) {
        return null;
    }
}
