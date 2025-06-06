package kivo.millennium.milltek.machine.Crusher;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import static kivo.millennium.milltek.Main.getKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kivo.millennium.milltek.block.device.AbstractRecipeMachineBE;
import kivo.millennium.milltek.capability.CapabilityCache;
import kivo.millennium.milltek.init.MillenniumBlockEntities;
import kivo.millennium.milltek.recipe.CrushingRecipe;
import kivo.millennium.milltek.recipe.ProxyContainer;

public class CrusherBE extends AbstractRecipeMachineBE<CrushingRecipe> {
    private static final Logger log = LoggerFactory.getLogger(CrusherBE.class);
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
    protected ProxyContainer getInputs() {
        return new ProxyContainer().addProxy(getItemHandler(), INPUT_SLOT);
    }

    @Override
    protected ProxyContainer getOutputs() {
        return new ProxyContainer().addProxy(getItemHandler(), OUTPUT_SLOT);
    }

    @Override
    protected boolean isInputValid() {
        return !getItemHandler().getStackInSlot(INPUT_SLOT).isEmpty();
    }

}