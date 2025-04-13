package kivo.millennium.millind.machine.Crystallizer;

import kivo.millennium.millind.block.device.AbstractRecipeMachineBE;
import kivo.millennium.millind.capability.CapabilityCache;
import kivo.millennium.millind.capability.MillenniumFluidStorage;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import kivo.millennium.millind.recipe.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

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
    }

    @Override
    protected NeoContainer getInputs() {
        return new NeoContainer()
                .addStack(getFluidTank().getFluidInTank(0))
                .addStack(getItemHandler().getStackInSlot(INPUT_SLOT));
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
        return !getFluidTank().isEmpty(0);
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

    /*
    @Override
    protected void process(CrystallizingRecipe recipe, int energycost) {
        ItemStack outputStack = getItemHandler().getStackInSlot(OUTPUT_SLOT);
        ItemStack result = recipe.getResultItem(null);
        if (outputStack.isEmpty()) {
            getItemHandler().setStackInSlot(OUTPUT_SLOT, result);
        } else if (outputStack.is(result.getItem())) {
            outputStack.grow(result.getCount());
        }
        recipe.costIngredient(getFluidTank().getFluidInTank(0), getItemHandler().getStackInSlot(INPUT_SLOT));
    }*/

    public MillenniumFluidStorage getFluidTank(){
        return this.cache.getFluidCapability();
    }
}