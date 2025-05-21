package kivo.millennium.milltek.gas;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * 表示一种气体类型，类似于Fluid
 */
public class Gas {
    private final int color;
    private final String id; // 形如 "milltek:oxygen"

    public Gas(String id, int color) {
        this.id = id;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public ResourceLocation getRegistryName() {
        return new ResourceLocation("milltek", id);
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
        return id.equals(gas.id);
    }

    public Component getDisplayName() {
        return Component.translatable(getDescriptionID());
    }

    public String getDescriptionID() {
        return Util.makeDescriptionId("gas", getRegistryName());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
