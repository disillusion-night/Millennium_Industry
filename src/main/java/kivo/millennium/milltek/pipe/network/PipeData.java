package kivo.millennium.milltek.pipe.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;

import java.util.ArrayList;
import java.util.List;

public class PipeData {
    int capacity;
    int maxInputPerTick;
    int maxOutputPerTick;
    EPipeState NORTH;
    EPipeState EAST;
    EPipeState SOUTH;
    EPipeState WEST;
    EPipeState UP;
    EPipeState DOWN;

    public PipeData(int capacity) {
        this(capacity, capacity, capacity);
    }

    public PipeData(int capacity, int maxInputPerTick, int maxOutputPerTick) {
        this.capacity = capacity;
        this.NORTH = EPipeState.NONE;
        this.EAST = EPipeState.NONE;
        this.SOUTH = EPipeState.NONE;
        this.WEST = EPipeState.NONE;
        this.UP = EPipeState.NONE;
        this.DOWN = EPipeState.NONE;
        this.maxInputPerTick = maxInputPerTick;
        this.maxOutputPerTick = maxOutputPerTick;
    }

    public EPipeState getDOWN() {
        return DOWN;
    }
    public void setDOWN(EPipeState DOWN) {
        this.DOWN = DOWN;
    }
    public EPipeState getUP() {
        return UP;
    }
    public void setUP(EPipeState UP) {
        this.UP = UP;
    }
    public EPipeState getNORTH() {
        return NORTH;
    }
    public void setNORTH(EPipeState NORTH) {
        this.NORTH = NORTH;
    }
    public EPipeState getEAST() {
        return EAST;
    }
    public void setEAST(EPipeState EAST) {
        this.EAST = EAST;
    }
    public EPipeState getSOUTH() {
        return SOUTH;
    }
    public void setSOUTH(EPipeState SOUTH) {
        this.SOUTH = SOUTH;
    }
    public EPipeState getWEST() {
        return WEST;
    }
    public void setWEST(EPipeState WEST) {
        this.WEST = WEST;
    }
    public int getCapacity() {
        return capacity;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    protected static Byte getByteFromState(EPipeState state) {
        return switch (state) {
            case NONE -> 0;
            case CONNECT -> 1;
            case PULL -> 2;
            case PUSH -> 3;
            case DISCONNECT -> 4;
            case PIPE -> 5;
        };
    }

    public EPipeState getStateFromDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
            default -> EPipeState.NONE; // 默认返回 NONE
        };
    }

    protected static EPipeState getStateFromByte(Byte state) {
        return switch (state) {
            case 0 -> EPipeState.NONE;
            case 1 -> EPipeState.CONNECT;
            case 2 -> EPipeState.PULL;
            case 3 -> EPipeState.PUSH;
            case 4 -> EPipeState.DISCONNECT;
            case 5 -> EPipeState.PIPE;
            default -> EPipeState.NONE; // 默认返回 NONE
        };
    }

    public CompoundTag writeToNBT() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt("capacity", capacity);
        compoundTag.putByte("NORTH",getByteFromState(NORTH));
        compoundTag.putByte("SOUTH", getByteFromState(SOUTH));
        compoundTag.putByte("EAST", getByteFromState(EAST));
        compoundTag.putByte("WEST", getByteFromState(WEST));
        compoundTag.putByte("UP", getByteFromState(UP));
        compoundTag.putByte("DOWN", getByteFromState(DOWN));
        compoundTag.putInt("maxInputPerTick", maxInputPerTick);
        compoundTag.putInt("maxOutputPerTick", maxOutputPerTick);
        return compoundTag;
    }

    public List<Direction> getConnectedDirections() {
        List<Direction> connectedDirections = new ArrayList<>();
        if (NORTH == EPipeState.CONNECT) connectedDirections.add(Direction.NORTH);
        if (SOUTH == EPipeState.CONNECT) connectedDirections.add(Direction.SOUTH);
        if (EAST == EPipeState.CONNECT) connectedDirections.add(Direction.EAST);
        if (WEST == EPipeState.CONNECT) connectedDirections.add(Direction.WEST);
        if (UP == EPipeState.CONNECT) connectedDirections.add(Direction.UP);
        if (DOWN == EPipeState.CONNECT) connectedDirections.add(Direction.DOWN);
        return connectedDirections;
    }

    public void setStateFromDirection(Direction direction, EPipeState state) {
    if (direction == null || state == null) {
            throw new IllegalArgumentException("Direction and state cannot be null");
        }
        switch (direction) {
            case NORTH -> this.NORTH = state;
            case SOUTH -> this.SOUTH = state;
            case EAST -> this.EAST = state;
            case WEST -> this.WEST = state;
            case UP -> this.UP = state;
            case DOWN -> this.DOWN = state;
        }
    }
    
    public boolean isConnectedTo(Direction direction) {
        EPipeState state = getStateFromDirection(direction);
        return state == EPipeState.CONNECT || state == EPipeState.PULL || 
               state == EPipeState.PUSH || state == EPipeState.PIPE;
    }

    public PipeData(CompoundTag tag) {
        this.capacity = tag.getInt("capacity");
        this.NORTH = getStateFromByte(tag.getByte("NORTH"));
        this.SOUTH = getStateFromByte(tag.getByte("SOUTH"));
        this.EAST = getStateFromByte(tag.getByte("EAST"));
        this.WEST = getStateFromByte(tag.getByte("WEST"));
        this.UP = getStateFromByte(tag.getByte("UP"));
        this.DOWN = getStateFromByte(tag.getByte("DOWN"));
        this.maxInputPerTick = tag.getInt("maxInputPerTick");
        this.maxOutputPerTick = tag.getInt("maxOutputPerTick");
    }
}
