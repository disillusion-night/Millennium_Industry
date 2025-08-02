package kivo.millennium.milltek.machine;

import static kivo.millennium.milltek.pipe.network.AbstractPipeBL.*;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public enum EIOState implements StringRepresentable {
    CONNECT,
    PULL,
    NONE,
    PUSH,
    DISCONNECTED;

    public static final EIOState[] VALUES = values();

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public boolean isConnected() {
        return this.equals(CONNECT) || this.equals(PULL) || this.equals(PUSH);
    }

    public static EnumProperty<EIOState> getPropertyForDirection(Direction direction) {
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