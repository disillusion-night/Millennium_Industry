package kivo.millennium.millind.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import kivo.millennium.millind.capability.CapabilityType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidProxy implements ISlotProxy<FluidStack> {
    private FluidStack fluidStack;
    private int capability;

    public FluidProxy(FluidStack fluidStack){
        this.fluidStack = fluidStack;
        this.capability = 16000;
    }

    public FluidProxy(){
        this.fluidStack = FluidStack.EMPTY;
        this.capability = 16000;
    }

    public FluidStack shrink(int amount){
        fluidStack.shrink(amount);
        return fluidStack;
    }

    public FluidStack grow(int amount){
        fluidStack.grow(amount);
        return fluidStack;
    }

    @Override
    public void clear() {
        this.fluidStack = FluidStack.EMPTY;
    }

    @Override
    public CapabilityType getType() {
        return CapabilityType.FLUID;
    }

    @Override
    public boolean contains(ISlotProxy proxy) {
        if (proxy instanceof FluidProxy fluidProxy){
            return fluidStack.containsFluid(fluidProxy.get());
        } else {
            return false;
        }
    }

    @Override
    public boolean remove(ISlotProxy proxy) {
        if (proxy instanceof FluidProxy fluidProxy){
            if( fluidStack.containsFluid(fluidProxy.get()) ){
                fluidStack.shrink(fluidProxy.getAmount());
                return true;
            }else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean add(ISlotProxy proxy) {
        if (proxy instanceof FluidProxy fluidProxy){
            if(this.fluidStack.isEmpty()){
                this.fluidStack = fluidProxy.get();
                return true;
            }
            if(this.fluidStack.getAmount() + fluidProxy.getAmount() <= capability ){
                this.fluidStack.grow(fluidProxy.getAmount());
                return true;
            }else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("fluid", BuiltInRegistries.FLUID.getKey(fluidStack.getFluid()).toString());
        jsonObject.addProperty("amount", fluidStack.getAmount());
        return jsonObject;
    }

    @Override
    public void readFromJson(JsonObject jsonObject) {
        ResourceLocation fluidName = new ResourceLocation(GsonHelper.getAsString(jsonObject, "fluid"));
        int amount = GsonHelper.getAsInt(jsonObject, "amount");
        this.fluidStack = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluidName), amount);
        if (this.fluidStack.isEmpty()) {
            throw new JsonSyntaxException("Unknown fluid: " + fluidName);
        }
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        if (fluidStack == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            fluidStack.writeToPacket(buf);
        }
    }

    @Override
    public void readFromNetwork(FriendlyByteBuf buf) {
        if (buf.readBoolean()) {
            this.fluidStack = FluidStack.readFromPacket(buf);
        } else {
            this.fluidStack = FluidStack.EMPTY;
        }
    }

    @Override
    public boolean isEmpty() {
        return fluidStack.isEmpty();
    }

    @Override
    public int getAmount() {
        return fluidStack.getAmount();
    }

    @Override
    public void setSlotLimit(int limit) {
        this.capability = limit;
    }


    @Override
    public int getSlotLimit() {
        return capability;
    }

    @Override
    public boolean hasPlaceFor(ISlotProxy slotProxy) {
        if (slotProxy instanceof FluidProxy fluidProxy){
            if(fluidStack.isEmpty()) return true;
            return fluidStack.getAmount() + fluidProxy.getAmount() <= capability;
        }
        return false;
    }

    @Override
    public FluidComponent getAsRecipeComponent() {
        return FluidComponent.of(fluidStack);
    }

    @Override
    public FluidProxy of(FluidStack stack) {
        return new FluidProxy(stack);
    }

    @Override
    public FluidStack get() {
        return fluidStack;
    }

    @Override
    public void set(FluidStack stack) {
        this.fluidStack = stack;
    }
}
