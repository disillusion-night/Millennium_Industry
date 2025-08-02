package kivo.millennium.milltek.machine.MeltingFurnace;

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

public class MeltingFurnaceMenu extends AbstractDeviceMenu<MeltingFurnaceBE> {
    private static final Vector2i inputpos = new Vector2i(53, 37);
    private static final Vector2i outputpos = new Vector2i(107, 35);

    private int progressAndLit;

    public MeltingFurnaceMenu(int pContainerId, Player player, BlockPos pos) {
        super(MillenniumMenuTypes.MELTING_FURNACE_MENU.get(), pContainerId, player, pos,
                new SimpleContainer(MeltingFurnaceBE.SLOT_COUNT));
    }

    @Override
    protected void setupSlot(Container container, MeltingFurnaceBE deviceBE) {
        super.setupSlot(container, deviceBE);
        addSlot(new ExtendedSlot(container, deviceBE.getItemHandler(), MeltingFurnaceBE.INPUT_SLOT, inputpos));
        
        addFluidSlot(new FluidSlot(deviceBE.getFluidTank(), 0, 107, 16));
    }

    @Override
    protected void setupDataSlot(Container container, MeltingFurnaceBE deviceBE) {
        super.setupDataSlot(container, deviceBE);
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return deviceBE.getProgressAndLit() & 0xffff;
            }

            @Override
            public void set(int pValue) {
                MeltingFurnaceMenu.this.progressAndLit = (MeltingFurnaceMenu.this.progressAndLit & 0xffff0000)
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
        return MillenniumBlocks.MELTING_FURNACE_BL.get();
    }
}