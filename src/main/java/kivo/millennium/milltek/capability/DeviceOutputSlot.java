package kivo.millennium.milltek.capability;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public class DeviceOutputSlot extends ExtendedSlot{
    public DeviceOutputSlot(Container container, ItemStackHandler itemHandler, int index, Vector2i pos) {
        super(container, itemHandler, index, pos);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack)
    {
        return false;
    }
}
