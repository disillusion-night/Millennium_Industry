package kivo.millennium.milltek.pipe.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class PipeFluidStorage implements IFluidHandler, INBTSerializable<CompoundTag>, IPipeStorage<PipeFluidStorage> {

    private int capacity;
    private FluidStack fluidStack;

    public PipeFluidStorage(int capacity) {
        this.capacity = capacity;
        this.fluidStack = FluidStack.EMPTY;
    }

    public PipeFluidStorage(int capacity, FluidStack fluidStack) {
        this.capacity = capacity;
        this.fluidStack = fluidStack;
    }

    public PipeFluidStorage(CompoundTag tag) {
        this.capacity = tag.getInt("capacity");
        if (tag.contains("fluid", Tag.TAG_COMPOUND)) {
            this.fluidStack = FluidStack.loadFluidStackFromNBT(tag.getCompound("fluid"));
        } else {
            this.fluidStack = FluidStack.EMPTY;
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("capacity", capacity);
        if (fluidStack != null) {
            tag.put("fluid", fluidStack.writeToNBT(new CompoundTag()));
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.capacity = tag.getInt("capacity");
        if (tag.contains("fluid", Tag.TAG_COMPOUND)) {
            this.fluidStack = FluidStack.loadFluidStackFromNBT(tag.getCompound("fluid"));
        } else {
            this.fluidStack = FluidStack.EMPTY;
        }
    }

    @Override
    public int getTanks() {
        return 1;
    }

    public int getCapacity(){
        return capacity;
    }

    public FluidStack getFluid(){
        return fluidStack;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return fluidStack;
    }

    @Override
    public int getTankCapacity(int tank) {
        return capacity;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return tank == 0 && (fluidStack.isEmpty() || fluidStack.isFluidEqual(stack));
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || resource.getAmount() <= 0 || !isFluidValid(0, resource)) {
            return 0;
        }
        int fillableAmount = Math.min(capacity - fluidStack.getAmount(), resource.getAmount());
        if (fillableAmount <= 0) {
            return 0;
        }
        if (action.execute()) {
            if (fluidStack.isEmpty()) {
                fluidStack = resource.copy();
                fluidStack.setAmount(fillableAmount);
            } else {
                fluidStack.grow(fillableAmount);
            }
        }
        return fillableAmount;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !fluidStack.isFluidEqual(resource)) {
            return FluidStack.EMPTY;
        }
        int drainedAmount = Math.min(resource.getAmount(), fluidStack.getAmount());
        FluidStack stack_out = new FluidStack(fluidStack, drainedAmount);
        if (action.execute()) {
            fluidStack.shrink(drainedAmount);
        }
        return stack_out;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        if (fluidStack.isEmpty() || maxDrain <= 0) {
            return FluidStack.EMPTY;
        }
        int drainedAmount = Math.min(maxDrain, fluidStack.getAmount());
        FluidStack stack_out = new FluidStack(this.fluidStack, drainedAmount);
        if (action.execute()) fluidStack.shrink(drainedAmount);
        return stack_out;
    }

    public PipeFluidStorage setCapacity(int newCapacity) {
        if (newCapacity < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative");
        }
        int oldCapacity = this.capacity;
        this.capacity = newCapacity;
        fluidStack.setAmount(Math.min(fluidStack.getAmount(), newCapacity));
        return this;
    }

    @Override
    public PipeFluidStorage merge(PipeFluidStorage other) {
        this.capacity += other.capacity;
        if (this.fluidStack.isEmpty()) {
            this.fluidStack = other.fluidStack.copy();
            other.clear();
            return this;
        }
        if (other.fluidStack.isEmpty()) {
            return this;
        }
        this.fluidStack.setAmount(Math.min(this.fluidStack.getAmount() + other.fluidStack.getAmount(), this.capacity));
        other.clear();
        return this;
    }

    @Override
    public void clear() {
        capacity = 0;
        fluidStack = FluidStack.EMPTY;
    }
}
