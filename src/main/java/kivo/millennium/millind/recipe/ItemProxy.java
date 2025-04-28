package kivo.millennium.millind.recipe;

import kivo.millennium.millind.Main;
import kivo.millennium.millind.capability.CapabilityType;
import kivo.millennium.millind.capability.MillenniumItemStorage;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemProxy implements ISlotProxy<ItemStack> {

    MillenniumItemStorage itemStorage;
    int index;

    public ItemProxy(MillenniumItemStorage itemStorage, int index){
        this.itemStorage = itemStorage;
        this.index = index;
    }

    @Override
    public boolean isEmpty() {
        return itemStorage.getStackInSlot(index).isEmpty();
    }

    @Override
    public int getAmount() {
        return itemStorage.getStackInSlot(index).getCount();
    }

    @Override
    public void setSlotLimit(int limit) {

    }

    @Override
    public int getSlotLimit() {
        return itemStorage.getSlotLimit(index);
    }

    @Override
    public RecipeComponent convert2RecipeComponent() {
        return null;
    }

    @Override
    public ItemStack get() {
        return itemStorage.getStackInSlot(index);
    }

    @Override
    public void set(ItemStack stack) {
        itemStorage.setStackInSlot(index, stack);
    }

    @Override
    public ItemStack shrink(int amount) {
        itemStorage.getStackInSlot(index).shrink(amount);
        return itemStorage.getStackInSlot(index);
    }

    @Override
    public ItemStack grow(int amount) {
        itemStorage.getStackInSlot(index).grow(amount);
        return itemStorage.getStackInSlot(index);
    }

    @Override
    public boolean hasPlaceFor(RecipeComponent<ItemStack> itemComponent) {
        ItemStack itemStack = itemComponent.get();
        if (isEmpty()) return getSlotLimit() >= itemStack.getCount();
        if (itemStack.is(get().getItem()) && itemStack.getCount() + getAmount() <= getSlotLimit()){
            return true;
        }
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public CapabilityType getType() {
        return CapabilityType.ITEM;
    }

    @Override
    public boolean contains(RecipeComponent<ItemStack> itemComponent) {
        if (isEmpty()) return false;
        ItemStack itemStack = itemComponent.get();
        if (get().is(itemStack.getItem()) && itemStack.getCount() <= getAmount()){
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(RecipeComponent<ItemStack> itemComponent) {
        ItemStack itemStack = itemComponent.get();
        if (contains(itemComponent)){
            Float costChance = itemComponent.asItemComponent().getCostChance();
            if (costChance.equals(1.0F)){
                shrink(itemComponent.get().getCount());
            } else if (costChance.equals(0.0F)){
                //
            } else {
                if (Math.random() < costChance)
                    shrink(itemComponent.get().getCount());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean add(RecipeComponent<ItemStack> itemComponent) {
        ItemStack itemStack = itemComponent.get();
        if (isEmpty()) {
            set(itemStack);
            return true;
        }
        if (contains(itemComponent)){
            grow(itemComponent.get().getCount());
            return true;
        }
        return false;
    }
}
