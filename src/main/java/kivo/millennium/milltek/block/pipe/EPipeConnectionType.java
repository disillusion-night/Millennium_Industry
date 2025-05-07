package kivo.millennium.milltek.block.pipe;

import net.minecraft.util.StringRepresentable;

import javax.annotation.Nonnull;

public enum EPipeConnectionType implements StringRepresentable {
    NONE,
    PIPE,
    BLOCK;

    public static final EPipeConnectionType[] VALUES = values();

    @Override
    @Nonnull
    public String getSerializedName() {
        return name().toLowerCase();
    }
}

