package kivo.millennium.milltek.pipe.network;

import net.minecraft.util.StringRepresentable;

public enum EPipeState implements StringRepresentable {
    DISCONNECT,
    CONNECT,
    PUSH,
    PULL,
    PIPE,
    NONE;

    public boolean isConnected() {
        return this == CONNECT || this == PUSH || this == PULL || this == PIPE;
    }

    public boolean isNormal(){
        return this == CONNECT || this == PIPE;
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }
}
