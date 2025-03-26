package kivo.millennium.millind.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

public class ItemComponent implements RecipeComponent {

    private ItemStack itemStack;

    public ItemComponent(ItemStack itemStack) {
        this.itemStack = itemStack.copy();
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public void writeToJson(JsonObject jsonObject) {
        jsonObject.addProperty("item", BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString());
        if (itemStack.getCount() > 1) {
            jsonObject.addProperty("count", itemStack.getCount());
        }
        if (itemStack.hasTag()) {
            jsonObject.addProperty("nbt", itemStack.getTag().toString());
        }
    }

    @Override
    public void readFromJson(JsonObject jsonObject) {
        ResourceLocation itemLocation;
        int count = 1;

        if (jsonObject.has("item")) {
            JsonElement itemElement = jsonObject.get("item");
            if (itemElement.isJsonPrimitive() && itemElement.getAsJsonPrimitive().isString()) {
                itemLocation = new ResourceLocation(itemElement.getAsString());
            } else if (itemElement.isJsonObject()) {
                JsonObject itemObject = itemElement.getAsJsonObject();
                itemLocation = new ResourceLocation(GsonHelper.getAsString(itemObject, "item"));
                count = GsonHelper.getAsInt(itemObject, "count", 1);
            } else {
                throw new JsonSyntaxException("Expected 'item' to be a string or an object containing 'item'");
            }
        } else if (jsonObject.isJsonPrimitive() && jsonObject.getAsJsonPrimitive().isString()) {
            itemLocation = new ResourceLocation(jsonObject.getAsString());
        } else {
            throw new JsonSyntaxException("Expected item data to be a string or an object containing 'item'");
        }

        this.itemStack = new ItemStack(BuiltInRegistries.ITEM.get(itemLocation), count);

        if (jsonObject.has("nbt")) {/*
            try {
                this.itemStack.setTag(net.minecraft.nbt.CompoundTag.tryParse(GsonHelper.getAsString(jsonObject, "nbt")));
            } catch (net.minecraft.nbt.TagFormatException e) {
                e.printStackTrace();
            }*/
        }
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeItem(itemStack);
    }

    @Override
    public void readFromNetwork(FriendlyByteBuf buffer) {
        this.itemStack = buffer.readItem();
    }

    @Override
    public String getType() {
        return "item";
    }
}