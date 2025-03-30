package kivo.millennium.millind.block.device.HydraulicPress;

import kivo.millennium.millind.block.device.AbstractMachineBE;
import kivo.millennium.millind.block.device.AbstractRecipeMachineBE;
import kivo.millennium.millind.capability.CapabilityCache;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import kivo.millennium.millind.recipe.CrushingRecipe;
import kivo.millennium.millind.recipe.ExtendedContainer;
import kivo.millennium.millind.recipe.PressingRecipe;
import kivo.millennium.millind.recipe.RecipeComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

import static kivo.millennium.millind.block.device.MillenniumBlockProperty.WORKING;

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
    protected ExtendedContainer getInputs() {
        ExtendedContainer container = new ExtendedContainer(2);
        container.setItem(0, getItemHandler().getStackInSlot(INPUT1_SLOT));
        container.setItem(1, getItemHandler().getStackInSlot(INPUT2_SLOT));

        return container;
    }

    @Override
    protected boolean isInputValid() {
        return !(getItemHandler().getStackInSlot(INPUT1_SLOT).isEmpty() || getItemHandler().getStackInSlot(INPUT2_SLOT).isEmpty());
    }

    @Override
    protected boolean canProcess(List<? extends RecipeComponent> recipeOutputs) {
        if (recipeOutputs.isEmpty()) {
            return false;
        }

        if (getItemHandler().getStackInSlot(OUTPUT_SLOT).isEmpty()) {
            return true;
        }

        if (!getItemHandler().getStackInSlot(OUTPUT_SLOT).is(recipeOutputs.get(0).asItemComponent().getItemStack().getItem())) {
            return false;
        }

        return getItemHandler().getStackInSlot(OUTPUT_SLOT).getCount() + recipeOutputs.get(0).asItemComponent().getItemStack().getCount() <= getItemHandler().getSlotLimit(OUTPUT_SLOT);

    }

    @Override
    protected void processItem(ExtendedContainer container, PressingRecipe recipe, int energycost) {
        ItemStack outputStack = getItemHandler().getStackInSlot(OUTPUT_SLOT);
        ItemStack result = recipe.getResultItem(null);
        if (outputStack.isEmpty()) {
            getItemHandler().setStackInSlot(OUTPUT_SLOT, result);
        } else if (outputStack.is(result.getItem())) {
            outputStack.grow(result.getCount());
        }
        recipe.costItem(getItemHandler().getStackInSlot(1), getItemHandler().getStackInSlot(2));
    }
}