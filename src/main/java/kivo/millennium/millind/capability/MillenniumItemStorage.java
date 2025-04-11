package kivo.millennium.millind.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;

public class MillenniumItemStorage extends ItemStackHandler implements IMillenniumStorage {

    public MillenniumItemStorage()
    {
        this(1);
    }

    public MillenniumItemStorage(int size)
    {
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    public MillenniumItemStorage(NonNullList<ItemStack> stacks)
    {
        this.stacks = stacks;
    }

    public void drops(Level level, BlockPos worldPosition){
        Containers.dropContents(level, worldPosition, stacks);
    }
}
