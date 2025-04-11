package kivo.millennium.millind.pipe.client;

import kivo.millennium.millind.cables.ConnectorType;
import net.minecraft.util.StringRepresentable;

public enum EPipeState implements StringRepresentable {
    CONNECT,
    INSERT,
    NONE,
    OUTPUT,
    DISCONNECTED;

    public static final EPipeState[] VALUES = values();

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public boolean isConnected(){
        return this.equals(CONNECT) || this.equals(INSERT) || this.equals(OUTPUT);
    }

    @Override
    public String getSerializedName() {
        return toString();
    }
}