package kivo.millennium.milltek.machine.ResonanceChamber;

import kivo.millennium.milltek.block.device.AbstractRecipeMachineBE;
import kivo.millennium.milltek.capability.CapabilityCache;
import kivo.millennium.milltek.capability.MillenniumItemStorage;
import kivo.millennium.milltek.init.MillenniumBlockEntities;
import kivo.millennium.milltek.recipe.ProxyContainer;
import kivo.millennium.milltek.recipe.ResonanceRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

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
    protected ProxyContainer getInputs() {
        return new ProxyContainer().addProxy(getItemHandler(), 1);
    }

    @Override
    protected ProxyContainer getOutputs() {
        return new ProxyContainer().addProxy(getItemHandler(), 1);
    }

    @Override
    protected boolean isInputValid() {
        return !getItemHandler().getStackInSlot(INPUT_SLOT).isEmpty();
    }
}