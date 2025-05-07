package kivo.millennium.milltek.recipe;


import com.google.gson.JsonObject;

import kivo.millennium.milltek.capability.CapabilityType;
import net.minecraft.network.FriendlyByteBuf;

public interface RecipeComponent<T> {


    T get();

    /**
     *
     * @param component
     * @return
     * @param <R>
     */

    <R extends RecipeComponent> boolean matches(R component);

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

    default ItemComponent asItemComponent(){
        if (this instanceof ItemComponent){
            return (ItemComponent) this;
        }else {
            return null;
        }
    }


    default FluidComponent asFluidComponent(){
        if (this instanceof FluidComponent){
            return (FluidComponent) this;
        }else {
            return null;
        }
    }

    CapabilityType getType();
}