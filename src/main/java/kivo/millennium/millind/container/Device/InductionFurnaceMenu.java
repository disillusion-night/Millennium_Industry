package kivo.millennium.millind.container.Device;

import kivo.millennium.millind.block.device.AbstractDeviceBE;
import kivo.millennium.millind.block.device.inductionFurnace.InductionFurnaceBE;
import kivo.millennium.millind.capability.DeviceOutputSlot;
import kivo.millennium.millind.capability.ExtendedSlot;
import kivo.millennium.millind.init.MillenniumBlocks;
import kivo.millennium.millind.init.MillenniumMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.Block;
import org.joml.Vector2i;

public class InductionFurnaceMenu extends AbstractDeviceMenu {
    private static final Vector2i inputpos = new Vector2i(53, 35);
    private static final Vector2i outputpos = new Vector2i(107, 35);
    private int progress;
    public InductionFurnaceMenu(int pContainerId, Player player, BlockPos pos) {
        super(MillenniumMenuTypes.INDUCTION_FURNACE_MENU.get(), pContainerId, player, pos, new SimpleContainer(InductionFurnaceBE.SLOT_COUNT));
    }

    @Override
    protected void setupSlot(Container container, AbstractDeviceBE deviceBE) {
        super.setupSlot(container, deviceBE);
        addSlot(new ExtendedSlot(container, deviceBE.getItemHandler(), InductionFurnaceBE.INPUT_SLOT, inputpos));
        addSlot(new DeviceOutputSlot(container, deviceBE.getItemHandler(), InductionFurnaceBE.OUTPUT_SLOT, outputpos));
    }


    @Override
    protected void setupDataSlot(Container container, AbstractDeviceBE deviceBE) {
        super.setupDataSlot(container, deviceBE);
        InductionFurnaceBE be = (InductionFurnaceBE) deviceBE;
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return be.getProgress() & 0xffff;
            }

            @Override
            public void set(int pValue) {
                InductionFurnaceMenu.this.progress = (InductionFurnaceMenu.this.progress & 0xffff0000) | (pValue & 0xffff);
            }
        });
    }

    public int getProgress(){
        return progress;
    }

    @Override
    protected Block getBlock() {
        return MillenniumBlocks.INDUCTION_FURNACE_BL.get();
    }
}
