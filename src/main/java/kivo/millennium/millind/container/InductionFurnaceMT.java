package kivo.millennium.millind.container;

import kivo.millennium.millind.init.MillenniumMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class InductionFurnaceMT extends AbstractDeviceMT{
    public InductionFurnaceMT(int windowId, Player player, BlockPos pos) {
        super(windowId, player, pos, MillenniumMenuTypes.INDUCTION_FURNACE_MENU.get());
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return null;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }
}
