package kivo.millennium.milltek.machine.Crystallizer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import kivo.millennium.milltek.block.device.AbstractRecipeMachineBE;
import kivo.millennium.milltek.capability.CapabilityCache;
import kivo.millennium.milltek.capability.MillenniumFluidStorage;
import kivo.millennium.milltek.init.MillenniumBlockEntities;
import kivo.millennium.milltek.recipe.*;

public class CrystallizerBE extends AbstractRecipeMachineBE<CrystallizingRecipe> {
    public static final int SLOT_COUNT = 3;
    public static final int BATTERY_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;
    private static final int FLUID_CAPACITY = 12000;


    public CrystallizerBE(BlockPos pWorldPosition, BlockState pBlockState) {
        super(MillenniumBlockEntities.CRYSTALLIZER_BE.get(),CrystallizingRecipe.Type.INSTANCE, pWorldPosition, pBlockState, new CapabilityCache.Builder()
                .withEnergy(100000, 2000)
                .withFluid(1, FLUID_CAPACITY)
                .withItems(3)
                .withProgress()
        );
        this.getFluidTank().setForInput(0);
    }

    @Override
    protected ProxyContainer getInputs() {
        return new ProxyContainer()
                .addProxy(getFluidTank(), 0)
                .addProxy(getItemHandler(), INPUT_SLOT);
    }

    @Override
    protected ProxyContainer getOutputs() {
        return new ProxyContainer()
                .addProxy(getItemHandler(), OUTPUT_SLOT);
    }

    @Override
    protected boolean isInputValid() {
        return !getFluidTank().isEmpty(0);
    }

    public MillenniumFluidStorage getFluidTank(){
        return this.cache.getFluidCapability();
    }
}