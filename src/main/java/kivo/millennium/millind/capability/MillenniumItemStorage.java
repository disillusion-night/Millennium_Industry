package kivo.millennium.millind.capability;

import kivo.millennium.millind.recipe.ItemProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Consumer;

public class MillenniumItemStorage extends ItemStackHandler implements IMillenniumStorage {

    public MillenniumItemStorage()
    {
        this(1);
    }

    public MillenniumItemStorage(int size) {
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    public MillenniumItemStorage(NonNullList<ItemStack> stacks) {
        this.stacks = stacks;
    }

    public ItemProxy getSlotProxy(int slot){
        return new ItemProxy(this, slot);
    }

    public void drops(Level level, BlockPos worldPosition){
        Containers.dropContents(level, worldPosition, stacks);
    }

}
