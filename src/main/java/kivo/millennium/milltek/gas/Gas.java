package kivo.millennium.milltek.gas;

import static kivo.millennium.milltek.Main.MODID;

import kivo.millennium.milltek.init.MillenniumGases;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * 表示一种气体类型，类似于Fluid
 */
public class Gas {
    private final int color;

    public Gas(int color) {
        this.color = color;
    }

    public ResourceLocation getRegistryName() {
        return MillenniumGases.getRL(this);
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
        return getRegistryName().equals(gas.getRegistryName());
    }

    public Component getDisplayName() {
        return Component.translatable(getDescriptionID());
    }

    public String getDescriptionID() {
        return Util.makeDescriptionId("gas", getRegistryName());
    }
}
