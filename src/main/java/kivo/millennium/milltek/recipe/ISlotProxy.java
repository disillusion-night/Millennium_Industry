package kivo.millennium.milltek.recipe;

import kivo.millennium.milltek.capability.CapabilityType;
import kivo.millennium.milltek.container.Device.FluidProxy;
import kivo.millennium.milltek.container.Device.ItemProxy;

public interface ISlotProxy<T> {
    boolean isEmpty();

    int getAmount();

    void setSlotLimit(int limit);

    int getSlotLimit();

    RecipeComponent convert2RecipeComponent();

    T get();

    void set(T stack);

    T shrink(int amount);

    T grow(int amount);

    boolean hasPlaceFor(RecipeComponent<T> component);

    void clear();

    CapabilityType getType();

    boolean contains(RecipeComponent<T> component);

    boolean remove(RecipeComponent<T> component);

    boolean add(RecipeComponent<T> component);

    default ItemProxy asItemProxy(){
        if(this.getType() == CapabilityType.ITEM){
            return (ItemProxy) this;
        }else {
            return null;
        }
    }

    default FluidProxy asFluidProxy(){
        if(this.getType() == CapabilityType.FLUID){
            return (FluidProxy) this;
        }else {
            return null;
        }
    }
}
