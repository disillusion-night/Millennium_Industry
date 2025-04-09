package kivo.millennium.millind.recipe;

import com.google.gson.JsonObject;
import kivo.millennium.millind.capability.CapabilityType;
import net.minecraft.network.FriendlyByteBuf;

public interface ISlotProxy<T> {
    boolean isEmpty();

    int getAmount();

    void setSlotLimit(int limit);

    int getSlotLimit();

    RecipeComponent getAsRecipeComponent();

    ISlotProxy<T> of(T stack);

    T get();

    void set(T stack);

    T shrink(int amount);

    T grow(int amount);

    boolean hasPlaceFor(ISlotProxy slotProxy);

    void clear();

    CapabilityType getType();

    boolean contains(ISlotProxy proxy);

    boolean remove(ISlotProxy proxy);


    boolean add(ISlotProxy proxy);
    /**
     * 将此成分写为 JSON 对象。
     */
    JsonObject toJson();

    /**
     * 从 JSON 对象中读取此成分。
     *
     * @param jsonObject 要读取的 JSON 对象。
     */
    void readFromJson(JsonObject jsonObject);

    /**
     * 将此成分写入网络缓冲区。
     *
     * @param buffer 要写入的网络缓冲区。
     */
    void writeToNetwork(FriendlyByteBuf buffer);

    /**
     * 从网络缓冲区中读取此成分。
     *
     * @param buffer 要读取的网络缓冲区。
     */
    void readFromNetwork(FriendlyByteBuf buffer);

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
