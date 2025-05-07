package kivo.millennium.milltek.pipe.client;

import static kivo.millennium.milltek.pipe.client.AbstractPipeBL.*;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;

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


    public static EnumProperty<EPipeState> getPropertyForDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }
    @Override
    public String getSerializedName() {
        return toString();
    }
}