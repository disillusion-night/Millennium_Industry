package kivo.millennium.millind.recipe;

import com.google.gson.JsonObject;
import kivo.millennium.millind.capability.CapabilityType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

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
