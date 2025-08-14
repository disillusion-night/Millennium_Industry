package kivo.millennium.milltek.storage;

import kivo.millennium.milltek.gas.GasStack;
import kivo.millennium.milltek.gas.IGasHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public class SimpleGasStorage implements IGasHandler, INBTSerializable<CompoundTag> {
    private int capacity;
    private GasStack gasStack;

    public SimpleGasStorage(int capacity) {
        this.capacity = capacity;
        this.gasStack = GasStack.EMPTY;
    }

    public SimpleGasStorage(int capacity, GasStack gasStack) {
        this.capacity = capacity;
        this.gasStack = gasStack;
    }

    public SimpleGasStorage(CompoundTag tag) {
        this.capacity = tag.getInt("capacity");
        if (tag.contains("gas", Tag.TAG_COMPOUND)) {
            this.gasStack = GasStack.readFromNBT(tag.getCompound("gas"));
        } else {
            this.gasStack = GasStack.EMPTY;
        }
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    @NotNull
    public GasStack getGasInTank(int tank) {
        return tank == 0 ? gasStack : GasStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return tank == 0 ? capacity : 0;
    }

    @Override
    public boolean isGasValid(int tank, @NotNull GasStack stack) {
        return tank == 0;
    }

    @Override
    public int fill(GasStack resource, GasAction action) {
        if (resource.isEmpty() || !isGasValid(0, resource)) return 0;
        if (gasStack.isEmpty()) {
            int fillAmount = Math.min(capacity, resource.getAmount());
            if (action.execute()) {
                gasStack = resource.copy();
                gasStack.setAmount(fillAmount);
            }
            return fillAmount;
        } else if (gasStack.isGasEqual(resource)) {
            int fillAmount = Math.min(capacity - gasStack.getAmount(), resource.getAmount());
            if (fillAmount > 0 && action.execute()) {
                gasStack.grow(fillAmount);
            }
            return fillAmount;
        }
        return 0;
    }

    @Override
    @NotNull
    public GasStack drain(int maxDrain, GasAction action) {
        if (gasStack.isEmpty() || maxDrain <= 0) return GasStack.EMPTY;
        int drainAmount = Math.min(gasStack.getAmount(), maxDrain);
        GasStack drained = gasStack.copy();
        drained.setAmount(drainAmount);
        if (action.execute()) {
            gasStack.shrink(drainAmount);
            if (gasStack.getAmount() <= 0) gasStack = GasStack.EMPTY;
        }
        return drained;
    }

    @Override
    @NotNull
    public GasStack drain(@NotNull GasStack resource, GasAction action) {
        if (resource.isEmpty() || !gasStack.isGasEqual(resource)) return GasStack.EMPTY;
        return drain(resource.getAmount(), action);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("capacity", capacity);
        if (!gasStack.isEmpty()) {
            tag.put("gas", gasStack.writeToNBT());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.capacity = nbt.getInt("capacity");
        if (nbt.contains("gas", Tag.TAG_COMPOUND)) {
            this.gasStack = GasStack.readFromNBT(nbt.getCompound("gas"));
        } else {
            this.gasStack = GasStack.EMPTY;
        }
    }

    public GasStack getGasStack() {
        return gasStack;
    }

    public int getCapacity() {
        return capacity;
    }
}
