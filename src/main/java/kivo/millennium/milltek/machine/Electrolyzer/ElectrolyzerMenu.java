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
    private static final int FLUID_INPUT = 0;
    private static final int GAS_OUTPUT1 = 0;
    private static final int GAS_OUTPUT2 = 1;

    public ElectrolyzerMenu(int containerId, Player player, BlockPos pos) {
        super(MillenniumMenuTypes.ELECTOLYZER_MENU.get(), containerId, player, pos,
                new SimpleContainer(ElectrolyzerBE.SLOT_COUNT));
    }

    @Override
    protected void setupSlot(net.minecraft.world.Container container, ElectrolyzerBE deviceBE) {
        super.setupSlot(container, deviceBE);
        // 液体输入槽
        addFluidSlot(new FluidSlot(deviceBE.getFluidTank(), FLUID_INPUT, 26, 16, 16, 57));
        // 气体输出槽
        addGasSlot(new GasSlot(deviceBE.getGasTank(), GAS_OUTPUT1, 134, 16, 16,
                57));
        addGasSlot(new GasSlot(deviceBE.getGasTank(), GAS_OUTPUT2, 152, 16, 16,
                57));
    }

    @Override
    protected Block getBlock() {
        return MillenniumBlocks.ELECTROLYZER.get();
    }
}