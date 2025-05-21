package kivo.millennium.milltek.machine.FusionChamber;

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
import kivo.millennium.milltek.container.FluidSlot;
import kivo.millennium.milltek.init.MillenniumBlocks;
import kivo.millennium.milltek.init.MillenniumMenuTypes;

import java.util.List;
import java.util.Arrays;

public class FusionChamberMenu extends AbstractDeviceMenu<FusionChamberBE> {
    private static final Vector2i inputpos = new Vector2i(56, 37);
    private static final Vector2i outputpos = new Vector2i(107, 35);
    private int progressAndLit;

    public FusionChamberMenu(int pContainerId, Player player, BlockPos pos) {
        super(MillenniumMenuTypes.FUSION_FURNACE_MENU.get(), pContainerId, player, pos,
                new SimpleContainer(FusionChamberBE.SLOT_COUNT));
    }

    @Override
    protected void setupSlot(Container container, FusionChamberBE deviceBE) {
        super.setupSlot(container, deviceBE);
        addSlot(new ExtendedSlot(container, deviceBE.getItemHandler(), FusionChamberBE.INPUT_SLOT, inputpos));
        // 标准化流体槽，直接添加两个FluidSlot
        addFluidSlot(new FluidSlot(deviceBE.getFluidTank(), 0, 26, 16));
        addFluidSlot(new FluidSlot(deviceBE.getFluidTank(), 1, 116, 16));
    }

    @Override
    protected void setupDataSlot(Container container, FusionChamberBE deviceBE) {
        super.setupDataSlot(container, deviceBE);
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return deviceBE.getProgressAndLit() & 0xffff;
            }

            @Override
            public void set(int pValue) {
                FusionChamberMenu.this.progressAndLit = (FusionChamberMenu.this.progressAndLit & 0xffff0000)
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

    public FluidStack getFluid(int index) {
        return fluidSlots.get(index).getFluidStack();
    }

    public int getFluidAmount(int index) {
        return fluidSlots.get(index).getFluidStack().getAmount();
    }

    public int getFluidCapacity(int index) {
        return fluidSlots.get(index).getFluidHandler().getTankCapacity(index);
    }

    @Override
    protected Block getBlock() {
        return MillenniumBlocks.FUSION_CHAMBER_BL.get();
    }
}