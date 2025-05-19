package kivo.millennium.milltek.machine.Electrolyzer;

import kivo.millennium.milltek.block.laser.HorizontalMachineBL;
import kivo.millennium.milltek.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ElectrolyzerBL extends HorizontalMachineBL<ElectrolyzerBE> {
  public ElectrolyzerBL() {
    super(Properties.of().destroyTime(40.0F).sound(SoundType.METAL).lightLevel(STATE_TO_LIGHT_LEVEL_FUNCTION));
  }

  @Override
  public ElectrolyzerBE newBlockEntity(BlockPos pos, BlockState state) {
    return new ElectrolyzerBE(pos, state);
  }

  @Override
  protected BlockEntityType<ElectrolyzerBE> blockEntityType() {
    return MillenniumBlockEntities.ELECTROLYZER_BE.get();
  }

  @Override
  protected AbstractContainerMenu createContainerMenu(int containerId, Inventory playerInventory, BlockPos pos,
      Player player) {
    return new ElectrolyzerMenu(containerId, player, pos);
  }
}