package kivo.millennium.milltek.machine.Electrolyzer;

import kivo.millennium.milltek.container.Device.AbstractDeviceMenu;
import kivo.millennium.milltek.container.slot.FluidSlot;
import kivo.millennium.milltek.container.slot.GasSlot;
import kivo.millennium.milltek.init.MillenniumBlocks;
import kivo.millennium.milltek.init.MillenniumMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

public class ElectrolyzerMenu extends AbstractDeviceMenu<ElectrolyzerBE> {

    public ElectrolyzerMenu(int containerId, Player player, BlockPos pos) {
        super(MillenniumMenuTypes.ELECTOLYZER_MENU.get(), containerId, player, pos,
                new SimpleContainer(1));
    }

    @Override
    protected void setupSlot(net.minecraft.world.Container container, ElectrolyzerBE deviceBE) {
        super.setupSlot(container, deviceBE);
        // 液体输入槽
        addFluidSlot(new FluidSlot(deviceBE.getFluidTank(), 0, 80, 17, 16, 57));
        // 气体输出槽
        addGasSlot(new GasSlot(deviceBE.getGasTank(), 0, 126, 15));
        addGasSlot(new GasSlot(deviceBE.getGasTank(), 1, 34, 15));
    }

    @Override
    protected Block getBlock() {
        return MillenniumBlocks.ELECTROLYZER_BL.get();
    }
}