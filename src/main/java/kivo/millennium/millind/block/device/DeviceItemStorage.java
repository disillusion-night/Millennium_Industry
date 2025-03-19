package kivo.millennium.millind.block.device;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;

public class DeviceItemStorage extends ItemStackHandler {

    public DeviceItemStorage()
    {
        this(1);
    }

    public DeviceItemStorage(int size)
    {
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    public void drops(Level level, BlockPos worldPosition){
            Containers.dropContents(level, worldPosition, stacks);
    }
}
