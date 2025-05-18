package kivo.millennium.milltek.storage;

import kivo.millennium.milltek.gas.IGasHandler;
import kivo.millennium.milltek.gas.IGasHandler.GasAction;
import kivo.millennium.milltek.gas.Gas;
import kivo.millennium.milltek.gas.GasStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class MillenniumGasStorage implements IGasHandler, IMillenniumStorage<GasStack> {
    protected Predicate<GasStack> validator = g -> true;
    protected int size;
    @NotNull
    protected List<GasStack> gases;
    protected List<Integer> capacities;
    protected List<Integer> tankAccess;
    @Nullable
    private Consumer<Void> onContentsChangedCallback;

    private List<Integer> createDefaultTankAccess(int size) {
        List<Integer> defaultAccess = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            defaultAccess.add(3);
        }
        return defaultAccess;
    }

    // ===================== 仅保留一份实现，避免冗余 =====================
    // 构造方法、判空、容量、访问权限等仅保留一份实现，接口方法命名与IFluidHandler一致
    // 其它辅助方法如areAllTanksEmpty、setGases等仅保留一份，无需重复

    // 构造方法
    public MillenniumGasStorage(int size, @NotNull List<GasStack> gases, @NotNull List<Integer> capacities,
            @NotNull List<Integer> tankAccess) {
        if (gases.size() != size || capacities.size() != size || tankAccess.size() != size) {
            throw new IllegalArgumentException(
                    "Gas list size, capacity list size, and access list size must match 'size'");
        }
        if (capacities.stream().anyMatch(cap -> cap <= 0)) {
            System.err.println("MillenniumGasStorage constructed with non-positive capacities: " + capacities);
        }
        if (tankAccess.stream().anyMatch(access -> access < 0 || access > 3)) {
            System.err.println("MillenniumGasStorage constructed with invalid tank access levels: " + tankAccess);
        }
        this.size = size;
        this.gases = new ArrayList<>(gases);
        this.capacities = new ArrayList<>(capacities);
        this.tankAccess = new ArrayList<>(tankAccess);
    }

    public MillenniumGasStorage(int capacity) {
        this(1, capacity);
    }

    public MillenniumGasStorage(int size, int capacity) {
        if (size <= 0 || capacity <= 0) {
            throw new IllegalArgumentException("Size and capacity must be positive");
        }
        this.size = size;
        this.gases = new ArrayList<>(size);
        this.capacities = new ArrayList<>(size);
        this.tankAccess = createDefaultTankAccess(size);
        for (int i = 0; i < size; i++) {
            this.gases.add(GasStack.EMPTY);
            this.capacities.add(capacity);
        }
    }

    public MillenniumGasStorage(int size, int... capacityArray) {
        if (capacityArray.length != size || size <= 0) {
            throw new IllegalArgumentException("Capacity array length must match 'size' and size must be positive");
        }
        if (IntStream.of(capacityArray).anyMatch(cap -> cap <= 0)) {
            throw new IllegalArgumentException("Capacities must be positive");
        }
        this.size = size;
        this.gases = new ArrayList<>(size);
        this.capacities = new ArrayList<>(size);
        this.tankAccess = createDefaultTankAccess(size);
        for (int i = 0; i < size; i++) {
            this.gases.add(GasStack.EMPTY);
        }
        for (int cap : capacityArray) {
            this.capacities.add(cap);
        }
    }

    public MillenniumGasStorage setCallback(@Nullable Consumer<Void> callback) {
        this.onContentsChangedCallback = callback;
        return this;
    }

    public MillenniumGasStorage setCapacity(int tank, int capacity) {
        if (tank < 0 || tank >= size)
            throw new IndexOutOfBoundsException("Invalid tank index: " + tank);
        if (capacity <= 0)
            throw new IllegalArgumentException("Capacity must be positive");
        this.capacities.set(tank, capacity);
        GasStack currentGas = this.gases.get(tank);
        if (!currentGas.isEmpty() && currentGas.getAmount() > capacity) {
            currentGas.setAmount(capacity);
        }
        return this;
    }

    public MillenniumGasStorage setForInput(int tank) {
        this.tankAccess.set(tank, 1);
        return this;
    }

    public MillenniumGasStorage setForOutput(int tank) {
        this.tankAccess.set(tank, 2);
        return this;
    }

    public boolean isForInput(int tank) {
        return (tankAccess.get(tank) & 1) == 1;
    }

    public boolean isForOutput(int tank) {
        return (tankAccess.get(tank) & 2) == 2;
    }

    public MillenniumGasStorage setValidator(@Nullable Predicate<GasStack> validator) {
        this.validator = validator != null ? validator : g -> true;
        return this;
    }

    @NotNull
    public List<GasStack> getGases() {
        List<GasStack> gasesCopy = new ArrayList<>(size);
        for (GasStack gas : this.gases) {
            gasesCopy.add(gas.copy());
        }
        return gasesCopy;
    }

    public int getGasAmount(int tank) {
        if (tank < 0 || tank >= size)
            return 0;
        return getGasRefInTank(tank).getAmount();
    }

    public CompoundTag serializeNBT() {
        return writeToNBT(new CompoundTag());
    }

    public void deserializeNBT(CompoundTag nbt) {
        this.gases = new ArrayList<>();
        this.capacities = new ArrayList<>();
        this.tankAccess = new ArrayList<>();
        this.size = 0;
        if (nbt.contains("size", Tag.TAG_INT)) {
            this.size = nbt.getInt("size");
        } else {
            System.err.println("MillenniumGasStorage NBT missing 'size' tag!");
            return;
        }
        this.gases = new ArrayList<>(size);
        this.capacities = new ArrayList<>(size);
        this.tankAccess = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.gases.add(GasStack.EMPTY);
            this.capacities.add(0);
            this.tankAccess.add(3);
        }
        if (nbt.contains("gases", Tag.TAG_LIST)) {
            ListTag tag = nbt.getList("gases", Tag.TAG_COMPOUND);
            for (int i = 0; i < tag.size() && i < size; i++) {
                CompoundTag gasTag = tag.getCompound(i);
                GasStack gas = GasStack.readFromNBT(gasTag);
                if (gas != null) {
                    setGasInTank(i, gas);
                } else {
                    setGasInTank(i, GasStack.EMPTY);
                }
            }
        } else {
            System.err.println("MillenniumGasStorage NBT missing 'gases' list tag!");
        }
        if (nbt.contains("capacities", Tag.TAG_INT_ARRAY)) {
            int[] loadedCapacities = nbt.getIntArray("capacities");
            for (int i = 0; i < loadedCapacities.length && i < size; i++) {
                if (loadedCapacities[i] > 0) {
                    this.capacities.set(i, loadedCapacities[i]);
                } else {
                    System.err.println("Loaded non-positive capacity for tank " + i + ": " + loadedCapacities[i]
                            + ". Using 0 capacity.");
                    this.capacities.set(i, 0);
                }
            }
        } else {
            System.err.println(
                    "MillenniumGasStorage NBT missing 'capacities' int array tag! Tanks will have 0 capacity.");
        }
        if (nbt.contains("tank_access", Tag.TAG_INT_ARRAY)) {
            int[] loadedAccess = nbt.getIntArray("tank_access");
            for (int i = 0; i < loadedAccess.length && i < size; i++) {
                if (loadedAccess[i] >= 0 && loadedAccess[i] <= 3) {
                    this.tankAccess.set(i, loadedAccess[i]);
                } else {
                    System.err.println("Loaded invalid access level for tank " + i + ": " + loadedAccess[i]
                            + ". Using default access 3.");
                    this.tankAccess.set(i, 3);
                }
            }
        } else {
            System.err.println(
                    "MillenniumGasStorage NBT missing 'tank_access' int array tag! Tanks will have default access 3.");
        }
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        nbt.putInt("size", size);
        ListTag tag = new ListTag();
        for (int i = 0; i < size; i++) {
            tag.add(this.gases.get(i).writeToNBT());
        }
        nbt.put("gases", tag);
        nbt.putIntArray("capacities", capacities.stream().mapToInt(Integer::intValue).toArray());
        nbt.putIntArray("tank_access", tankAccess.stream().mapToInt(Integer::intValue).toArray());
        return nbt;
    }

    // IGasHandler实现
    @Override
    public int getTanks() {
        return size;
    }

    @Override
    public int fill(GasStack resource, GasAction action) {
        if (resource.isEmpty())
            return 0;
        int totalFilled = 0;
        GasStack resourceToFill = resource.copy();
        for (int tank = 0; tank < size; tank++) {
            if (!isForInput(tank))
                continue;
            int filledInTank = addGasToTank(tank, resourceToFill, action.execute());
            totalFilled += filledInTank;
            resourceToFill.setAmount(resourceToFill.getAmount() - filledInTank);
            if (resourceToFill.isEmpty()) {
                break;
            }
        }
        return totalFilled;
    }

    @Override
    @NotNull
    public GasStack drain(GasStack resource, GasAction action) {
        if (resource.isEmpty())
            return GasStack.EMPTY;
        int amountToDrain = resource.getAmount();
        int totalDrained = 0;
        GasStack drainedStack = GasStack.EMPTY;
        for (int tank = size - 1; tank >= 0; tank--) {
            if (!isForOutput(tank))
                continue;
            GasStack internalTankGas = this.gases.get(tank);
            if (!internalTankGas.isEmpty() && internalTankGas.getGas().equals(resource.getGas())) {
                int canDrainThisTank = Math.min(amountToDrain - totalDrained, internalTankGas.getAmount());
                if (canDrainThisTank > 0) {
                    if (drainedStack.isEmpty()) {
                        drainedStack = new GasStack(internalTankGas.getGas(), canDrainThisTank);
                    } else {
                        drainedStack.setAmount(drainedStack.getAmount() + canDrainThisTank);
                    }
                    totalDrained += canDrainThisTank;
                    if (action.execute()) {
                        internalTankGas.setAmount(internalTankGas.getAmount() - canDrainThisTank);
                        if (internalTankGas.getAmount() <= 0) {
                            setGasInTank(tank, GasStack.EMPTY);
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

    @Override
    @NotNull
    public GasStack drain(int maxDrain, GasAction action) {
        if (maxDrain <= 0)
            return GasStack.EMPTY;
        for (int tank = size - 1; tank >= 0; tank--) {
            if (!isForOutput(tank))
                continue;
            GasStack internalTankGas = this.gases.get(tank);
            if (!internalTankGas.isEmpty()) {
                int canDrainThisTank = Math.min(maxDrain, internalTankGas.getAmount());
                if (canDrainThisTank > 0) {
                    GasStack drainedStack = new GasStack(internalTankGas.getGas(), canDrainThisTank);
                    if (action.execute()) {
                        internalTankGas.setAmount(internalTankGas.getAmount() - canDrainThisTank);
                        if (internalTankGas.getAmount() <= 0) {
                            setGasInTank(tank, GasStack.EMPTY);
                        }
                        onContentsChanged(tank);
                    }
                    return drainedStack;
                }
            }
        }
        return GasStack.EMPTY;
    }

    public boolean isEmpty(int tank) {
        return gases.get(tank).isEmpty();
    }

    public int addGasToTank(int tank, GasStack resource, boolean doFill) {
        if (tank < 0 || tank >= size)
            return 0;
        GasStack stack = this.gases.get(tank);
        int capacity = getTankCapacity(tank);
        if (resource.isEmpty() || capacity <= 0) {
            return 0;
        }
        if (!isGasValid(tank, resource)) {
            return 0;
        }
        int filled = 0;
        int resourceAmount = resource.getAmount();
        int tankAmount = stack.getAmount();
        if (stack.isEmpty()) {
            filled = Math.min(capacity, resourceAmount);
            if (doFill) {
                this.gases.set(tank, new GasStack(resource.getGas(), filled));
                onContentsChanged(tank);
            }
        } else {
            filled = Math.min(capacity - tankAmount, resourceAmount);
            if (doFill && filled > 0) {
                stack.setAmount(stack.getAmount() + filled);
                onContentsChanged(tank);
            }
        }
        return filled;
    }

    public GasStack setGasInTank(int tank, GasStack gasStack) {
        if (tank < 0 || tank >= size)
            return GasStack.EMPTY;
        this.gases.set(tank, gasStack != null && !gasStack.isEmpty() ? gasStack.copy() : GasStack.EMPTY);
        return this.gases.get(tank);
    }

    public GasStack setEmptyInTank(int tank) {
        if (tank < 0 || tank >= size)
            return GasStack.EMPTY;
        this.gases.set(tank, GasStack.EMPTY);
        return GasStack.EMPTY;
    }

    @NotNull
    public GasStack getGasInTank(int tank) {
        if (tank < 0 || tank >= size)
            return GasStack.EMPTY;
        return this.gases.get(tank).copy();
    }

    @NotNull
    public GasStack getGasRefInTank(int tank) {
        if (tank < 0 || tank >= size)
            return GasStack.EMPTY;
        return this.gases.get(tank);
    }

    public int getTankCapacity(int tank) {
        if (tank < 0 || tank >= size)
            return 0;
        return capacities.get(tank);
    }

    public boolean isGasValid(int tank, @NotNull GasStack stack) {
        if (tank < 0 || tank >= size)
            return false;
        if (stack.isEmpty())
            return false;
        if (validator != null && !validator.test(stack)) {
            return false;
        }
        GasStack currentGas = this.gases.get(tank);
        if (currentGas.isEmpty()) {
            return true;
        }
        return currentGas.getGas().equals(stack.getGas());
    }

    protected void onContentsChanged(int tank) {
        if (this.onContentsChangedCallback != null) {
            this.onContentsChangedCallback.accept(null);
        }
    }

    public boolean hasNoTanks() {
        return size <= 0;
    }

    public boolean areAllTanksEmpty() {
        for (int i = 0; i < size; i++) {
            if (!getGasRefInTank(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void setGases(@NotNull List<GasStack> gases) {
        if (gases.size() != this.size) {
            System.err.println(
                    "Attempted to set gases list with incorrect size. Expected " + this.size + ", got " + gases.size());
            return;
        }
        this.gases.clear();
        for (GasStack gas : gases) {
            this.gases.add(gas != null ? gas.copy() : GasStack.EMPTY);
        }
        for (int i = 0; i < size; i++) {
            onContentsChanged(i);
        }
    }
    // ===================== 扩展点 =====================
    // 可扩展：添加批量fill/drain等方法
    // ================================================
}