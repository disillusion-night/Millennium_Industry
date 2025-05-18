package kivo.millennium.milltek.storage;

import kivo.millennium.milltek.capability.IMillenniumStorage;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.energy.EnergyStorage;

public class MillenniumEnergyStorage extends EnergyStorage implements IMillenniumStorage {
    private boolean canExact;
    private boolean canReceive;

    public MillenniumEnergyStorage(int capacity) {
        super(capacity);
        this.canExact = true;
        this.canReceive = true;
    }

    public MillenniumEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
        this.canExact = true;
        this.canReceive = true;
    }

    public MillenniumEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
        this.canExact = true;
        this.canReceive = true;
    }

    public MillenniumEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
        this.canExact = true;
        this.canReceive = true;
    }

    public int costEnergy(int cost){
        int temp = this.energy;
        if(cost < this.energy){
            this.energy -= cost;
            return cost;
        }else{
            this.energy = 0;
            return temp;
        }
    }

    public void setEnergy(int energy){
        this.energy = energy;
    }


    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        return super.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public boolean canExtract() {
        return canExact && super.canExtract();
    }

    @Override
    public boolean canReceive() {
        return canReceive && super.canReceive();
    }

    public void setCanExact(boolean canExact){
        this.canExact = canExact;
    }

    public void setCanReceive(boolean canReceive){
        this.canReceive = canReceive;
    }

    @Override
    public Tag serializeNBT()
    {
        return IntTag.valueOf(this.getEnergyStored());
    }

    @Override
    public void deserializeNBT(Tag nbt)
    {
        if (!(nbt instanceof IntTag intNbt))
            throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
        this.energy = intNbt.getAsInt();
    }
}
