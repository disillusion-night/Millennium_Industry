package kivo.millennium.millind.recipe;


import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;

public interface RecipeComponent {

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

    /**
     * 返回此成分的类型标识符，用于在 JSON 和网络数据中区分不同的成分类型。
     *
     * @return 成分类型标识符。
     */
    String getType();
}