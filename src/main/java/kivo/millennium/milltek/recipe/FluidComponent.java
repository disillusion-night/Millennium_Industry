package kivo.millennium.milltek.recipe;


import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import kivo.millennium.milltek.capability.CapabilityType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

public class FluidComponent implements RecipeComponent<FluidStack> {
    @Nullable
    private FluidStack fluidStack;

    public FluidComponent(@Nullable FluidStack fluidStack) {
        this.fluidStack = fluidStack;
    }

    public FluidComponent(JsonObject jsonObject) {
        ResourceLocation fluidName = new ResourceLocation(GsonHelper.getAsString(jsonObject, "fluid"));
        int amount = GsonHelper.getAsInt(jsonObject, "amount");
        this.fluidStack = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluidName), amount);
        if (this.fluidStack.isEmpty()) {
            throw new JsonSyntaxException("Unknown fluid: " + fluidName);
        }
    }

    @Override
    public FluidStack get() {
        return fluidStack == null ? FluidStack.EMPTY : fluidStack;
    }

    @Override
    public <R extends RecipeComponent> boolean matches(R component) {
        if(component instanceof FluidComponent fluidComponent){
            FluidStack stack = fluidComponent.get();
            return stack.containsFluid(fluidStack);
        }
        return false;
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
    public CapabilityType getType() {
        return CapabilityType.FLUID;
    }
}