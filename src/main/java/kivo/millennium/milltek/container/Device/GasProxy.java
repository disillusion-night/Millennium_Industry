package kivo.millennium.milltek.container.Device;

import kivo.millennium.milltek.Main;
import kivo.millennium.milltek.capability.CapabilityType;
import kivo.millennium.milltek.gas.GasStack;
import kivo.millennium.milltek.recipe.ISlotProxy;
import kivo.millennium.milltek.recipe.RecipeComponent;
import kivo.millennium.milltek.recipe.component.GasComponent;
import kivo.millennium.milltek.storage.MillenniumGasStorage;

public class GasProxy implements ISlotProxy<GasStack> {
    MillenniumGasStorage gasStorage;
    int index;

    public GasProxy(MillenniumGasStorage gasStorage, int index) {
        this.gasStorage = gasStorage;
        this.index = index;
    }

    @Override
    public boolean isEmpty() {
        return gasStorage.isEmpty(index);
    }

    @Override
    public int getAmount() {
        return gasStorage.getGasAmount(index);
    }

    @Override
    public int getSlotLimit() {
        return gasStorage.getTankCapacity(index);
    }

    @Override
    public void setSlotLimit(int limit) {
        gasStorage.setCapacity(index, limit);
    }

    @Override
    public RecipeComponent<GasStack> convert2RecipeComponent() {
        return new kivo.millennium.milltek.recipe.component.GasComponent(get());
    }

    @Override
    public GasStack get() {
        return gasStorage.getGasRefInTank(index);
    }

    @Override
    public void set(GasStack stack) {
        gasStorage.setGasInTank(index, stack);
    }

    @Override
    public GasStack shrink(int amount) {
        GasStack stack = gasStorage.getGasRefInTank(index);
        stack.setAmount(stack.getAmount() - amount);
        if (stack.getAmount() <= 0)
            gasStorage.setEmptyInTank(index);
        return gasStorage.getGasInTank(index);
    }

    @Override
    public GasStack grow(int amount) {
        GasStack stack = gasStorage.getGasRefInTank(index);
        stack.setAmount(stack.getAmount() + amount);
        return gasStorage.getGasInTank(index);
    }

    @Override
    public boolean hasPlaceFor(RecipeComponent<GasStack> slotProxy) {
        if (!(slotProxy instanceof GasComponent gasComponent))
            return false;
        GasStack gasStack = gasComponent.get();
        if (isEmpty())
            return getSlotLimit() >= gasStack.getAmount();
        if (gasStack.getGas().equals(get().getGas()) && gasStack.getAmount() + getAmount() <= getSlotLimit()) {
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        gasStorage.setEmptyInTank(index);
    }

    @Override
    public CapabilityType getType() {
        return CapabilityType.GAS;
    }

    @Override
    public boolean contains(RecipeComponent<GasStack> gasComponent) {
        if (!(gasComponent instanceof GasComponent gc))
            return false;
        if (isEmpty())
            return false;
        GasStack gasStack = gc.get();
        if (get().getGas().equals(gasStack.getGas()) && gasStack.getAmount() <= getAmount()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(RecipeComponent<GasStack> gasComponent) {
        if (!(gasComponent instanceof GasComponent gc))
            return false;
        GasStack gasStack = gc.get();
        if (contains(gc)) {
            shrink(gc.get().getAmount());
            if (gasStack.getAmount() <= 0)
                gasStorage.setEmptyInTank(index);
            return true;
        }
        return false;
    }

    @Override
    public boolean add(RecipeComponent<GasStack> gasComponent) {
        if (!(gasComponent instanceof GasComponent gc))
            return false;
        GasStack gasStack = gc.get();
        if (isEmpty()) {
            set(gasStack);
            return true;
        }
        if (contains(gc)) {
            grow(gc.get().getAmount());
            return true;
        }
        return false;
    }
}
