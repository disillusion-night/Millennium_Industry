package kivo.millennium.millind.block.device.inductionFurnace;

import kivo.millennium.millind.block.device.AbstractDeviceBE;
import kivo.millennium.millind.block.laser.HorizontalDeviceBL;
import kivo.millennium.millind.container.Device.InductionFurnaceMenu;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class InductionFurnaceBL extends HorizontalDeviceBL {
    public static final Component SCREEN_INDUCTION_FURNACE = Component.translatable("screen.millind.generator");

    public InductionFurnaceBL() {
        super(Properties.of().destroyTime(40.0F).sound(SoundType.METAL).lightLevel(blockState -> {
            if(blockState.getValue(POWERED)){
                return 15;
            }else {
                return 0;
            }
        }));
    }

    /*
    @Override
    protected void handleRightClick(Level pLevel, BlockPos pPos, AbstractDeviceBE pBE, ServerPlayer pPlayer) {
        MenuProvider containerProvider = new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return SCREEN_INDUCTION_FURNACE;
            }

            @Override
            public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                return new InductionFurnaceMenu(windowId, playerEntity, pPos);
            }
        };
        NetworkHooks.openScreen(pPlayer, containerProvider, pPos);
    }*/

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new InductionFurnaceBE(pPos, pState);
    }

    @Override
    protected BlockEntityType<? extends AbstractDeviceBE> blockEntityType() {
        return MillenniumBlockEntities.INDUCTION_FURNACE_BE.get();
    }

    @Override
    protected AbstractContainerMenu createContainerMenu(int containerId, Inventory playerInventory, BlockPos pos, Player player) {
        return new InductionFurnaceMenu(containerId, player, pos);
    }
}
