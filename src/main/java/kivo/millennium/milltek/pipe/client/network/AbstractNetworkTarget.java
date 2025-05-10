package kivo.millennium.milltek.pipe.client.network;

import org.antlr.v4.parse.v3TreeGrammarException;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public abstract class AbstractNetworkTarget {
    public abstract IEnergyStorage getEnergyStorage(Level level);

    public abstract int getMaxInsertEnergy(Level level);

    public abstract int insertEnergy(Level level, int amount);

    public abstract IFluidHandler getFluidStorage(Level level);

    protected abstract int getMaxInsertFluid(Level level);

    public abstract int insertFluid(Level level, FluidStack fluidStack);

    public abstract CompoundTag writeToNBT();

    public abstract void readFromNBT(CompoundTag compoundTag);
}
