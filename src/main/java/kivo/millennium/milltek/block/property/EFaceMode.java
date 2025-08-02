package kivo.millennium.milltek.block.property;

import net.minecraft.util.StringRepresentable;

public enum EFaceMode implements StringRepresentable {
    NONE("none"), // 可被访问，但不主动输入/输出
    PULL("pull"), // 主动输入
    PUSH("push"), // 主动输出
    DISCONNECT("disconnect"); // 完全不可访问

    private final String name;

    EFaceMode(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}