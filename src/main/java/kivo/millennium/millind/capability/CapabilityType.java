package kivo.millennium.millind.capability;

public enum CapabilityType{
    ENERGY, FLUID, ITEM, GAS, DATA, CACHE;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
