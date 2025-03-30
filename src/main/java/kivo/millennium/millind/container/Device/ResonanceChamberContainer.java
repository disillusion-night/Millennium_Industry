package kivo.millennium.millind.container.Device;

import kivo.millennium.millind.block.device.AbstractMachineBE;
import kivo.millennium.millind.block.device.ResonanceChamber.ResonanceChamberBE;
import kivo.millennium.millind.block.device.crusher.CrusherBE;
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

public class ResonanceChamberContainer extends AbstractDeviceMenu<ResonanceChamberBE> {
    private static final Vector2i inputpos = new Vector2i(80, 36);
    private int progressAndLit;
    public ResonanceChamberContainer(int pContainerId, Player player, BlockPos pos) {
        super(MillenniumMenuTypes.RESONANCE_CHAMBER_MENU.get(), pContainerId, player, pos, new SimpleContainer(ResonanceChamberBE.SLOT_COUNT));
    }

    @Override
    protected void setupSlot(Container container, ResonanceChamberBE deviceBE) {
        super.setupSlot(container, deviceBE);
        addSlot(new ExtendedSlot(container, deviceBE.getItemHandler(), ResonanceChamberBE.INPUT_SLOT, inputpos));
    }


    @Override
    protected void setupDataSlot(Container container, ResonanceChamberBE deviceBE) {
        super.setupDataSlot(container, deviceBE);
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return deviceBE.getProgressAndLit() & 0xffff;
            }

            @Override
            public void set(int pValue) {
                ResonanceChamberContainer.this.progressAndLit = (ResonanceChamberContainer.this.progressAndLit & 0xffff0000) | (pValue & 0xffff);
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
        return MillenniumBlocks.RESONANCE_CHAMBER_BL.get();
    }
}