package kivo.millennium.millind.container.Device;

import kivo.millennium.millind.block.device.AbstractDeviceBE;
import kivo.millennium.millind.block.device.crusher.CrusherBE;
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

public class CrusherContainer extends AbstractDeviceMenu {
    private static final Vector2i inputpos = new Vector2i(53, 35);
    private static final Vector2i outputpos = new Vector2i(107, 35);
    private int progressAndLit;
    public CrusherContainer(int pContainerId, Player player, BlockPos pos) {
        super(MillenniumMenuTypes.CRUSHER_CONTAINER.get(), pContainerId, player, pos, new SimpleContainer(CrusherBE.SLOT_COUNT));
    }

    @Override
    protected void setupSlot(Container container, AbstractDeviceBE deviceBE) {
        super.setupSlot(container, deviceBE);
        addSlot(new ExtendedSlot(container, deviceBE.getItemHandler(), CrusherBE.INPUT_SLOT, inputpos));
        addSlot(new DeviceOutputSlot(container, deviceBE.getItemHandler(), CrusherBE.OUTPUT_SLOT, outputpos));
    }


    @Override
    protected void setupDataSlot(Container container, AbstractDeviceBE deviceBE) {
        super.setupDataSlot(container, deviceBE);
        CrusherBE be = (CrusherBE) deviceBE;
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return be.getProgressAndLit() & 0xffff;
            }

            @Override
            public void set(int pValue) {
                CrusherContainer.this.progressAndLit = (CrusherContainer.this.progressAndLit & 0xffff0000) | (pValue & 0xffff);
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
        return MillenniumBlocks.CRUSHER_BL.get(); // 返回破碎机方块实例  (请确保你已注册 ModBlocks.CRUSHER_BLOCK)
    }
}