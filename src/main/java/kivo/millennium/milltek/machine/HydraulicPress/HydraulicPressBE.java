package kivo.millennium.milltek.machine.HydraulicPress;

import kivo.millennium.milltek.block.device.AbstractRecipeMachineBE;
import kivo.millennium.milltek.capability.CapabilityCache;
import kivo.millennium.milltek.init.MillenniumBlockEntities;
import kivo.millennium.milltek.recipe.PressingRecipe;
import kivo.millennium.milltek.recipe.ProxyContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class HydraulicPressBE extends AbstractRecipeMachineBE<PressingRecipe> {
    public static final int SLOT_COUNT = 4;
    public static int INPUT1_SLOT = 1;
    public static int INPUT2_SLOT = 2;
    public static int OUTPUT_SLOT = 3;

    public HydraulicPressBE(BlockPos pPos, BlockState pBlockState) {
        super(MillenniumBlockEntities.HYDRAULIC_PRESS_BE.get(),PressingRecipe.Type.INSTANCE, pPos, pBlockState, new CapabilityCache.Builder()
                .withEnergy(100000, 2000)
                .withItems(4));
    }

    @Override
    protected ProxyContainer getInputs() {
        return new ProxyContainer()
                .addProxy(getItemHandler(), INPUT1_SLOT)
                .addProxy(getItemHandler(), INPUT2_SLOT);
    }

    @Override
    protected ProxyContainer getOutputs() {
        return new ProxyContainer()
                .addProxy(getItemHandler(), OUTPUT_SLOT);
    }

    @Override
    protected boolean isInputValid() {
        return !(getItemHandler().getStackInSlot(INPUT1_SLOT).isEmpty() || getItemHandler().getStackInSlot(INPUT2_SLOT).isEmpty());
    }
}