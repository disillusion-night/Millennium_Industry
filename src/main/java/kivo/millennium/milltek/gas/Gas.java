package kivo.millennium.milltek.gas;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 表示一种气体类型，类似于Fluid，但不与实际方块关联。
 */
public class Gas {
  private final String name;
  private final int color;

  public Gas(@NotNull String name, int color) {
    this.name = name;
    this.color = color;
  }

  @NotNull
  public String getName() {
    return name;
  }

  public int getColor() {
    return color;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Gas gas = (Gas) o;
    return name.equals(gas.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
