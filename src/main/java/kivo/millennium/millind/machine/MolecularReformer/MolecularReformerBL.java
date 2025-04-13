package kivo.millennium.millind.machine.MolecularReformer;

import kivo.millennium.millind.block.device.AbstractMachineBE;
import kivo.millennium.millind.block.laser.HorizonalMachineBL;
import kivo.millennium.millind.container.Device.MeltingFurnaceMenu;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MolecularReformerBL extends HorizonalMachineBL {
    public MolecularReformerBL() {
        super(Properties.of().destroyTime(40.0F).sound(SoundType.METAL).lightLevel(blockState -> {
            if(blockState.getValue(WORKING)){
                return 15;
            }else {
                return 0;
            }
        }));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new MolecularReformerBE(pPos, pState);
    }

    @Override
    protected BlockEntityType<? extends AbstractMachineBE> blockEntityType() {
        return MillenniumBlockEntities.MOLECULAR_REFORMER_BE.get();
    }

    @Override
    protected AbstractContainerMenu createContainerMenu(int containerId, Inventory playerInventory, BlockPos pos, Player player) {
        return new MeltingFurnaceMenu(containerId, player, pos);
    }
}
