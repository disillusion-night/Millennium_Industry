package kivo.millennium.millind.block.device.ResonanceChamber;

import kivo.millennium.millind.block.device.AbstractMachineBE;
import kivo.millennium.millind.block.device.AbstractRecipeMachineBE;
import kivo.millennium.millind.block.device.MeltingFurnace.MeltingFurnaceBL;
import kivo.millennium.millind.capability.CapabilityCache;
import kivo.millennium.millind.capability.CapabilityType;
import kivo.millennium.millind.capability.MillenniumFluidStorage;
import kivo.millennium.millind.capability.MillenniumItemStorage;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import kivo.millennium.millind.recipe.ExtendedContainer;
import kivo.millennium.millind.recipe.MeltingRecipe;
import kivo.millennium.millind.recipe.RecipeComponent;
import kivo.millennium.millind.recipe.ResonanceRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

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
    protected ExtendedContainer getInputs() {
        return new ExtendedContainer(getItemHandler().getStackInSlot(1));
    }

    @Override
    protected boolean isInputValid() {
        return !getItemHandler().getStackInSlot(INPUT_SLOT).isEmpty();
    }

    @Override
    protected boolean canProcess(List<? extends RecipeComponent> recipeOutputs) {
        return true;
    }

    @Override
    protected void processItem(ExtendedContainer container, ResonanceRecipe recipe, int energycost) {
        getItemHandler().setStackInSlot(INPUT_SLOT, recipe.getResultItem(null));
    }
}