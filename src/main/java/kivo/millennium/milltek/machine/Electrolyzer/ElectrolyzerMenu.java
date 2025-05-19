package kivo.millennium.milltek.machine.Electrolyzer;

import kivo.millennium.milltek.container.Device.AbstractDeviceMenu;
import kivo.millennium.milltek.init.MillenniumBlocks;
import kivo.millennium.milltek.init.MillenniumMenuTypes;
import kivo.millennium.milltek.machine.Crusher.CrusherBE;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;

public class ElectrolyzerMenu extends AbstractDeviceMenu<ElectrolyzerBE> {
  public ElectrolyzerMenu(int containerId, Player player, BlockPos pos) {
    super(MillenniumMenuTypes.ELECTOLYZER_MENU.get(), containerId, player, pos, new SimpleContainer(1));
  }

  @Override
  protected void setupDataSlot(net.minecraft.world.Container container, ElectrolyzerBE deviceBE) {
    // 可选：添加进度、能量、流体等同步槽
    // 如有需要可用DataSlot、ContainerData等方式同步
  }

  @Override
  protected Block getBlock() {
    return MillenniumBlocks.ELECTROLYZER.get();
  }
}