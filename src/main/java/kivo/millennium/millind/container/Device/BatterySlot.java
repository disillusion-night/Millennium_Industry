package kivo.millennium.millind.container.Device;

import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.joml.Vector2i;

public class BatterySlot extends SlotItemHandler {
    public BatterySlot(IItemHandler itemHandler, int index, Vector2i pos) {
        super(itemHandler, index, pos.x, pos.y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
    }
}
