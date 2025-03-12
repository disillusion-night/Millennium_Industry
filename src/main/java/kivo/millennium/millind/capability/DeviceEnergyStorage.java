package kivo.millennium.millind.capability;

import net.minecraftforge.energy.EnergyStorage;

public class DeviceEnergyStorage extends EnergyStorage {
    public DeviceEnergyStorage(int capacity) {
        super(capacity);
    }

    public DeviceEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public DeviceEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public DeviceEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    public int costEnergy(int cost){
        int temp = this.energy;
        if(cost > this.energy){
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
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
