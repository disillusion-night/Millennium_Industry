package kivo.millennium.millind.block.device.FusionFurnace;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class FusionFurnaceTank implements IFluidHandler {

    protected Predicate<FluidStack> validator;
    @NotNull
    protected FluidStack fluidIn = FluidStack.EMPTY;
    protected FluidStack fluidOut = FluidStack.EMPTY;
    protected int capacityIn;
    protected int capacityOut;

    public FusionFurnaceTank(int capacity)
    {
        this(capacity, e -> true);
    }

    public FusionFurnaceTank(int capacity, Predicate<FluidStack> validator)
    {
        this.capacityIn = capacity;
        this.capacityOut = capacity;
        this.validator = validator;
    }

    public FusionFurnaceTank setCapacity(int tank, int capacity)
    {
        this.capacityIn = capacity;
        this.capacityOut = capacity;
        return this;
    }

    public FusionFurnaceTank setValidator(Predicate<FluidStack> validator)
    {
        if (validator != null) {
            this.validator = validator;
        }
        return this;
    }

    public boolean isFluidValid(FluidStack stack)
    {
        return validator.test(stack);
    }

    public int getCapacityIn()
    {
        return capacityIn;
    }

    public int getCapacityOut()
    {
        return capacityOut;
    }

    @NotNull
    public FluidStack getFluidIn()
    {
        return fluidIn;
    }

    @NotNull
    public FluidStack getFluidOut()
    {
        return fluidOut;
    }

    public int getFluidAmountIn()
    {
        return fluidIn.getAmount();
    }

    public int getFluidAmountOut()
    {
        return fluidOut.getAmount();
    }

    public FusionFurnaceTank readFromNBT(CompoundTag nbt) {

        FluidStack fluidIn = FluidStack.loadFluidStackFromNBT(nbt);
        FluidStack fluidOut = FluidStack.loadFluidStackFromNBT(nbt);
        setFluidIn(fluidIn);
        setFluidOut(fluidOut);
        return this;
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        fluidIn.writeToNBT(nbt);
        fluidOut.writeToNBT(nbt);

        return nbt;
    }

    @Override
    public int getTanks() {

        return 2;
    }

    @NotNull
    @Override
    public FluidStack getFluidInTank(int tank) {
        if (tank == 0){
          return getFluidIn();
        }
        return getFluidOut();
    }

    @Override
    public int getTankCapacity(int tank) {

        if (tank == 0){
            return getCapacityIn();
        }
        return getCapacityOut();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {

        return isFluidValid(stack);
    }

    public int addToOut(FluidStack resource, FluidAction action){
        if (resource.isEmpty() || !isFluidValid(resource))
        {
            return 0;
        }
        if (action.simulate())
        {
            if (fluidOut.isEmpty())
            {
                return Math.min(capacityOut, resource.getAmount());
            }
            if (!fluidOut.isFluidEqual(resource))
            {
                return 0;
            }
            return Math.min(capacityOut - fluidOut.getAmount(), resource.getAmount());
        }
        if (fluidOut.isEmpty())
        {
            fluidOut = new FluidStack(resource, Math.min(capacityOut, resource.getAmount()));
            onContentsChanged();
            return fluidOut.getAmount();
        }
        if (!fluidOut.isFluidEqual(resource))
        {
            return 0;
        }
        int filled = capacityOut - fluidOut.getAmount();

        if (resource.getAmount() < filled)
        {
            fluidOut.grow(resource.getAmount());
            filled = resource.getAmount();
        }
        else
        {
            fluidOut.setAmount(capacityOut);
        }
        if (filled > 0)
            onContentsChanged();
        return filled;
        
    }
    
    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        if (resource.isEmpty() || !isFluidValid(resource))
        {
            return 0;
        }
        if (action.simulate())
        {
            if (fluidIn.isEmpty())
            {
                return Math.min(capacityIn, resource.getAmount());
            }
            if (!fluidIn.isFluidEqual(resource))
            {
                return 0;
            }
            return Math.min(capacityIn - fluidIn.getAmount(), resource.getAmount());
        }
        if (fluidIn.isEmpty())
        {
            fluidIn = new FluidStack(resource, Math.min(capacityIn, resource.getAmount()));
            onContentsChanged();
            return fluidIn.getAmount();
        }
        if (!fluidIn.isFluidEqual(resource))
        {
            return 0;
        }
        int filled = capacityIn - fluidIn.getAmount();

        if (resource.getAmount() < filled)
        {
            fluidIn.grow(resource.getAmount());
            filled = resource.getAmount();
        }
        else
        {
            fluidIn.setAmount(capacityIn);
        }
        if (filled > 0)
            onContentsChanged();
        return filled;
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        if (resource.isEmpty() || !resource.isFluidEqual(fluidIn))
        {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        int drained = maxDrain;
        if (fluidIn.getAmount() < drained)
        {
            drained = fluidIn.getAmount();
        }
        FluidStack stack = new FluidStack(fluidIn, drained);
        if (action.execute() && drained > 0)
        {
            fluidIn.shrink(drained);
            onContentsChanged();
        }
        return stack;
    }

    protected void onContentsChanged()
    {

    }

    public void setFluidIn(FluidStack stack)
    {
        this.fluidIn = stack;
    }

    public void setFluidOut(FluidStack stack)
    {
        this.fluidOut = stack;
    }

    public boolean isEmpty()
    {
        return fluidIn.isEmpty() && fluidOut.isEmpty();
    }

    public int getSpace()
    {
        return Math.max(0, capacityIn - fluidIn.getAmount());
    }

}
