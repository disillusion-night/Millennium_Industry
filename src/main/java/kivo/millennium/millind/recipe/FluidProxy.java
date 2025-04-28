package kivo.millennium.millind.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import kivo.millennium.millind.capability.CapabilityType;
import kivo.millennium.millind.capability.MillenniumFluidStorage;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidProxy implements ISlotProxy<FluidStack> {

    MillenniumFluidStorage fluidStorage;
    int index;

    public FluidProxy (MillenniumFluidStorage fluidStorage, int index){
        this.fluidStorage = fluidStorage;
        this.index = index;
    }

    @Override
    public boolean isEmpty() {
        return fluidStorage.isEmpty(index);
    }

    @Override
    public int getAmount() {
        return fluidStorage.getFluidAmount(index);
    }

    @Override
    public void setSlotLimit(int limit) {
        fluidStorage.setCapacity(index, limit);
    }

    @Override
    public int getSlotLimit() {
        return fluidStorage.getTankCapacity(index);
    }

    @Override
    public RecipeComponent convert2RecipeComponent() {
        return new FluidComponent(get());
    }

    @Override
    public FluidStack get() {
        return fluidStorage.getFluidInTank(index);
    }

    @Override
    public void set(FluidStack stack) {
        fluidStorage.setFluidInTank(index, stack);
    }

    @Override
    public FluidStack shrink(int amount) {
        fluidStorage.getFluidInTank(index).shrink(amount);
        return fluidStorage.getFluidInTank(index);
    }

    @Override
    public FluidStack grow(int amount) {
        fluidStorage.getFluidInTank(index).grow(amount);
        return fluidStorage.getFluidInTank(index);
    }

    @Override
    public boolean hasPlaceFor(RecipeComponent<FluidStack> slotProxy) {
        FluidStack fluidStack = slotProxy.get();
        if (isEmpty()) return getSlotLimit() >= fluidStack.getAmount();
        if (fluidStack.getFluid() == get().getFluid() && fluidStack.getAmount() + getAmount() <= getSlotLimit()){
            return true;
        }
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public CapabilityType getType() {
        return CapabilityType.FLUID;
    }

    @Override
    public boolean contains(RecipeComponent<FluidStack> fluidComponent) {
        if (isEmpty()) return false;
        FluidStack fluidStack = fluidComponent.get();
        if (get().getFluid() == fluidStack.getFluid() && fluidStack.getAmount() <= getAmount()){
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(RecipeComponent<FluidStack> fluidComponent) {
        FluidStack fluidStack = fluidComponent.get();
        if (contains(fluidComponent)){
            shrink(fluidComponent.get().getAmount());
            return true;
        }
        return false;
    }

    @Override
    public boolean add(RecipeComponent<FluidStack> fluidComponent) {
        FluidStack fluidStack = fluidComponent.get();
        if (isEmpty()) {
            set(fluidStack);
            return true;
        }
        if (contains(fluidComponent)){
            grow(fluidComponent.get().getAmount());
            return true;
        }
        return false;
    }
}
