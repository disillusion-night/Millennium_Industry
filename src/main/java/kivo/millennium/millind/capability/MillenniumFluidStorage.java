package kivo.millennium.millind.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Predicate;

public class MillenniumFluidStorage implements IFluidHandler, IMillenniumStorage {

    protected Predicate<FluidStack> validator;
    protected int size;
    @NotNull
    protected ArrayList<FluidStack> fluids;
    protected ArrayList<Integer> capacities;

    public MillenniumFluidStorage(int size, ArrayList<FluidStack> fluids, ArrayList<Integer> capacities){
        this.size = size;
        this.fluids = fluids;
        this.capacities = capacities;
    }

    public MillenniumFluidStorage(int capacity)
    {
        ArrayList<FluidStack> fluids = new ArrayList<FluidStack>(1);
        fluids.set(0, FluidStack.EMPTY);
        ArrayList<Integer> capa = new ArrayList<Integer>(1);
        capa.set(0, capacity);
        this.size = 1;
        this.fluids = fluids;
        this.capacities = capa;
    }

    public MillenniumFluidStorage(int size, int capacity){
        ArrayList<FluidStack> fluids = new ArrayList<FluidStack>(size);
        for (int i = 0; i < size; i++) {
            fluids.add(FluidStack.EMPTY);
        }
        ArrayList<Integer> capa = new ArrayList<Integer>(size);
        for (int i = 0; i < size; i++) {
            capa.add(capacity);
        }
        this.size = size;
        this.fluids = fluids;
        this.capacities = capa;
    }

    public MillenniumFluidStorage(int size, int... capacity){
        if (capacity.length != size)
            throw new RuntimeException("");

        ArrayList<FluidStack> fluids = new ArrayList<FluidStack>(size);
        for (int i = 0; i < size; i++) {
            fluids.add(FluidStack.EMPTY);
        }
        ArrayList<Integer> capa = new ArrayList<Integer>(size);
        for (int i = 0; i < capacity.length; i ++){
            capa.add(capacity[i]);
        }
        this.size = size;
        this.fluids = fluids;
        this.capacities = capa;
    }

    public MillenniumFluidStorage setCapacity(int tank, int capacity)
    {
        this.capacities.set(tank, capacity);
        return this;
    }

    public MillenniumFluidStorage setValidator(Predicate<FluidStack> validator)
    {
        if (validator != null) {
            this.validator = validator;
        }
        return this;
    }

    @NotNull
    public ArrayList<FluidStack> getFluids()
    {
        return fluids;
    }

    public int getFluidAmount(int tank)
    {
        return getFluidInTank(tank).getAmount();
    }

    public MillenniumFluidStorage readFromNBT(CompoundTag nbt) {
        size = nbt.getInt("size");
        ListTag tag = nbt.getList("fluids", Tag.TAG_COMPOUND);
        for(int i = 0; i < tag.size(); i++){
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(tag.getCompound(i));
            setFluidInTank(i, fluid);
        }
        return this;
    }

    public CompoundTag serializeNBT(){
        return writeToNBT(new CompoundTag());
    }

    public void deserializeNBT(CompoundTag nbt){
        this.readFromNBT(nbt);
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        nbt.putInt("size", size);
        ListTag tag = new ListTag();
        for(int i = 0; i < size; i++){
            CompoundTag fluid = new CompoundTag();
            getFluidInTank(i).writeToNBT(fluid);
            tag.add(i, fluid);
        }
        nbt.put("fluids", tag);

        return nbt;
    }

    @Override
    public int getTanks() {
        return size;
    }

    public FluidStack addFluid(FluidStack fluidStack){
        this.fluids.add(fluidStack);
        return fluidStack;
    }

    public boolean isEmpty(int tank){
        return fluids.get(tank).isEmpty();
    }

    public int addFluidToTank(int tank, FluidStack resource, FluidAction action){
        FluidStack stack = getFluidInTank(tank);
        int capacity = getTankCapacity(tank);

        if (resource.isEmpty() || !isFluidValid(tank, resource))
        {
            return 0;
        }
        if (action.simulate())
        {
            if (stack.isEmpty())
            {
                return Math.min(capacity, resource.getAmount());
            }
            if (!stack.isFluidEqual(resource))
            {
                return 0;
            }
            return Math.min(capacity - stack.getAmount(), resource.getAmount());
        }
        if (stack.isEmpty())
        {
            int amount = Math.min(capacity, resource.getAmount());
            setFluidInTank(tank, resource.getFluid(), amount);
            onContentsChanged(tank);
            return amount;
        }
        if (!stack.isFluidEqual(resource))
        {
            return 0;
        }
        int filled = capacity - stack.getAmount();

        if (resource.getAmount() < filled)
        {
            stack.grow(resource.getAmount());
            filled = resource.getAmount();
        }
        else
        {
            stack.setAmount(capacity);
        }
        if (filled > 0)
            onContentsChanged(tank);
        return filled;
    }
    public FluidStack setFluidInTank(int tank, FluidStack fluidStack){
        this.fluids.set(tank, fluidStack);
        return fluidStack;
    }

    public FluidStack setFluidInTank(int tank, Fluid fluid, int amount){
        FluidStack stack = new FluidStack(fluid, amount);
        this.fluids.set(tank, stack);
        return stack;
    }

    public FluidStack setEmptyInTank(int tank){
        this.fluids.set(tank, FluidStack.EMPTY);
        return FluidStack.EMPTY;
    }
    @NotNull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return this.fluids.get(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return capacities.get(tank);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        if(getFluidInTank(tank).isEmpty()){
            return true;
        }
        return getFluidInTank(tank).isFluidEqual(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        return addFluidToTank(size - 1, resource, action);
    }

    public int fill(int tank, FluidStack resource, FluidAction action)
    {
        return addFluidToTank(tank, resource, action);
    }

    public int drainFluidFromTank(int tank, int amount){
        int curr = getFluidAmount(tank);
        if(curr > amount){
            setFluidInTank(tank, getFluidInTank(tank).getFluid(), curr - amount);
            return amount;
        }else {
            setEmptyInTank(tank);
            return amount - curr;
        }
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        if (resource.isEmpty() || !resource.isFluidEqual(getFluidInTank(0)))
        {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        /*
        int drained = maxDrain;
        if (fluids.getAmount() < drained)
        {
            drained = fluids.getAmount();
        }
        FluidStack stack = new FluidStack(fluids, drained);
        if (action.execute() && drained > 0)
        {
            fluids.shrink(drained);
            onContentsChanged();
        }*/
        return FluidStack.EMPTY;
    }

    protected void onContentsChanged(int tank)
    {

    }

    public boolean isEmpty()
    {
        return size == 0;
    }
    /*
    public int getSpace()
    {
        return Math.max(0, capacities - fluids.getAmount());
    }*/

}
