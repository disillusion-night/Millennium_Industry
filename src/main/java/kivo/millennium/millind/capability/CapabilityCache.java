package kivo.millennium.millind.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static kivo.millennium.millind.capability.CapabilityType.*;


public class CapabilityCache {
    private boolean hasFluidCapability;
    private final boolean hasItemCapability;
    private final boolean hasEnergyCapability;

    private MillenniumFluidStorage fluidCapability;
    private MillenniumItemStorage itemCapability;
    private MillenniumEnergyStorage energyCapability;

    private LazyOptional<IFluidHandler> fluidHandlerLazy;
    private LazyOptional<IItemHandler> itemHandlerLazy;
    private LazyOptional<IEnergyStorage> energyHandlerLazy;


    private CapabilityCache(Builder builder) {
        this.hasFluidCapability = builder.hasFluidCapability;
        this.hasItemCapability = builder.hasItemCapability;
        this.hasEnergyCapability = builder.hasEnergyCapability;
        this.fluidCapability = builder.fluidCapability;
        this.fluidHandlerLazy = LazyOptional.of(() -> this.fluidCapability);
        this.itemCapability = builder.itemCapability;
        this.itemHandlerLazy = LazyOptional.of(() -> this.itemCapability);
        this.energyCapability = builder.energyCapability;
        this.energyHandlerLazy = LazyOptional.of(() -> this.energyCapability);
    }

    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER && hasFluidCapability) {
            return fluidHandlerLazy.cast();
        }
         if (cap == ForgeCapabilities.ITEM_HANDLER && hasItemCapability) {
             return itemHandlerLazy.cast();
         }
         if (cap == ForgeCapabilities.ENERGY && hasEnergyCapability) {
             return energyHandlerLazy.cast();
         }
        return LazyOptional.empty();
    }

    public CapabilityCache withItems() {
        this.itemCapability = new MillenniumItemStorage(){
            @Override
            protected void onContentsChanged(int slot){
               CapabilityCache.this.onContentsChanged(ITEM);
            }
        };
        return this;
    }

    public void onContentsChanged(CapabilityType capabilityType) {
    }

    public static class Builder {
        private boolean hasFluidCapability = false;
        private boolean hasItemCapability = false;
        private boolean hasEnergyCapability = false;

        private MillenniumFluidStorage fluidCapability = null;
        private MillenniumItemStorage itemCapability = null;
        private MillenniumEnergyStorage energyCapability = null;

        private Consumer<CapabilityType> capabilityTypeConsumer;

        public Builder withFluid(int size, int capacity) {
            this.hasFluidCapability = true;
            this.fluidCapability = new MillenniumFluidStorage(size, capacity){
                @Override
                protected void onContentsChanged(int tank){
                    capabilityTypeConsumer.accept(FLUID);
                }
            };
            return this;
        }

        public Builder withItems(int slot) {
            this.hasItemCapability = true;
            this.itemCapability = new MillenniumItemStorage(slot){
                @Override
                protected void onContentsChanged(int slot){
                    capabilityTypeConsumer.accept(ITEM);
                }
            };
            return this;
        }
        public Builder withItems() {
            this.hasItemCapability = true;
            this.itemCapability = new MillenniumItemStorage(){
                @Override
                protected void onContentsChanged(int slot){
                    capabilityTypeConsumer.accept(ITEM);
                }
            };
            return this;
        }

        public Builder withEnergy(int capacity, int maxTransfer) {
             this.hasEnergyCapability = true;
             this.energyCapability = new MillenniumEnergyStorage(capacity, maxTransfer);
             return this;
        }

        public CapabilityCache build(Consumer<CapabilityType> consumer) {
            this.capabilityTypeConsumer = consumer;
            return new CapabilityCache(this);
        }
    }

    public MillenniumItemStorage getItemCapability(){
        return this.itemCapability;
    }

    public MillenniumEnergyStorage getEnergyCapability(){
        return this.energyCapability;
    }

    public MillenniumFluidStorage getFluidCapability(){
        return this.fluidCapability;
    }

    public void writeToNBT(CompoundTag nbt){
        CompoundTag tag = new CompoundTag();
        if(hasEnergyCapability){
           tag.put(ENERGY.toString(), energyCapability.serializeNBT());
        }
        if(hasFluidCapability){
            tag.put(FLUID.toString(), fluidCapability.serializeNBT());
        }
        if(hasItemCapability){
            tag.put(ITEM.toString(), itemCapability.serializeNBT());
        }
        nbt.put(CACHE.toString(), tag);
    }

    public void readFromNBT(CompoundTag nbt){
        CompoundTag tag = nbt.getCompound(CACHE.toString());
        if(tag.contains(ENERGY.toString())){
            energyCapability.deserializeNBT(tag.get(ENERGY.toString()));
        }
        if(tag.contains(FLUID.toString())){
            fluidCapability.deserializeNBT(tag.getCompound(FLUID.toString()));
        }
        if(tag.contains(ITEM.toString())){
            itemCapability.deserializeNBT(tag.getCompound(ITEM.toString()));
        }
    }

    public void inValidCaps(){
        if(hasEnergyCapability){
            energyHandlerLazy.invalidate();
        }
        if(hasFluidCapability){
            fluidHandlerLazy.invalidate();
        }
        if(hasItemCapability){
            itemHandlerLazy.invalidate();
        }
    }
}