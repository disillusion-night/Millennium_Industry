package kivo.millennium.millind.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import kivo.millennium.millind.capability.CapabilityType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemComponent implements RecipeComponent<ItemStack> {

    private ItemStack itemStack;
    private float costChance;

    public ItemComponent(ItemStack itemStack) {
        this.itemStack = itemStack.copy();
        this.costChance = 1.0F;
    }

    public ItemComponent(JsonObject jsonObject) {

        ResourceLocation item = new ResourceLocation(GsonHelper.getAsString(jsonObject, "item"));
        int count = GsonHelper.getAsInt(jsonObject, "count", 1);
        this.costChance = GsonHelper.getAsFloat(jsonObject, "cost_chance", 1.0F);
        this.itemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(item), count);
        if (this.itemStack.isEmpty()) {
            throw new JsonSyntaxException("Unknown item: " + item);
        }
    }


    public ItemComponent(ItemStack itemStack, int costChance) {
        this.itemStack = itemStack.copy();
        this.costChance = costChance;
    }

    public static ItemComponent of(ItemStack itemStack){
        return new ItemComponent(itemStack);
    }

    public static ItemComponent of(ItemStack itemStack, int costChance){
        return new ItemComponent(itemStack, costChance);
    }

    @Override
    public ItemStack get() {
        return itemStack;
    }

    @Override
    public <R extends RecipeComponent> boolean matches(R component) {
        if(component instanceof ItemComponent itemComponent){
            ItemStack stack = itemComponent.get();
           return stack.is(this.itemStack.getItem()) && stack.getCount() >= itemStack.getCount();
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("item", BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString());
        if(costChance != 1.0) {
            jsonObject.addProperty("cost_chance", costChance);
        }
        if (itemStack.getCount() > 1) {
            jsonObject.addProperty("count", itemStack.getCount());
        }
        if (itemStack.hasTag()) {
            jsonObject.addProperty("nbt", itemStack.getTag().toString());
        }
        return jsonObject;
    }

    @Override
    public void readFromJson(JsonObject jsonObject) {
        ResourceLocation item = new ResourceLocation(GsonHelper.getAsString(jsonObject, "item"));
        int count = GsonHelper.getAsInt(jsonObject, "count", 1);
        this.costChance = GsonHelper.getAsFloat(jsonObject, "cost_chance", 1.0F);
        this.itemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(item), count);
        if (this.itemStack.isEmpty()) {
            throw new JsonSyntaxException("Unknown item: " + item);
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
    public CapabilityType getType() {
        return CapabilityType.ITEM;
    }

    public float getCostChance() {
        return costChance;
    }

    public void setNoCost(){
        this.costChance = 0;
    }
}