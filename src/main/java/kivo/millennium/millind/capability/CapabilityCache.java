package kivo.millennium.millind.capability;

import kivo.millennium.millind.init.MillenniumItems;
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
import org.spongepowered.asm.util.IConsumer;

import java.util.function.Consumer;

import static kivo.millennium.millind.capability.CapabilityType.*;


public class CapabilityCache {
    private final boolean hasFluidCapability;
    private final boolean hasItemCapability;
    private final boolean hasEnergyCapability;
    private final boolean hasProgress;

    private MillenniumFluidStorage fluidCapability;
    private MillenniumItemStorage itemCapability;
    private MillenniumEnergyStorage energyCapability;
    private int progress;

    private LazyOptional<IFluidHandler> fluidHandlerLazy;
    private LazyOptional<IItemHandler> itemHandlerLazy;
    private LazyOptional<IEnergyStorage> energyHandlerLazy;


    private CapabilityCache(Builder builder) {
        this.hasFluidCapability = builder.hasFluidCapability;
        this.hasItemCapability = builder.hasItemCapability;
        this.hasEnergyCapability = builder.hasEnergyCapability;
        this.hasProgress = builder.hasProgress;
        this.fluidCapability = builder.fluidCapability;
        this.fluidHandlerLazy = LazyOptional.of(() -> this.fluidCapability);
        this.itemCapability = builder.itemCapability;
        this.itemHandlerLazy = LazyOptional.of(() -> this.itemCapability);
        this.energyCapability = builder.energyCapability;
        this.energyHandlerLazy = LazyOptional.of(() -> this.energyCapability);
        this.progress = builder.progress;
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


    public int addProgress(int count){
        if (!hasProgress) return -1;
        this.progress += count;
        return this.progress;
    }

    public void setProgress(int progress){
        if (!hasProgress) return;
        this.progress = progress;
    }

    public int getProgress(){
        if (!hasProgress) return -1;
        return progress;
    }

    public void onContentsChanged(CapabilityType capabilityType) {
    }

    public static class Builder {
        private boolean hasFluidCapability = false;
        private boolean hasItemCapability = false;
        private boolean hasEnergyCapability = false;
        private boolean hasProgress = false;

        private MillenniumFluidStorage fluidCapability = null;
        private MillenniumItemStorage itemCapability = null;
        private MillenniumEnergyStorage energyCapability = null;
        private int progress = 0;

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

        public Builder withItems(MillenniumItemStorage storage){
            this.hasItemCapability = true;
            this.itemCapability = storage;
            return this;
        }

        public Builder withEnergy(int capacity, int maxTransfer) {
             this.hasEnergyCapability = true;
             this.energyCapability = new MillenniumEnergyStorage(capacity, maxTransfer);
             return this;
        }

        public Builder withProgress(){
            this.progress = 0;
            this.hasProgress = true;
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
        if(hasProgress){
            tag.putInt(PROGRESS.toString(), progress);
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
        if(tag.contains(PROGRESS.toString())){
           progress = tag.getInt(PROGRESS.toString());
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
        if (hasProgress){
            progress = 0;
        }
    }
}