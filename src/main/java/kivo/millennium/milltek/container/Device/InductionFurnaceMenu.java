package kivo.millennium.milltek.container.Device;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.Block;
import org.joml.Vector2i;

import kivo.millennium.milltek.capability.DeviceOutputSlot;
import kivo.millennium.milltek.capability.ExtendedSlot;
import kivo.millennium.milltek.init.MillenniumBlocks;
import kivo.millennium.milltek.init.MillenniumMenuTypes;
import kivo.millennium.milltek.machine.InductionFurnace.InductionFurnaceBE;

public class InductionFurnaceMenu extends AbstractDeviceMenu<InductionFurnaceBE> {
    private static final Vector2i inputpos = new Vector2i(53, 35);
    private static final Vector2i outputpos = new Vector2i(107, 35);
    private int progressAndLit;
    public InductionFurnaceMenu(int pContainerId, Player player, BlockPos pos) {
        super(MillenniumMenuTypes.INDUCTION_FURNACE_MENU.get(), pContainerId, player, pos, new SimpleContainer(InductionFurnaceBE.SLOT_COUNT));
    }

    @Override
    protected void setupSlot(Container container, InductionFurnaceBE deviceBE) {
        super.setupSlot(container, deviceBE);
        addSlot(new ExtendedSlot(container, deviceBE.getItemHandler(), InductionFurnaceBE.INPUT_SLOT, inputpos));
        addSlot(new DeviceOutputSlot(container, deviceBE.getItemHandler(), InductionFurnaceBE.OUTPUT_SLOT, outputpos));
    }


    @Override
    protected void setupDataSlot(Container container, InductionFurnaceBE deviceBE) {
        super.setupDataSlot(container, deviceBE);
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return deviceBE.getProgressAndLit() & 0xffff;
            }

            @Override
            public void set(int pValue) {
                InductionFurnaceMenu.this.progressAndLit = (InductionFurnaceMenu.this.progressAndLit & 0xffff0000) | (pValue & 0xffff);
            }
        });
    }

    public int getProgress(){
        return progressAndLit >> 1;
    }

    public boolean getLit(){
        return (progressAndLit & 1) > 0;
    }
    @Override
    protected Block getBlock() {
        return MillenniumBlocks.INDUCTION_FURNACE_BL.get();
    }
}
