package kivo.millennium.millind.machine.MeltingFurnace;

import kivo.millennium.millind.block.device.AbstractRecipeMachineBE;
import kivo.millennium.millind.capability.CapabilityCache;
import kivo.millennium.millind.capability.CapabilityType;
import kivo.millennium.millind.capability.MillenniumFluidStorage;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import kivo.millennium.millind.recipe.ItemProxy;
import kivo.millennium.millind.recipe.MeltingRecipe;
import kivo.millennium.millind.recipe.NeoContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class MeltingFurnaceBE extends AbstractRecipeMachineBE<MeltingRecipe> {
    public static final int SLOT_COUNT = 2;
    public static final int BATTERY_SLOT = 0;
    public static final int OUTPUT_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    private static final int FLUID_CAPACITY = 12000;


    public MeltingFurnaceBE(BlockPos pWorldPosition, BlockState pBlockState) {
        super(MillenniumBlockEntities.MELTING_FURNACE_BE.get(), MeltingRecipe.Type.INSTANCE, pWorldPosition, pBlockState, new CapabilityCache.Builder()
                .withEnergy(100000, 2000)
                .withFluid(1, FLUID_CAPACITY)
                .withItems(2)
                .withProgress()
        );
    }


    @Override
    public void setCapabilityChanged(CapabilityType type) {
        super.setCapabilityChanged(type);
        if(type == CapabilityType.FLUID) level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }

    public MillenniumFluidStorage getFluidTank(){
        return this.cache.getFluidCapability();
    }

    @Override
    protected NeoContainer getInputs() {
        return new NeoContainer()
                .addStack((getItemHandler().getStackInSlot(INPUT_SLOT)));
    }

    @Override
    protected NeoContainer getOutputs() {
        return new NeoContainer()
                .addStack(getFluidTank().getFluidInTank(OUTPUT_SLOT));
    }

    @Override
    protected void acceptOutputs(NeoContainer container) {
        getFluidHandler().setFluidInTank(OUTPUT_SLOT, container.getFluid(0));
    }

    @Override
    protected boolean isInputValid() {
        return! getItemHandler().getStackInSlot(INPUT_SLOT).isEmpty();
    }

    @Override
    protected boolean canProcess(NeoContainer neoContainer) {
        if (neoContainer.isEmpty()) {
            return false;
        }

        if (getItemHandler().getStackInSlot(OUTPUT_SLOT).isEmpty()) {
            return true;
        }

        if (!(getFluidTank().getFluidInTank(OUTPUT_SLOT).getFluid() == neoContainer.getFluid(0).getFluid())) {
            return false;
        }

        return getItemHandler().getStackInSlot(OUTPUT_SLOT).getCount() + neoContainer.getAmount(0) <= getItemHandler().getSlotLimit(OUTPUT_SLOT);

    }
}