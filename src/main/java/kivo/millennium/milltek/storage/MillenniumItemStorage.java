package kivo.millennium.milltek.storage;

import kivo.millennium.milltek.container.Device.ItemProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;

public class MillenniumItemStorage extends ItemStackHandler{

    public MillenniumItemStorage() {
        this(1);
    }

    public MillenniumItemStorage(int size) {
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    public MillenniumItemStorage(NonNullList<ItemStack> stacks) {
        this.stacks = stacks;
    }

    public ItemProxy getSlotProxy(int slot) {
        return new ItemProxy(this, slot);
    }

    public void drops(Level level, BlockPos worldPosition) {
        Containers.dropContents(level, worldPosition, stacks);
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        CompoundTag tag = new CompoundTag();
        deserializeNBT(nbt);
        return tag;
    }

}
