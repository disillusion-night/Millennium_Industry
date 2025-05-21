package kivo.millennium.milltek.container.slot;

import kivo.millennium.milltek.gas.GasStack;
import kivo.millennium.milltek.storage.MillenniumGasStorage;

public class GasSlot {
    private final MillenniumGasStorage gasHandler;
    private final int tankIndex;
    private final int x, y, width, height;
    private GasStack gasStack = GasStack.EMPTY;
    private int capacity;

    public GasSlot(MillenniumGasStorage gasHandler, int tankIndex, int x, int y, int width, int height) {
        this.gasHandler = gasHandler;
        this.tankIndex = tankIndex;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.capacity = gasHandler.getTankCapacity(tankIndex);
        this.gasStack = gasHandler.getGasInTank(tankIndex);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getGasCapacity() {
        return capacity;
    }

    public GasStack getGasStack() {
        return gasStack;
    }

    public MillenniumGasStorage getGasHandler() {
        return gasHandler;
    }

    public void setGasStack(GasStack stack) {
        this.gasStack = stack;
    }
}
