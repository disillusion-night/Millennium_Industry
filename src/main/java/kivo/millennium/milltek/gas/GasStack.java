package kivo.millennium.milltek.gas;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import kivo.millennium.milltek.init.MillenniumGases;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

/**
 * 气体堆栈，类似于FluidStack，包含气体类型和数量。
 */
public class GasStack {
  public static final GasStack EMPTY = new GasStack(new Gas("empty", 0), 0);
  private final Gas gas;
  private int amount;

  public GasStack(@NotNull Gas gas, int amount) {
    this.gas = gas;
    this.amount = amount;
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
    return amount <= 0;
  }

  public GasStack copy() {
    return new GasStack(gas, amount);
  }

  public CompoundTag writeToNBT() {
    CompoundTag tag = new CompoundTag();
    tag.putString("Gas", gas.getRegistryName().toString());
    tag.putInt("Amount", amount);
    return tag;
  }

  public static GasStack readFromNBT(CompoundTag tag) {
    String name = tag.getString("Gas");
    int amount = tag.getInt("Amount");
    Gas gas = MillenniumGases.GASES.getEntries().stream()
        .filter(e -> e.getId().getPath().equals(name))
        .findFirst()
        .map(RegistryObject::get)
        .orElse(null);
    if (gas == null)
      return null;
    return new GasStack(gas, amount);
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

  @Override
  public int hashCode() {
    int result = gas.hashCode();
    result = 31 * result + amount;
    return result;
  }
}
