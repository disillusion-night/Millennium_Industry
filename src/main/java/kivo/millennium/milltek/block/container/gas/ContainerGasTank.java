package kivo.millennium.milltek.block.container.gas;

import kivo.millennium.milltek.gas.IGasHandler;
import kivo.millennium.milltek.gas.IGasHandler.GasAction;
import kivo.millennium.milltek.gas.GasStack;

public class ContainerGasTank implements IGasHandler {
    private GasStack gas = GasStack.EMPTY;
    private final int capacity;
    private final int maxInput;
    private final int maxOutput;

    public ContainerGasTank(int capacity, int maxInput, int maxOutput) {
        this.capacity = capacity;
        this.maxInput = maxInput;
        this.maxOutput = maxOutput;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public GasStack getGasInTank(int tank) {
        return gas.copy();
    }

    @Override
    public int getTankCapacity(int tank) {
        return capacity;
    }

    @Override
    public boolean isGasValid(int tank, GasStack stack) {
        return true;
    }

    @Override
    public int fill(GasStack resource, GasAction action) {
        if (resource.isEmpty())
            return 0;
        if (!isGasValid(0, resource))
            return 0;
        int space = capacity - gas.getAmount();
        int toFill = Math.min(space, Math.min(resource.getAmount(), maxInput));
        if (toFill > 0 && action.execute()) {
            if (gas.isEmpty()) {
                gas = resource.copy();
                gas.setAmount(toFill);
            } else if (gas.isGasEqual(resource)) {
                gas.grow(toFill);
            }
        }
        return toFill;
    }

    @Override
    public GasStack drain(int maxDrain, GasAction action) {
        if (gas.isEmpty())
            return GasStack.EMPTY;
        int toDrain = Math.min(gas.getAmount(), Math.min(maxDrain, maxOutput));
        GasStack drained = gas.copy();
        drained.setAmount(toDrain);
        if (toDrain > 0 && action.execute()) {
            gas.shrink(toDrain);
            if (gas.getAmount() <= 0)
                gas = GasStack.EMPTY;
        }
        return drained;
    }

    @Override
    public GasStack drain(GasStack resource, GasAction action) {
        if (gas.isEmpty() || !gas.isGasEqual(resource))
            return GasStack.EMPTY;
        return drain(resource.getAmount(), action);
    }
}
