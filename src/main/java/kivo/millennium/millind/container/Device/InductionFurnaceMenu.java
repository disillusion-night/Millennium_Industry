package kivo.millennium.millind.container.Device;

import kivo.millennium.millind.block.device.AbstractDeviceBE;
import kivo.millennium.millind.block.device.inductionFurnace.InductionFurnaceBE;
import kivo.millennium.millind.capability.ExtendedSlot;
import kivo.millennium.millind.init.MillenniumBlocks;
import kivo.millennium.millind.init.MillenniumMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.IItemHandler;
import org.joml.Vector2i;

public class InductionFurnaceMT extends AbstractDeviceMT {
    private static final Vector2i invlabel = new Vector2i(8, 94);
    private static final Vector2i batteryslot = new Vector2i(152, 71);
    private static final Vector2i input1pos = new Vector2i(44, 34);
    private static final Vector2i input2pos = new Vector2i(44, 52);
    private static final Vector2i output1pos = new Vector2i(103, 34);
    private static final Vector2i output2pos = new Vector2i(121, 34);
    private static final Vector2i output3pos = new Vector2i(103, 52);
    private static final Vector2i output4pos = new Vector2i(121, 52);
    public InductionFurnaceMT(int pContainerId, Player player, BlockPos pos) {
        super(MillenniumMenuTypes.INDUCTION_FURNACE_MENU.get(), pContainerId, player, pos, new SimpleContainer(InductionFurnaceBE.SLOT_COUNT));
        this.BATTERY_SLOT_POS = new Vector2i(152, 71);
    }

    @Override
    protected void setupSlot(Container container, AbstractDeviceBE deviceBE) {
        super.setupSlot(container, deviceBE);
        addSlot(new ExtendedSlot(container, deviceBE.getItemHandler(), InductionFurnaceBE.INPUT1_SLOT, input1pos));
        addSlot(new ExtendedSlot(container, deviceBE.getItemHandler(), InductionFurnaceBE.INPUT2_SLOT, input2pos));
        addSlot(new ExtendedSlot(container, deviceBE.getItemHandler(), InductionFurnaceBE.OUTPUT1_SLOT, output1pos));
        addSlot(new ExtendedSlot(container, deviceBE.getItemHandler(), InductionFurnaceBE.OUTPUT2_SLOT, output2pos));
        addSlot(new ExtendedSlot(container, deviceBE.getItemHandler(), InductionFurnaceBE.OUTPUT3_SLOT, output3pos));
        addSlot(new ExtendedSlot(container, deviceBE.getItemHandler(), InductionFurnaceBE.OUTPUT4_SLOT, output4pos));
    }

    @Override
    public Vector2i getBatterySlotPos() {
        return batteryslot;
    }


    @Override
    public Vector2i getPlayerInvPos() {
        return invlabel;
    }

    @Override
    protected Block getBlock() {
        return MillenniumBlocks.INDUCTION_FURNACE_BL.get();
    }
}
