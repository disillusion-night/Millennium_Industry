package kivo.millennium.millind.block.device.crusher;

import kivo.millennium.millind.block.device.AbstractMachineBE;
import kivo.millennium.millind.block.device.AbstractRecipeMachineBE;
import kivo.millennium.millind.capability.CapabilityCache;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import kivo.millennium.millind.recipe.CrushingRecipe;
import kivo.millennium.millind.recipe.ExtendedContainer;
import kivo.millennium.millind.recipe.RecipeComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.openjdk.nashorn.api.tree.BreakTree;

import java.util.List;
import java.util.Optional;

import static kivo.millennium.millind.block.device.MillenniumBlockProperty.WORKING;

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
    protected ExtendedContainer getInputs() {
        return new ExtendedContainer(getItemHandler().getStackInSlot(INPUT_SLOT));
    }


    @Override
    protected boolean isInputValid() {
        return !getItemHandler().getStackInSlot(INPUT_SLOT).isEmpty();
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
    protected void processItem(ExtendedContainer container, CrushingRecipe recipe, int energycost) {
        ItemStack outputStack = getItemHandler().getStackInSlot(OUTPUT_SLOT);
        ItemStack result = recipe.getResultItem(null);
        if (outputStack.isEmpty()) {
            getItemHandler().setStackInSlot(OUTPUT_SLOT, result);
        } else if (outputStack.is(result.getItem())) {
            outputStack.grow(result.getCount());
        }

        getItemHandler().extractItem(INPUT_SLOT, 1, false);
    }
}