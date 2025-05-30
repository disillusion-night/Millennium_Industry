package kivo.millennium.milltek.container.Device;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemStackHandler;
import org.joml.Vector2i;

import kivo.millennium.milltek.capability.ExtendedSlot;

public class BatterySlot extends ExtendedSlot {
    public BatterySlot(Container container, ItemStackHandler itemHandler, int index, Vector2i pos) {
        super(container, itemHandler, index, pos);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
    }
}
