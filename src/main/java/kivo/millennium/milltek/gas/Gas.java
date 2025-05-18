package kivo.millennium.milltek.gas;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import kivo.millennium.milltek.init.MillenniumGases;

/**
 * 表示一种气体类型，类似于Fluid
 */
public class Gas {
  private final String name;
  private final int color;
  // 注册表引用，便于数据驱动、序列化、唯一性
  private Holder.Reference<Gas> builtInRegistryHolder;

  public Gas(@NotNull String name, int color) {
    this.name = name;
    this.color = color;
  }

  /**
   * 由注册表注入引用，mod注册时自动调用
   */
  public void setRegistryHolder(Holder.Reference<Gas> holder) {
    this.builtInRegistryHolder = holder;
  }

  @Nullable
  public Holder.Reference<Gas> getRegistryHolder() {
    return builtInRegistryHolder;
  }

  @Nullable
  public ResourceLocation getRegistryName() {
    if (builtInRegistryHolder != null) {
      return builtInRegistryHolder.key().location();
    }
    // 兼容未注入时的查找
    return MillenniumGases.getGas(this);
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
