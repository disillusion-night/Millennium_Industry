package kivo.millennium.milltek.capability;

import kivo.millennium.milltek.init.MillenniumItems;
import kivo.millennium.milltek.recipe.ISlotProxy;
import kivo.millennium.milltek.storage.MillenniumEnergyStorage;
import kivo.millennium.milltek.storage.MillenniumFluidStorage;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public interface IMillenniumStorage<T> {
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
