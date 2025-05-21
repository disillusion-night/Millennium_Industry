package kivo.millennium.milltek.machine.Crystallizer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Vector2i;

import kivo.millennium.milltek.capability.ExtendedSlot;

import kivo.millennium.milltek.container.Device.AbstractDeviceMenu;
import kivo.millennium.milltek.container.slot.FluidSlot;
import kivo.millennium.milltek.init.MillenniumBlocks;
import kivo.millennium.milltek.init.MillenniumMenuTypes;

public class CrystallizerMenu extends AbstractDeviceMenu<CrystallizerBE> {
    private static final Vector2i inputpos = new Vector2i(116, 17);
    private static final Vector2i outputpos = new Vector2i(116, 54);
    private int fluidCapacity;
    private int fluidAmount;
    private int progressAndLit;
    private int fluidId;

    public CrystallizerMenu(int pContainerId, Player player, BlockPos pos) {
        super(MillenniumMenuTypes.CRYSTALLIZER_MENU.get(), pContainerId, player, pos,
                new SimpleContainer(CrystallizerBE.SLOT_COUNT));
    }

    @Override
    protected void setupSlot(Container container, CrystallizerBE deviceBE) {
        super.setupSlot(container, deviceBE);
        addSlot(new ExtendedSlot(container, deviceBE.getItemHandler(), CrystallizerBE.INPUT_SLOT, inputpos));
        addSlot(new ExtendedSlot(container, deviceBE.getItemHandler(), CrystallizerBE.OUTPUT_SLOT, outputpos));
        addFluidSlot(new FluidSlot(deviceBE.getFluidHandler(), 0, 44, 17));
    }

    @Override
    protected void setupDataSlot(Container container, CrystallizerBE deviceBE) {
        super.setupDataSlot(container, deviceBE);
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return deviceBE.getProgressAndLit() & 0xffff;
            }

            @Override
            public void set(int pValue) {
                CrystallizerMenu.this.progressAndLit = (CrystallizerMenu.this.progressAndLit & 0xffff0000)
                        | (pValue & 0xffff);
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
        return MillenniumBlocks.CRYSTALLIZER_BL.get();
    }
}