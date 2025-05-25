package kivo.millennium.milltek.block.container.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class ContainerFluidTank implements IFluidHandler {
    private FluidStack fluid = FluidStack.EMPTY;
    private final int capacity;
    private final int maxInput;
    private final int maxOutput;

    public ContainerFluidTank(int capacity, int maxInput, int maxOutput) {
        this.capacity = capacity;
        this.maxInput = maxInput;
        this.maxOutput = maxOutput;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return fluid.copy();
    }

    @Override
    public int getTankCapacity(int tank) {
        return capacity;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return 0;
        if (!isFluidValid(0, resource)) return 0;
        int space = capacity - fluid.getAmount();
        int toFill = Math.min(space, Math.min(resource.getAmount(), maxInput));
        if (toFill > 0 && action.execute()) {
            if (fluid.isEmpty()) {
                fluid = new FluidStack(resource, toFill);
            } else if (fluid.isFluidEqual(resource)) {
                fluid.grow(toFill);
            }
        }
        return toFill;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (fluid.isEmpty()) return FluidStack.EMPTY;
        int toDrain = Math.min(fluid.getAmount(), Math.min(maxDrain, maxOutput));
        FluidStack drained = new FluidStack(fluid, toDrain);
        if (toDrain > 0 && action.execute()) {
            fluid.shrink(toDrain);
            if (fluid.getAmount() <= 0) fluid = FluidStack.EMPTY;
        }
        return drained;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (fluid.isEmpty() || !fluid.isFluidEqual(resource)) return FluidStack.EMPTY;
        return drain(resource.getAmount(), action);
    }
}
