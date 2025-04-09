package kivo.millennium.millind.machine.HydraulicPress;

import kivo.millennium.millind.block.device.AbstractRecipeMachineBE;
import kivo.millennium.millind.capability.CapabilityCache;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import kivo.millennium.millind.recipe.NeoContainer;
import kivo.millennium.millind.recipe.PressingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
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
    protected NeoContainer getInputs() {
        return new NeoContainer()
                .addStack(getItemHandler().getStackInSlot(INPUT1_SLOT))
                .addStack(getItemHandler().getStackInSlot(INPUT2_SLOT));
    }

    @Override
    protected NeoContainer getOutputs() {
        return new NeoContainer()
                .addStack(getItemHandler().getStackInSlot(OUTPUT_SLOT));
    }

    @Override
    protected void acceptOutputs(NeoContainer container) {
        getItemHandler().setStackInSlot(OUTPUT_SLOT, container.getItem(0));
    }

    @Override
    protected boolean isInputValid() {
        return !(getItemHandler().getStackInSlot(INPUT1_SLOT).isEmpty() || getItemHandler().getStackInSlot(INPUT2_SLOT).isEmpty());
    }

    @Override
    protected boolean canProcess(NeoContainer recipeOutputs) {
        if (recipeOutputs.isEmpty()) {
            return false;
        }

        if (getItemHandler().getStackInSlot(OUTPUT_SLOT).isEmpty()) {
            return true;
        }

        if (!getItemHandler().getStackInSlot(OUTPUT_SLOT).is(recipeOutputs.getItem(0).getItem())) {
            return false;
        }

        return getItemHandler().getStackInSlot(OUTPUT_SLOT).getCount() + recipeOutputs.getAmount(0) <= getItemHandler().getSlotLimit(OUTPUT_SLOT);

    }
}