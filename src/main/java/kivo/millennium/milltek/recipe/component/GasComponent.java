package kivo.millennium.milltek.recipe.component;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import kivo.millennium.milltek.capability.CapabilityType;
import kivo.millennium.milltek.gas.GasStack;
import kivo.millennium.milltek.init.MillenniumGases;
import kivo.millennium.milltek.recipe.RecipeComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

public class GasComponent implements RecipeComponent<GasStack> {
    @Nullable
    private GasStack gasStack;

    public GasComponent(@Nullable GasStack gasStack) {
        this.gasStack = gasStack;
    }

    public GasComponent(JsonObject jsonObject) {
        String gasName = GsonHelper.getAsString(jsonObject, "gas");
        int amount = GsonHelper.getAsInt(jsonObject, "amount");
        this.gasStack = new GasStack(MillenniumGases.getGasById(gasName), amount);
    }

    @Override
    public GasStack get() {
        return gasStack == null ? GasStack.EMPTY : gasStack;
    }

    @Override
    public boolean matches(RecipeComponent component) {
        if (component instanceof GasComponent gasComponent) {
            GasStack stack = gasComponent.get();
            return stack.getGas().equals(get().getGas()) && stack.getAmount() <= get().getAmount();
        }
        return false;
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("gas", get().getGas().getRegistryName().toString());
        jsonObject.addProperty("amount", get().getAmount());
        return jsonObject;
    }

    @Override
    public void readFromJson(JsonObject jsonObject) {
        String gasName = GsonHelper.getAsString(jsonObject, "gas");
        int amount = GsonHelper.getAsInt(jsonObject, "amount");
        this.gasStack = MillenniumGases.GASES.getEntries().stream()
                .filter(e -> e.getId().toString().equals(gasName))
                .findFirst()
                .map(e -> new GasStack(e.get(), amount))
                .orElseThrow(() -> new JsonSyntaxException("Unknown gas: " + gasName));
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        if (gasStack == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeUtf(gasStack.getGas().getRegistryName().getPath());
            buf.writeInt(gasStack.getAmount());
        }
    }

    @Override
    public void readFromNetwork(FriendlyByteBuf buf) {
        if (buf.readBoolean()) {
            String gas = buf.readUtf();
            int amount = buf.readInt();
            this.gasStack = new GasStack(MillenniumGases.getGas(new ResourceLocation(gas)), amount);
        } else {
            this.gasStack = GasStack.EMPTY;
        }
    }

    @Override
    public CapabilityType getType() {
        return CapabilityType.GAS;
    }

    public GasComponent asGasComponent() {
        return this;
    }
}
