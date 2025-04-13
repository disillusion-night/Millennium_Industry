package kivo.millennium.millind.capability;

import kivo.millennium.millind.init.MillenniumItems;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public interface IMillenniumStorage {
    default Capability getCapability(){
        if(this instanceof MillenniumFluidStorage){
            return ForgeCapabilities.FLUID_HANDLER;
        }
        if(this instanceof MillenniumEnergyStorage){
            return ForgeCapabilities.ENERGY;
        }
        if(this instanceof MillenniumItems){
            return ForgeCapabilities.ITEM_HANDLER;
        }
        return null;
    }
}
