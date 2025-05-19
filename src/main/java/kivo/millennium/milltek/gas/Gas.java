package kivo.millennium.milltek.gas;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;
import kivo.millennium.milltek.init.MillenniumGases;

/**
 * 表示一种气体类型，类似于Fluid
 */
public class Gas {
  private final int color;
  // 注册表引用
  private Holder.Reference<Gas> builtInRegistryHolder;

  public Gas(int color) {
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
    ResourceLocation rl1 = this.getRegistryName();
    ResourceLocation rl2 = gas.getRegistryName();
    return rl1 != null && rl1.equals(rl2);
  }

  @Override
  public int hashCode() {
    ResourceLocation rl = getRegistryName();
    return rl != null ? rl.hashCode() : 0;
  }
}
