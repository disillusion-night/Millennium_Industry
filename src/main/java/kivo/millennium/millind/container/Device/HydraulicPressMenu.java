package kivo.millennium.millind.container.Device;

import kivo.millennium.millind.block.device.AbstractMachineBE;
import kivo.millennium.millind.block.device.HydraulicPress.HydraulicPressBE;
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

public class HydraulicPressMenu extends AbstractDeviceMenu {
    private static final Vector2i input1pos = new Vector2i(53, 17);
    private static final Vector2i input2pos = new Vector2i(53, 53);
    private static final Vector2i outputpos = new Vector2i(107, 35);
    private int progressAndLit;
    public HydraulicPressMenu(int pContainerId, Player player, BlockPos pos) {
        super(MillenniumMenuTypes.HYDRAULIC_PRESS_MENU.get(), pContainerId, player, pos, new SimpleContainer(HydraulicPressBE.SLOT_COUNT));
    }

    @Override
    protected void setupSlot(Container container, AbstractMachineBE deviceBE) {
        super.setupSlot(container, deviceBE);
        addSlot(new ExtendedSlot(container, deviceBE.getItemHandler(), HydraulicPressBE.INPUT1_SLOT, input1pos));
        addSlot(new ExtendedSlot(container, deviceBE.getItemHandler(), HydraulicPressBE.INPUT2_SLOT, input2pos));
        addSlot(new DeviceOutputSlot(container, deviceBE.getItemHandler(), HydraulicPressBE.OUTPUT_SLOT, outputpos));
    }


    @Override
    protected void setupDataSlot(Container container, AbstractMachineBE deviceBE) {
        super.setupDataSlot(container, deviceBE);
        HydraulicPressBE be = (HydraulicPressBE) deviceBE;
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return be.getProgressAndLit() & 0xffff;
            }

            @Override
            public void set(int pValue) {
                HydraulicPressMenu.this.progressAndLit = (HydraulicPressMenu.this.progressAndLit & 0xffff0000) | (pValue & 0xffff);
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
        return MillenniumBlocks.HYDRAULIC_PRESS_BL.get();
    }
}
