package kivo.millennium.millind.recipe;


import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class FluidComponent implements RecipeComponent {
    @Nullable
    private FluidStack fluidStack;

    public FluidComponent(@Nullable FluidStack fluidStack) {
        this.fluidStack = fluidStack;
    }

    public FluidStack getFluidStack() {
        return fluidStack == null ? FluidStack.EMPTY : fluidStack;
    }

    @Override
    public void writeToJson(JsonObject jsonObject) {

    }

    @Override
    public void readFromJson(JsonObject jsonObject) {
        if (jsonObject.has("fluid")) {
            JsonObject fluidObject = GsonHelper.getAsJsonObject(jsonObject, "fluid");
            ResourceLocation fluidName = new ResourceLocation(GsonHelper.getAsString(fluidObject, "name"));
            int amount = GsonHelper.getAsInt(fluidObject, "amount");
            this.fluidStack = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluidName), amount);
            if (this.fluidStack.isEmpty()) {
                throw new JsonSyntaxException("Unknown fluid: " + fluidName);
            }
        } else {
            throw new JsonSyntaxException("Expected 'fluid' object");
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
    public String getType() {
        return "fluid";
    }
}