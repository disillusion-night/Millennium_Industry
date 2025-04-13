package kivo.millennium.millind.machine.ResonanceChamber;

import kivo.millennium.millind.block.device.AbstractRecipeMachineBE;
import kivo.millennium.millind.capability.CapabilityCache;
import kivo.millennium.millind.capability.MillenniumItemStorage;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import kivo.millennium.millind.recipe.ItemProxy;
import kivo.millennium.millind.recipe.NeoContainer;
import kivo.millennium.millind.recipe.ResonanceRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;

public class ResonanceChamberBE extends AbstractRecipeMachineBE<ResonanceRecipe> {
    public static final int SLOT_COUNT = 2;
    public static final int INPUT_SLOT = 1;


    public ResonanceChamberBE(BlockPos pWorldPosition, BlockState pBlockState) {
        super(MillenniumBlockEntities.RESONANCE_CHAMBER_BE.get(),ResonanceRecipe.Type.INSTANCE, pWorldPosition, pBlockState, new CapabilityCache.Builder()
                .withEnergy(6000000, 10000)
                .withItems(new MillenniumItemStorage(2){
                    @Override
                    public int getSlotLimit(int slot)
                    {
                        return 1;
                    }
                })
                .withProgress()
        );
    }


    @Override
    protected NeoContainer getInputs() {
        return new NeoContainer().addStack(getItemHandler().getStackInSlot(1));
    }

    @Override
    protected NeoContainer getOutputs() {
        return new NeoContainer().addStack(getItemHandler().getStackInSlot(1));
    }

    @Override
    protected void acceptOutputs(NeoContainer container) {
        getItemHandler().setStackInSlot(INPUT_SLOT, container.getItem(0));
    }

    @Override
    protected boolean isInputValid() {
        return !getItemHandler().getStackInSlot(INPUT_SLOT).isEmpty();
    }

    @Override
    protected boolean canProcess(NeoContainer recipeOutputs) {
        return true;
    }
}