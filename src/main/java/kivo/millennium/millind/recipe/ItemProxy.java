package kivo.millennium.millind.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import kivo.millennium.millind.capability.CapabilityType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemProxy implements ISlotProxy<ItemStack> {
    private ItemStack itemStack;
    //private float costChance;
    private int capability;
    private int damage;


    public ItemProxy(){
        this.itemStack = ItemStack.EMPTY;
        this.capability = 64;
        this.damage = 0;
    }

    public ItemProxy(ItemStack itemStack){
        this.itemStack = itemStack;
        this.capability = 64;
        this.damage = 0;
    }

    public void  setDamage(int damage){
        this.damage = damage;
    }

    public int getDamage(){
        return this.damage;
    }

    /*public void setCostChance(float costChance){
        this.costChance = costChance;
    }

    public float getCostChance(){
        return this.costChance;
    }*/

    public void set(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack get() {
        return itemStack;
    }

    @Override
    public ItemStack shrink(int amount) {
        itemStack.shrink(amount);
        return itemStack;
    }

    @Override
    public ItemStack grow(int amount){
        itemStack.grow(amount);
        return itemStack;
    }

    @Override
    public boolean hasPlaceFor(ISlotProxy slotProxy) {
        if (slotProxy instanceof ItemProxy itemProxy){
            if(itemStack.isEmpty()) return true;
            return itemStack.getCount() + itemProxy.getAmount() <= capability;
        }
        return false;
    }

    @Override
    public void clear() {
        this.itemStack = ItemStack.EMPTY;
    }

    @Override
    public CapabilityType getType() {
        return CapabilityType.ITEM;
    }

    @Override
    public boolean contains(ISlotProxy proxy) {
        if (proxy instanceof ItemProxy itemProxy){
            return itemStack.is(itemProxy.get().getItem()) && getAmount() >= itemProxy.getAmount();
        }else {
            return false;
        }
    }

    @Override
    public boolean remove(ISlotProxy proxy) {
        if (proxy instanceof ItemProxy itemProxy){
            if(this.itemStack.is(itemProxy.get().getItem()) && getAmount() >= itemProxy.getAmount()){
                this.itemStack.shrink(itemProxy.getAmount());
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    @Override
    public boolean add(ISlotProxy proxy) {
        if (proxy instanceof ItemProxy itemProxy){
            if(this.itemStack.isEmpty()){
                this.itemStack = itemProxy.get();
                return true;
            }
            if(this.itemStack.getCount() + itemProxy.getAmount() <= capability ){
                this.itemStack.grow(itemProxy.getAmount());
                return true;
            }else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean isEmpty() {
        return itemStack.isEmpty();
    }

    @Override
    public int getAmount() {
        return itemStack.getCount();
    }

    @Override
    public void setSlotLimit(int limit) {
        this.capability = limit;
    }


    @Override
    public int getSlotLimit() {
        return this.capability;
    }

    @Override
    public ItemComponent getAsRecipeComponent() {
        return ItemComponent.of(itemStack);
    }

    @Override
    public ItemProxy of(ItemStack stack) {
        return new ItemProxy(stack);
    }


    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("item", BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString());
        if (itemStack.getCount() > 1) {
            jsonObject.addProperty("count", itemStack.getCount());
        }
        if (itemStack.hasTag()) {
            jsonObject.addProperty("nbt", itemStack.getTag().toString());
        }
        if (damage > 0) {
            jsonObject.addProperty("damage", damage);
        }
        //if ()
        return jsonObject;
    }

    @Override
    public void readFromJson(JsonObject jsonObject) {
        ResourceLocation item = new ResourceLocation(GsonHelper.getAsString(jsonObject, "item"));
        int count = GsonHelper.getAsInt(jsonObject, "count", 1);
        int damage = GsonHelper.getAsInt(jsonObject,"damage", 0);
        this.itemStack = new ItemStack(ForgeRegistries.ITEMS.getValue(item), count);
        this.damage = damage;
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
}
