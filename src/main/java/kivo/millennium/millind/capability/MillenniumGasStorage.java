package kivo.millennium.millind.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

// Assuming IMillenniumStorage provides serializeNBT and deserializeNBT

public class MillenniumGasStorage implements IFluidHandler, IMillenniumStorage {

    protected Predicate<FluidStack> validator = f -> true;

    protected int size;

    @NotNull
    protected List<FluidStack> fluids;
    protected List<Integer> capacities;

    // New field for tank access levels (0: None, 1: Fill, 2: Drain, 3: Both)
    protected List<Integer> tankAccess; // Use List<Integer>

    @Nullable
    private Consumer<CapabilityType> onContentsChangedCallback;


    // --- Constructors ---

    // Helper to initialize tankAccess list
    private List<Integer> createDefaultTankAccess(int size) {
        List<Integer> defaultAccess = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            defaultAccess.add(3); // Default access is 3 (Both)
        }
        return defaultAccess;
    }

    // Constructor for initializing with existing lists (use cautiously)
    public MillenniumGasStorage(int size, @NotNull List<FluidStack> fluids, @NotNull List<Integer> capacities, @NotNull List<Integer> tankAccess){ // Added tankAccess parameter
        if (fluids.size() != size || capacities.size() != size || tankAccess.size() != size) {
            throw new IllegalArgumentException("Fluid list size, capacity list size, and access list size must match 'size'");
        }
        if (capacities.stream().anyMatch(cap -> cap <= 0)) {
            System.err.println("MillenniumFluidStorage constructed with non-positive capacities: " + capacities);
        }
        if (tankAccess.stream().anyMatch(access -> access < 0 || access > 3)) {
            System.err.println("MillenniumFluidStorage constructed with invalid tank access levels: " + tankAccess);
        }


        this.size = size;
        this.fluids = new ArrayList<>(fluids);
        this.capacities = new ArrayList<>(capacities);
        this.tankAccess = new ArrayList<>(tankAccess); // Copy access list
    }

    // Constructor for a single tank with a given capacity
    public MillenniumGasStorage(int capacity)
    {
        this(1, capacity);
    }

    // Constructor for multiple tanks with the same capacity
    public MillenniumGasStorage(int size, int capacity){
        if (size <= 0 || capacity <= 0) {
            throw new IllegalArgumentException("Size and capacity must be positive");
        }
        this.size = size;
        this.fluids = new ArrayList<>(size);
        this.capacities = new ArrayList<>(size);
        this.tankAccess = createDefaultTankAccess(size); // Initialize default access

        for (int i = 0; i < size; i++) {
            this.fluids.add(FluidStack.EMPTY);
            this.capacities.add(capacity);
        }
    }

    // Constructor for multiple tanks with variable capacities
    public MillenniumGasStorage(int size, int... capacityArray){
        if (capacityArray.length != size || size <= 0) {
            throw new IllegalArgumentException("Capacity array length must match 'size' and size must be positive");
        }
        if (IntStream.of(capacityArray).anyMatch(cap -> cap <= 0)) {
            throw new IllegalArgumentException("Capacities must be positive");
        }

        this.size = size;
        this.fluids = new ArrayList<>(size);
        this.capacities = new ArrayList<>(size);
        this.tankAccess = createDefaultTankAccess(size); // Initialize default access

        for (int i = 0; i < size; i++) {
            this.fluids.add(FluidStack.EMPTY);
        }
        for (int cap : capacityArray){
            this.capacities.add(cap);
        }
    }

    // --- Callback Setter ---

    public MillenniumGasStorage setCallback(@Nullable Consumer<CapabilityType> callback) {
        this.onContentsChangedCallback = callback;
        return this;
    }

    // --- Configuration ---

    public MillenniumGasStorage setCapacity(int tank, int capacity)
    {
        if (tank < 0 || tank >= size) throw new IndexOutOfBoundsException("Invalid tank index: " + tank);
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.capacities.set(tank, capacity);
        FluidStack currentFluid = this.fluids.get(tank);
        if (!currentFluid.isEmpty() && currentFluid.getAmount() > capacity) {
            currentFluid.setAmount(capacity);
        }
        return this;
    }

    public MillenniumGasStorage setForInput(int tank){
        this.tankAccess.set(tank, 1);
        return this;
    }

    public MillenniumGasStorage setForOutput(int tank){
        this.tankAccess.set(tank, 2);
        return this;
    }

    public boolean isForInput(int tank){
        return (tankAccess.get(tank) & 1) == 1;
    }

    public boolean isForOutput(int tank){
        return (tankAccess.get(tank) & 2) == 2;
    }

    public MillenniumGasStorage setValidator(@Nullable Predicate<FluidStack> validator)
    {
        this.validator = validator != null ? validator : f -> true;
        return this;
    }

    // --- Getters ---

    @NotNull
    public List<FluidStack> getFluids()
    {
        List<FluidStack> fluidsCopy = new ArrayList<>(size);
        for (FluidStack fluid : this.fluids) {
            fluidsCopy.add(fluid.copy());
        }
        return fluidsCopy;
    }

    public int getFluidAmount(int tank)
    {
        if (tank < 0 || tank >= size) return 0;
        return getFluidRefInTank(tank).getAmount();
    }

    // --- NBT Serialization ---

    public CompoundTag serializeNBT(){
        return writeToNBT(new CompoundTag());
    }

    public void deserializeNBT(CompoundTag nbt){
        this.fluids = new ArrayList<>();
        this.capacities = new ArrayList<>();
        this.tankAccess = new ArrayList<>(); // Initialize access list
        this.size = 0;

        if (nbt.contains("size", Tag.TAG_INT)) {
            this.size = nbt.getInt("size");
        } else {
            System.err.println("MillenniumFluidStorage NBT missing 'size' tag!");
            return;
        }

        this.fluids = new ArrayList<>(size);
        this.capacities = new ArrayList<>(size);
        this.tankAccess = new ArrayList<>(size); // Initialize access list
        for (int i = 0; i < size; i++) {
            this.fluids.add(FluidStack.EMPTY);
            this.capacities.add(0);
            this.tankAccess.add(3); // Initialize with default access 3
        }


        if (nbt.contains("fluids", Tag.TAG_LIST)) {
            ListTag tag = nbt.getList("fluids", Tag.TAG_COMPOUND);
            for(int i = 0; i < tag.size() && i < size; i++){
                CompoundTag fluidTag = tag.getCompound(i);
                FluidStack fluid = FluidStack.loadFluidStackFromNBT(fluidTag);
                if (fluid != null) {
                    setFluidInTank(i, fluid);
                } else {
                    setFluidInTank(i, FluidStack.EMPTY);
                }
            }
        } else {
            System.err.println("MillenniumFluidStorage NBT missing 'fluids' list tag!");
        }

        // Load capacities
        if (nbt.contains("capacities", Tag.TAG_INT_ARRAY)) {
            int[] loadedCapacities = nbt.getIntArray("capacities");
            for (int i = 0; i < loadedCapacities.length && i < size; i++) {
                if (loadedCapacities[i] > 0) {
                    this.capacities.set(i, loadedCapacities[i]);
                } else {
                    System.err.println("Loaded non-positive capacity for tank " + i + ": " + loadedCapacities[i] + ". Using 0 capacity.");
                    this.capacities.set(i, 0);
                }
            }
        } else {
            System.err.println("MillenniumFluidStorage NBT missing 'capacities' int array tag! Tanks will have 0 capacity.");
        }

        // Load tank access levels
        if (nbt.contains("tank_access", Tag.TAG_INT_ARRAY)) {
            int[] loadedAccess = nbt.getIntArray("tank_access");
            for (int i = 0; i < loadedAccess.length && i < size; i++) {
                // Ensure loaded access is valid (0-3)
                if (loadedAccess[i] >= 0 && loadedAccess[i] <= 3) {
                    this.tankAccess.set(i, loadedAccess[i]);
                } else {
                    System.err.println("Loaded invalid access level for tank " + i + ": " + loadedAccess[i] + ". Using default access 3.");
                    this.tankAccess.set(i, 3); // Use default access if invalid loaded value
                }
            }
        } else {
            System.err.println("MillenniumFluidStorage NBT missing 'tank_access' int array tag! Tanks will have default access 3.");
        }
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        nbt.putInt("size", size);
        ListTag tag = new ListTag();
        for(int i = 0; i < size; i++){
            CompoundTag fluid = new CompoundTag();
            this.fluids.get(i).writeToNBT(fluid);
            tag.add(fluid);
        }
        nbt.put("fluids", tag);

        nbt.putIntArray("capacities", capacities.stream().mapToInt(Integer::intValue).toArray());

        // Save tank access levels
        nbt.putIntArray("tank_access", tankAccess.stream().mapToInt(Integer::intValue).toArray());

        return nbt;
    }

    // --- IFluidHandler Implementation ---

    @Override
    public int getTanks() {
        return size;
    }

    public boolean isEmpty(int tank){
        return fluids.get(tank).isEmpty();
    }

    public int addFluidToTank(int tank, FluidStack resource, FluidAction action){
        if (tank < 0 || tank >= size) return 0;

        FluidStack stack = this.fluids.get(tank);
        int capacity = getTankCapacity(tank);

        if (resource.isEmpty() || capacity <= 0)
        {
            return 0;
        }
        if (!isFluidValid(tank, resource)) {
            return 0;
        }

        int filled = 0;
        int resourceAmount = resource.getAmount();
        int tankAmount = stack.getAmount();

        if (stack.isEmpty()) {
            filled = Math.min(capacity, resourceAmount);
            if (action.execute()) {
                this.fluids.set(tank, new FluidStack(resource.getFluid(), filled));
                onContentsChanged(tank);
            }
        } else {
            filled = Math.min(capacity - tankAmount, resourceAmount);
            if (action.execute() && filled > 0) {
                stack.grow(filled);
                onContentsChanged(tank);
            }
        }
        return filled;
    }


    public FluidStack setFluidInTank(int tank, FluidStack fluidStack){
        if (tank < 0 || tank >= size) return FluidStack.EMPTY;
        this.fluids.set(tank, fluidStack != null && !fluidStack.isEmpty() ? fluidStack.copy() : FluidStack.EMPTY);
        return this.fluids.get(tank);
    }

    public FluidStack setFluidInTank(int tank, Fluid fluid, int amount){
        if (tank < 0 || tank >= size) return FluidStack.EMPTY;
        FluidStack stack = new FluidStack(fluid, amount);
        return setFluidInTank(tank, stack);
    }

    public FluidStack setEmptyInTank(int tank){
        if (tank < 0 || tank >= size) return FluidStack.EMPTY;
        this.fluids.set(tank, FluidStack.EMPTY);
        return FluidStack.EMPTY;
    }

    @NotNull
    @Override
    public FluidStack getFluidInTank(int tank) {
        if (tank < 0 || tank >= size) return FluidStack.EMPTY;
        return this.fluids.get(tank).copy();
    }

    @NotNull
    public FluidStack getFluidRefInTank(int tank) {
        if (tank < 0 || tank >= size) return FluidStack.EMPTY;
        return this.fluids.get(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        if (tank < 0 || tank >= size) return 0;
        return capacities.get(tank);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        if (tank < 0 || tank >= size) return false;
        if (stack.isEmpty()) return false;
        if (validator != null && !validator.test(stack)) {
            return false;
        }
        FluidStack currentFluid = this.fluids.get(tank);
        if (currentFluid.isEmpty()) {
            return true;
        }
        return currentFluid.isFluidEqual(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        if (resource.isEmpty()) return 0;

        int totalFilled = 0;
        FluidStack resourceToFill = resource.copy();

        for (int tank = 0; tank < size; tank++) {
            if (!isForInput(tank)) continue;
            int filledInTank = addFluidToTank(tank, resourceToFill, action); // addFluidToTank handles access check
            totalFilled += filledInTank;

            resourceToFill.shrink(filledInTank);

            if (resourceToFill.isEmpty()) {
                break;
            }
        }
        return totalFilled;
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        if (resource.isEmpty()) return FluidStack.EMPTY;

        int amountToDrain = resource.getAmount();
        int totalDrained = 0;
        FluidStack drainedStack = FluidStack.EMPTY;

        for (int tank = size - 1; tank >= 0; tank--) {
            if (!isForOutput(tank)) continue;

            FluidStack internalTankFluid = this.fluids.get(tank);

            if (!internalTankFluid.isEmpty() && internalTankFluid.isFluidEqual(resource)) {
                int canDrainThisTank = Math.min(amountToDrain - totalDrained, internalTankFluid.getAmount());

                if (canDrainThisTank > 0) {
                    if (drainedStack.isEmpty()) {
                        drainedStack = new FluidStack(internalTankFluid.getFluid(), canDrainThisTank);
                    } else {
                        drainedStack.grow(canDrainThisTank);
                    }

                    totalDrained += canDrainThisTank;

                    if (action.execute()) {
                        internalTankFluid.shrink(canDrainThisTank);
                        if (internalTankFluid.getAmount() <= 0) {
                            setFluidInTank(tank, FluidStack.EMPTY);
                        }
                        onContentsChanged(tank);
                    }

                    if (totalDrained >= amountToDrain) {
                        break;
                    }
                }
            }
        }

        return drainedStack;
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        if (maxDrain <= 0) return FluidStack.EMPTY;

        for (int tank = size - 1; tank >= 0; tank--) {
            if (!isForOutput(tank)) continue;

            FluidStack internalTankFluid = this.fluids.get(tank);

            if (!internalTankFluid.isEmpty()) {
                int canDrainThisTank = Math.min(maxDrain, internalTankFluid.getAmount());

                if (canDrainThisTank > 0) {
                    FluidStack drainedStack = new FluidStack(internalTankFluid.getFluid(), canDrainThisTank);

                    if (action.execute()) {
                        internalTankFluid.shrink(canDrainThisTank);

                        if (internalTankFluid.getAmount() <= 0) {
                            setFluidInTank(tank, FluidStack.EMPTY);
                        }
                        onContentsChanged(tank);
                    }
                    return drainedStack;
                }
            }
        }

        return FluidStack.EMPTY;
    }

    protected void onContentsChanged(int tank)
    {
        if (this.onContentsChangedCallback != null) {
            this.onContentsChangedCallback.accept(CapabilityType.FLUID);
        }
    }

    public boolean hasNoTanks()
    {
        return size <= 0;
    }

    public boolean areAllTanksEmpty() {
        for (int i = 0; i < size; i++) {
            if (!getFluidRefInTank(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    // Custom method to set the entire fluids list (use cautiously)
    public void setFluids(@NotNull List<FluidStack> fluids) {
        if (fluids.size() != this.size) {
            System.err.println("Attempted to set fluids list with incorrect size. Expected " + this.size + ", got " + fluids.size());
            return;
        }
        this.fluids.clear();
        for (FluidStack fluid : fluids) {
            this.fluids.add(fluid != null ? fluid.copy() : FluidStack.EMPTY);
        }

        for (int i = 0; i < size; i++) {
            onContentsChanged(i);
        }
    }
}