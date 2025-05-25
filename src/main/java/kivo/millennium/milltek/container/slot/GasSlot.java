package kivo.millennium.milltek.container.slot;

import kivo.millennium.milltek.gas.GasStack;
import kivo.millennium.milltek.storage.MillenniumGasStorage;

public class GasSlot {
    private final MillenniumGasStorage gasHandler;
    private final int tankIndex;
    private final int x, y, width, height;
    public static final int default_width = 16;
    public static final int default_height = 57;

    public GasSlot(MillenniumGasStorage gasHandler, int tankIndex, int x, int y, int width, int height) {
        this.gasHandler = gasHandler;
        this.tankIndex = tankIndex;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public GasSlot(MillenniumGasStorage gasHandler, int tankIndex, int x, int y) {
        this.gasHandler = gasHandler;
        this.tankIndex = tankIndex;
        this.x = x;
        this.y = y;
        this.width = default_width;
        this.height = default_height;
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
        return gasHandler.getTankCapacity(tankIndex);
    }

    public GasStack getGasStack() {
        return gasHandler.getGasInTank(tankIndex);
    }

    public MillenniumGasStorage getGasHandler() {
        return gasHandler;
    }

    public void setGasStack(GasStack stack) {
        gasHandler.setGasInTank(tankIndex, stack);
    }

    public int getTankIndex() {
        return tankIndex;
    }
}
