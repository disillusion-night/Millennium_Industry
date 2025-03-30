package kivo.millennium.millind.capability;

public enum CapabilityType{
    ENERGY, FLUID, ITEM, GAS, DATA, CACHE, PROGRESS;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
