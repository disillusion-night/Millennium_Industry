package kivo.millennium.milltek.machine.Crusher;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.Block;
import org.joml.Vector2i;

import kivo.millennium.milltek.capability.DeviceOutputSlot;
import kivo.millennium.milltek.capability.ExtendedSlot;
import kivo.millennium.milltek.container.Device.AbstractDeviceMenu;
import kivo.millennium.milltek.init.MillenniumBlocks;
import kivo.millennium.milltek.init.MillenniumMenuTypes;

public class CrusherMenu extends AbstractDeviceMenu<CrusherBE> {
    private static final Vector2i inputpos = new Vector2i(53, 35);
    private static final Vector2i outputpos = new Vector2i(107, 35);
    private int progressAndLit;

    public CrusherMenu(int pContainerId, Player player, BlockPos pos) {
        super(MillenniumMenuTypes.CRUSHER_MENU.get(), pContainerId, player, pos,
                new SimpleContainer(CrusherBE.SLOT_COUNT));
    }

    @Override
    protected void setupSlot(Container container, CrusherBE deviceBE) {
        super.setupSlot(container, deviceBE);
        addSlot(new ExtendedSlot(container, deviceBE.getItemHandler(), CrusherBE.INPUT_SLOT, inputpos));
        addSlot(new DeviceOutputSlot(container, deviceBE.getItemHandler(), CrusherBE.OUTPUT_SLOT, outputpos));
    }

    @Override
    protected void setupDataSlot(Container container, CrusherBE deviceBE) {
        super.setupDataSlot(container, deviceBE);
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return deviceBE.getProgressAndLit() & 0xffff;
            }

            @Override
            public void set(int pValue) {
                CrusherMenu.this.progressAndLit = (CrusherMenu.this.progressAndLit & 0xffff0000) | (pValue & 0xffff);
            }
        });
    }

    public int getProgress() {
        return progressAndLit >> 1;
    }

    public boolean getLit() {
        return (progressAndLit & 1) > 0;
    }

    @Override
    protected Block getBlock() {
        return MillenniumBlocks.CRUSHER_BL.get();
    }
}