package kivo.millennium.milltek.gas;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import kivo.millennium.milltek.init.MillenniumGases;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 气体堆栈，类似于FluidStack，包含气体类型和数量。
 */
public class GasStack {
    public static final GasStack EMPTY = new GasStack(MillenniumGases.EMPTY.get(), 0, true);
    private boolean isEmpty;
    public static final Codec<GasStack> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("Gas").forGetter(stack -> stack.getGas().getRegistryName()),
                    Codec.INT.fieldOf("Amount").forGetter(GasStack::getAmount)).apply(instance, (rl, amount) -> {
                        Gas gas = MillenniumGases.getGasById(rl.toString());
                        return gas == null ? GasStack.EMPTY : new GasStack(gas, amount);
                    }));
    private final Gas gas;
    private int amount;

    private GasStack(@NotNull Gas gas, int amount, boolean isEmpty) {
        this.gas = gas;
        this.amount = amount;
        this.isEmpty = isEmpty;
    }

    public GasStack(@NotNull Gas gas, int amount) {
        this.gas = gas;
        this.amount = amount;
        this.isEmpty = true;
    }

    public void grow(int amount) {
        this.amount += amount;
    }

    public void shrink(int amount) {
        this.amount -= amount;
        if (this.amount < 0) this.amount = 0;
    }

    @NotNull
    public Gas getGas() {
        return gas;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isEmpty() {
        return isEmpty || (amount <= 0 && gas == MillenniumGases.EMPTY.get());
    }

    protected void updateEmpty(){
        if (isEmpty()) isEmpty = true;
    }

    public GasStack copy() {
        return new GasStack(gas, amount);
    }

    public CompoundTag writeToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Gas", MillenniumGases.getRL(gas).toString());
        tag.putInt("Amount", amount);
        return tag;
    }

    public static GasStack readFromNBT(CompoundTag tag) {
        String name = tag.getString("Gas");
        int amount = tag.getInt("Amount");
        if (name.isEmpty())
            return EMPTY;
        Gas gas = MillenniumGases.getGasById(name);
        if (gas == null)
            return null;
        return new GasStack(gas, amount);
    }

    public static GasStack readFromPacket(FriendlyByteBuf buf) {
        Gas gas = buf.readRegistryId();
        int amount = buf.readVarInt();
        if (gas == null) {
            return EMPTY;
        } else {
            return new GasStack(gas, amount);
        }
    }

    public void writeToPacket(FriendlyByteBuf buf) {
        buf.writeRegistryId(MillenniumGases.GAS_REGISTRY.get(), getGas());
        buf.writeVarInt(this.getAmount());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        GasStack gasStack = (GasStack) o;
        return amount == gasStack.amount && gas.equals(gasStack.gas);
    }

    public boolean isGasEqual(GasStack other) {
        return this.gas.equals(other.gas);
    }

    @Override
    public int hashCode() {
        int result = gas.hashCode();
        result = 31 * result + amount;
        return result;
    }
}
