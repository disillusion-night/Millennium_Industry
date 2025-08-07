package kivo.millennium.milltek.pipe.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;

public class PipeEnergyStorage implements IEnergyStorage, INBTSerializable<Tag>, IPipeStorage<PipeEnergyStorage> {
    private int energy;
    private int capacity;
    private int maxReceive;
    private int maxExtract;

    public PipeEnergyStorage(int capacity) {
        this(capacity, capacity, capacity, 0);
    }

    public PipeEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = Math.min(energy, capacity);
    }

    public PipeEnergyStorage(CompoundTag tag) {
        this.capacity = tag.getInt("capacity");
        this.maxReceive = tag.getInt("maxReceive");
        this.maxExtract = tag.getInt("maxExtract");
        this.energy = tag.getInt("energy");
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate) {
            energy += energyReceived;
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate) {
            energy -= energyExtracted;
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return maxReceive > 0;
    }

    public void setEnergy(int energy) {
        this.energy = Math.min(energy, capacity);
    }

    @Override
    public PipeEnergyStorage setCapacity(int capacity) {
        this.capacity = capacity;
        if (energy > capacity) {
            energy = capacity;
        }
        return this;
    }

    public void setMaxReceive(int maxReceive) {
        this.maxReceive = maxReceive;
    }

    public void setMaxExtract(int maxExtract) {
        this.maxExtract = maxExtract;
    }

    @Override
    public Tag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("energy", energy);
        tag.putInt("capacity", capacity);
        tag.putInt("maxReceive", maxReceive);
        tag.putInt("maxExtract", maxExtract);
        return tag;
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        if (nbt instanceof CompoundTag tag) {
            this.energy = tag.getInt("energy");
            this.capacity = tag.getInt("capacity");
            this.maxReceive = tag.getInt("maxReceive");
            this.maxExtract = tag.getInt("maxExtract");
        }
    }

    @Override
    public PipeEnergyStorage merge(PipeEnergyStorage other) {
        this.capacity += other.capacity;
        this.receiveEnergy(other.getEnergyStored(), false);
        other.clear();
        return this;
    }

    @Override
    public void clear() {
        this.energy = 0;
        this.capacity = 0;
        this.maxReceive = 0;
        this.maxExtract = 0;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }
}
