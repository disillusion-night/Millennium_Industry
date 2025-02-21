package kivo.millennium.millind.capability;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class MetalTankFluidHandler extends FluidTank {
    public MetalTankFluidHandler() {
        super(12000); // 设置容量为12000mb
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (!resource.isEmpty() && isFluidValid(0, resource)) {
            return super.fill(resource, action);
        }
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return FluidStack.EMPTY;
        return super.drain(resource, action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return super.drain(maxDrain, action);
    }
}