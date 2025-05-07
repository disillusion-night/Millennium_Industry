package kivo.millennium.milltek.machine.MeltingFurnace;

import kivo.millennium.milltek.block.device.AbstractRecipeMachineBE;
import kivo.millennium.milltek.capability.CapabilityCache;
import kivo.millennium.milltek.capability.CapabilityType;
import kivo.millennium.milltek.capability.MillenniumFluidStorage;
import kivo.millennium.milltek.init.MillenniumBlockEntities;
import kivo.millennium.milltek.recipe.MeltingRecipe;
import kivo.millennium.milltek.recipe.ProxyContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;

public class MeltingFurnaceBE extends AbstractRecipeMachineBE<MeltingRecipe> {
    public static final int SLOT_COUNT = 2;
    public static final int BATTERY_SLOT = 0;
    public static final int OUTPUT_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    private static final int FLUID_CAPACITY = 12000;

    int fluidIntakeCooldown = 0;

    public MeltingFurnaceBE(BlockPos pWorldPosition, BlockState pBlockState) {
        super(MillenniumBlockEntities.MELTING_FURNACE_BE.get(), MeltingRecipe.Type.INSTANCE, pWorldPosition, pBlockState, new CapabilityCache.Builder()
                .withEnergy(100000, 2000)
                .withFluid(1, FLUID_CAPACITY)
                .withItems(2)
                .withProgress()
        );
        this.getFluidTank().setForOutput(0);
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
    protected ProxyContainer getInputs() {
        return new ProxyContainer()
                .addProxy(getItemHandler(), INPUT_SLOT);
    }

    @Override
    protected ProxyContainer getOutputs() {
        return new ProxyContainer()
                .addProxy(getFluidTank(), OUTPUT_SLOT);
    }

    @Override
    protected boolean isInputValid() {
        return! getItemHandler().getStackInSlot(INPUT_SLOT).isEmpty();
    }
}