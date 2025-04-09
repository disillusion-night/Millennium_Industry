package kivo.millennium.millind.machine.Crusher;

import kivo.millennium.millind.block.device.AbstractRecipeMachineBE;
import kivo.millennium.millind.capability.CapabilityCache;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import kivo.millennium.millind.recipe.CrushingRecipe;
import kivo.millennium.millind.recipe.ItemProxy;
import kivo.millennium.millind.recipe.NeoContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CrusherBE extends AbstractRecipeMachineBE<CrushingRecipe> {
    public static int SLOT_COUNT = 3;
    public static int INPUT_SLOT = 1;
    public static int OUTPUT_SLOT = 2;


    public CrusherBE(BlockPos pPos, BlockState pBlockState) {
        super(MillenniumBlockEntities.Crusher_BE.get(),CrushingRecipe.Type.INSTANCE, pPos, pBlockState, new CapabilityCache.Builder()
                .withEnergy(100000, 2000)
                .withItems(SLOT_COUNT)
        );
    }

    @Override
    protected NeoContainer getInputs() {
        return new NeoContainer(new ItemProxy(getItemHandler().getStackInSlot(INPUT_SLOT)));
    }

    @Override
    protected void acceptOutputs(NeoContainer container) {
        getItemHandler().setStackInSlot(OUTPUT_SLOT, container.getItem(0));
    }

    @Override
    protected NeoContainer getOutputs() {
        return new NeoContainer(new ItemProxy(getItemHandler().getStackInSlot(OUTPUT_SLOT)));
    }

    @Override
    protected boolean isInputValid() {
        return !getItemHandler().getStackInSlot(INPUT_SLOT).isEmpty();
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

        return getItemHandler().getStackInSlot(OUTPUT_SLOT).getCount() + recipeOutputs.getItem(0).getCount() <= getItemHandler().getSlotLimit(OUTPUT_SLOT);
    }

}