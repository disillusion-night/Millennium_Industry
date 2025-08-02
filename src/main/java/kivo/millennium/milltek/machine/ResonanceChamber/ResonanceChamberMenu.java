package kivo.millennium.milltek.machine.ResonanceChamber;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.Block;
import org.joml.Vector2i;

import kivo.millennium.milltek.capability.ExtendedSlot;
import kivo.millennium.milltek.container.Device.AbstractDeviceMenu;
import kivo.millennium.milltek.init.MillenniumBlocks;
import kivo.millennium.milltek.init.MillenniumMenuTypes;

public class ResonanceChamberMenu extends AbstractDeviceMenu<ResonanceChamberBE> {
    private static final Vector2i inputpos = new Vector2i(80, 36);
    private int progressAndLit;
    public ResonanceChamberMenu(int pContainerId, Player player, BlockPos pos) {
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
                ResonanceChamberMenu.this.progressAndLit = (ResonanceChamberMenu.this.progressAndLit & 0xffff0000) | (pValue & 0xffff);
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