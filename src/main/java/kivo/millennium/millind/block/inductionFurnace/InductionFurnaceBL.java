package kivo.millennium.millind.block.inductionFurnace;

import kivo.millennium.millind.Main;
import kivo.millennium.millind.block.AbstractDeviceBE;
import kivo.millennium.millind.block.AbstractDeviceBL;
import kivo.millennium.millind.container.GeneratorMT;
import kivo.millennium.millind.container.InductionFurnaceMT;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;

public class InductionFurnaceBL extends AbstractDeviceBL {
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


    @Override
    protected void handleRightClick(Level pLevel, BlockPos pPos, AbstractDeviceBE pBE, ServerPlayer pPlayer) {
        MenuProvider containerProvider = new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return SCREEN_INDUCTION_FURNACE;
            }

            @Override
            public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                return new InductionFurnaceMT(windowId, playerEntity, pPos);
            }
        };
        NetworkHooks.openScreen(pPlayer, containerProvider, pPos);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new InductionFurnaceBE(pPos, pState);
    }
}
