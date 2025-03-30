package kivo.millennium.millind.recipe;

import com.google.common.collect.Lists;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


public class ExtendedContainer implements Container, StackedContentsCompatible {
    private final int size;
    private final NonNullList<ItemStack> items;
    private final ArrayList<FluidStack> fluids;
    //private final List<ItemStack> items;
    @Nullable
    private List<ContainerListener> listeners;

    public ExtendedContainer(int pSize) {
        this.size = pSize;
        this.items = NonNullList.withSize(pSize, ItemStack.EMPTY);
        this.fluids = new ArrayList<FluidStack>();
        fluids.add(FluidStack.EMPTY);
    }

    public ExtendedContainer(ItemStack... pItems) {
        this.size = pItems.length;
        this.items = NonNullList.of(ItemStack.EMPTY, pItems);
        this.fluids = new ArrayList<FluidStack>();
        fluids.add(FluidStack.EMPTY);
    }

    public void addListener(ContainerListener pListener) {
        if (this.listeners == null) {
            this.listeners = Lists.newArrayList();
        }

        this.listeners.add(pListener);
    }

    public void removeListener(ContainerListener pListener) {
        if (this.listeners != null) {
            this.listeners.remove(pListener);
        }

    }

    public FluidStack getFluid(int pIndex) {
        return pIndex >= 0 && pIndex < this.fluids.size() ? (FluidStack) this.fluids.get(pIndex) : FluidStack.EMPTY;
    }

    public ItemStack getItem(int pIndex) {
        return pIndex >= 0 && pIndex < this.items.size() ? (ItemStack)this.items.get(pIndex) : ItemStack.EMPTY;
    }

    public List<ItemStack> removeAllItems() {
        List<ItemStack> $$0 = (List)this.items.stream().filter((p_19197_) -> {
            return !p_19197_.isEmpty();
        }).collect(Collectors.toList());
        this.clearContent();
        return $$0;
    }

    public ItemStack removeItem(int pIndex, int pCount) {
        ItemStack $$2 = ContainerHelper.removeItem(this.items, pIndex, pCount);
        if (!$$2.isEmpty()) {
            this.setChanged();
        }

        return $$2;
    }

    public ItemStack removeItemType(Item pItem, int pAmount) {
        ItemStack $$2 = new ItemStack(pItem, 0);

        for(int $$3 = this.size - 1; $$3 >= 0; --$$3) {
            ItemStack $$4 = this.getItem($$3);
            if ($$4.getItem().equals(pItem)) {
                int $$5 = pAmount - $$2.getCount();
                ItemStack $$6 = $$4.split($$5);
                $$2.grow($$6.getCount());
                if ($$2.getCount() == pAmount) {
                    break;
                }
            }
        }

        if (!$$2.isEmpty()) {
            this.setChanged();
        }

        return $$2;
    }

    public FluidStack addFluid(FluidStack pStack) {
        if (pStack.isEmpty()) {
            return FluidStack.EMPTY;
        } else {
            FluidStack stack = pStack.copy();
            this.moveFluidToOccupiedSlotsWithSameType(stack);
            if (stack.isEmpty()) {
                return FluidStack.EMPTY;
            } else {
                this.moveFluidToEmptySlots(stack);
                return stack.isEmpty() ? FluidStack.EMPTY : stack;
            }
        }
    }

    public ItemStack addItem(ItemStack pStack) {
        if (pStack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            ItemStack $$1 = pStack.copy();
            this.moveItemToOccupiedSlotsWithSameType($$1);
            if ($$1.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                this.moveItemToEmptySlots($$1);
                return $$1.isEmpty() ? ItemStack.EMPTY : $$1;
            }
        }
    }

    public boolean canAddItem(ItemStack pStack) {
        boolean $$1 = false;
        Iterator var3 = this.items.iterator();

        while(var3.hasNext()) {
            ItemStack $$2 = (ItemStack)var3.next();
            if ($$2.isEmpty() || ItemStack.isSameItemSameTags($$2, pStack) && $$2.getCount() < $$2.getMaxStackSize()) {
                $$1 = true;
                break;
            }
        }

        return $$1;
    }

    public ItemStack removeItemNoUpdate(int pIndex) {
        ItemStack $$1 = (ItemStack)this.items.get(pIndex);
        if ($$1.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.items.set(pIndex, ItemStack.EMPTY);
            return $$1;
        }
    }

    public void setFluid(int pIndex, FluidStack pStack) {
        this.fluids.set(pIndex, pStack);
        //if (!pStack.isEmpty() && pStack.getCount() > this.getMaxStackSize()) {
            //pStack.setCount(this.getMaxStackSize());
        //}

        this.setChanged();
    }
    public void setItem(int pIndex, ItemStack pStack) {
        this.items.set(pIndex, pStack);/*
        if (!pStack.isEmpty() && pStack.getCount() > this.getMaxStackSize()) {
            pStack.setCount(this.getMaxStackSize());
        }*/

        this.setChanged();
    }

    public int getContainerSize() {
        return this.size;
    }

    public boolean isEmpty() {
        Iterator iterator = this.items.iterator();
        boolean out1 = false;
        boolean out2 = false;
        ItemStack itemStack;
        do {
            if (!iterator.hasNext()) {
                out1 = true;
            }

            itemStack = (ItemStack)iterator.next();
        } while(itemStack.isEmpty());

        if(!this.fluids.get(0).isEmpty()){
            Iterator var2 = this.fluids.iterator();

            FluidStack fluidStack;
            do {
                if (!var2.hasNext()) {
                    out2 = true;
                }

                fluidStack = (FluidStack)var2.next();
            } while(fluidStack.isEmpty());
        }else {
            out2 = true;
        }
        return out1 && out2;
    }

    public void setChanged() {
        if (this.listeners != null) {
            Iterator var1 = this.listeners.iterator();

            while(var1.hasNext()) {
                ContainerListener $$0 = (ContainerListener)var1.next();
                $$0.containerChanged(this);
            }
        }

    }

    public boolean stillValid(Player pPlayer) {
        return true;
    }

    public void clearContent() {
        this.items.clear();
        this.setChanged();
    }

    public void fillStackedContents(StackedContents pHelper) {
        Iterator var2 = this.items.iterator();

        while(var2.hasNext()) {
            ItemStack $$1 = (ItemStack)var2.next();
            pHelper.accountStack($$1);
        }

    }

    public String toString() {
        return ((List)this.items.stream().filter((p_19194_) -> {
            return !p_19194_.isEmpty();
        }).collect(Collectors.toList())).toString();
    }

    private void moveFluidToEmptySlots(FluidStack fluidStack) {
        for(int $$1 = 0; $$1 < this.size; ++$$1) {
            ItemStack $$2 = this.getItem($$1);
            if ($$2.isEmpty()) {
                this.setFluid($$1, fluidStack.copy());
                return;
            }
        }

    }
    private void moveItemToEmptySlots(ItemStack pStack) {
        for(int $$1 = 0; $$1 < this.size; ++$$1) {
            ItemStack $$2 = this.getItem($$1);
            if ($$2.isEmpty()) {
                this.setItem($$1, pStack.copyAndClear());
                return;
            }
        }

    }

    private void moveFluidToOccupiedSlotsWithSameType(FluidStack fluidStack) {
        for(int i = 0; i < this.size; ++i) {
            FluidStack tgt = this.getFluid(i);
            if (fluidStack.containsFluid(tgt)) {
                this.moveFluidsBetweenStacks(fluidStack, tgt);
                if (fluidStack.isEmpty()) {
                    return;
                }
            }
        }

    }
    private void moveItemToOccupiedSlotsWithSameType(ItemStack pStack) {
        for(int $$1 = 0; $$1 < this.size; ++$$1) {
            ItemStack $$2 = this.getItem($$1);
            if (ItemStack.isSameItemSameTags($$2, pStack)) {
                this.moveItemsBetweenStacks(pStack, $$2);
                if (pStack.isEmpty()) {
                    return;
                }
            }
        }

    }

    private void moveFluidsBetweenStacks(FluidStack pStack, FluidStack pOther) {
        //int $$2 = Math.min(this.getMaxStackSize(), pOther.getMaxStackSize());
        int $$3 = pStack.getAmount();
        if ($$3 > 0) {
            pOther.grow($$3);
            pStack.shrink($$3);
            this.setChanged();
        }

    }


    private void moveItemsBetweenStacks(ItemStack pStack, ItemStack pOther) {
        int $$2 = Math.min(this.getMaxStackSize(), pOther.getMaxStackSize());
        int $$3 = Math.min(pStack.getCount(), $$2 - pOther.getCount());
        if ($$3 > 0) {
            pOther.grow($$3);
            pStack.shrink($$3);
            this.setChanged();
        }

    }

    public void fromTag(ListTag pContainerNbt) {
        this.clearContent();

        for(int $$1 = 0; $$1 < pContainerNbt.size(); ++$$1) {
            ItemStack $$2 = ItemStack.of(pContainerNbt.getCompound($$1));
            if (!$$2.isEmpty()) {
                this.addItem($$2);
            }
        }

    }

    public ListTag createTag() {
        ListTag $$0 = new ListTag();

        for(int $$1 = 0; $$1 < this.getContainerSize(); ++$$1) {
            ItemStack $$2 = this.getItem($$1);
            if (!$$2.isEmpty()) {
                $$0.add($$2.save(new CompoundTag()));
            }
        }

        return $$0;
    }
}
