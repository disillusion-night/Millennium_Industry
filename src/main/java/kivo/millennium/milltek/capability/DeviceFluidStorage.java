package kivo.millennium.milltek.capability;

import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.side.fluid.IFluidStorage;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public class DeviceFluidStorage implements IFluidStorage, INBTSerializable<Tag> {
    protected int fluidAmount;
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    @Override
    public @NotNull FluidStack getFluid() {
        return null;
    }

    @Override
    public void setFluid(FluidStack fluidStack) {

    }

    @Override
    public long getCapacity() {
        return 0;
    }

    @Override
    public boolean isFluidValid(FluidStack fluidStack) {
        return false;
    }

    @Override
    public long fill(int i, FluidStack fluidStack, boolean b, boolean b1) {
        return 0;
    }

    @Override
    public boolean supportsFill(int i) {
        return false;
    }

    @Override
    public @NotNull FluidStack drain(int i, FluidStack fluidStack, boolean b, boolean b1) {
        return null;
    }

    @Override
    public boolean supportsDrain(int i) {
        return false;
    }

    @Override
    public @NotNull Object createSnapshot() {
        return null;
    }

    @Override
    public void restoreFromSnapshot(Object o) {

    }

    @Override
    public Tag serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(Tag nbt) {

    }
}
