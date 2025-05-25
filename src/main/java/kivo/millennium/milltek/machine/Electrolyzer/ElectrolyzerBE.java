package kivo.millennium.milltek.machine.Electrolyzer;

import kivo.millennium.milltek.block.device.AbstractRecipeMachineBE;
import kivo.millennium.milltek.capability.CapabilityCache;
import kivo.millennium.milltek.init.MillenniumBlockEntities;
import kivo.millennium.milltek.recipe.ElectrolyzingRecipe;
import kivo.millennium.milltek.recipe.ProxyContainer;
import kivo.millennium.milltek.storage.MillenniumFluidStorage;
import kivo.millennium.milltek.storage.MillenniumGasStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

public class ElectrolyzerBE extends AbstractRecipeMachineBE<ElectrolyzingRecipe> {
    public static final int SLOT_COUNT = 1;
    public static final int BATTERY_SLOT = 0;
    private static final int FLUID_CAPACITY = 12000;

    public ElectrolyzerBE(BlockPos pos, BlockState state) {
        super(MillenniumBlockEntities.ELECTROLYZER_BE.get(), ElectrolyzingRecipe.Type.INSTANCE, pos, state,
                new CapabilityCache.Builder()
                        .withEnergy(100000, 2000)
                        .withFluid(1, FLUID_CAPACITY)
                        .withGas(2, FLUID_CAPACITY)
                        .withItems(SLOT_COUNT)
                        .withProgress());
        this.getFluidTank().setForInput(0);
        this.getGasTank().setForOutput(0);
        this.getGasTank().setForOutput(1);
        this.getFluidTank().setFluidInTank(0, new FluidStack(Fluids.WATER, 10000));
    }

    @Override
    protected ProxyContainer getInputs() {
        return new ProxyContainer()
                .addProxy(getFluidTank(), 0);
    }

    @Override
    protected ProxyContainer getOutputs() {
        return new ProxyContainer()
                .addProxy(getGasTank(), 0)
                .addProxy(getGasTank(), 1);
    }

    @Override
    protected boolean isInputValid() {
        return !getFluidTank().isEmpty(0);
    }

    public MillenniumFluidStorage getFluidTank() {
        return this.cache.getFluidCapability();
    }

    public MillenniumGasStorage getGasTank() {
        return this.cache.getGasCapability();
    }
}