package kivo.millennium.milltek.machine.FusionChamber;

import kivo.millennium.milltek.block.device.AbstractRecipeMachineBE;
import kivo.millennium.milltek.capability.CapabilityCache;
import kivo.millennium.milltek.init.MillenniumBlockEntities;
import kivo.millennium.milltek.init.MillenniumFluids;
import kivo.millennium.milltek.recipe.*;
import kivo.millennium.milltek.storage.MillenniumFluidStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;


public class FusionChamberBE extends AbstractRecipeMachineBE<FusionRecipe> {
    public static final int SLOT_COUNT = 2;
    public static final int INPUT_SLOT = 1;

    private int fluidIntakeCooldown = 0;
    private static final int FLUID_INTAKE_INTERVAL = 20; // 每隔 20 ticks (1 秒) 尝试吸取流体
    private static final int FLUID_INTAKE_AMOUNT = 100; // 每次尝试吸取的流体数量 (以毫升为单位)

    public FusionChamberBE(BlockPos pWorldPosition, BlockState pBlockState) {
        super(MillenniumBlockEntities.FUSION_CHAMBER_BE.get(), FusionRecipe.Type.INSTANCE, pWorldPosition, pBlockState, new CapabilityCache.Builder()
                .withFluid(2, 12000)
                .withEnergy(200000, 10000)
                .withItems(2)
                .withProgress()
        );
        this.getFluidTank().setForInput(0).setForOutput(1);
    }

    @Override
    protected ProxyContainer getInputs() {
        return new ProxyContainer()
                .addProxy(getFluidTank(), 0)
                .addProxy(getItemHandler(), INPUT_SLOT);
    }

    @Override
    protected ProxyContainer getOutputs() {
        return new ProxyContainer().addProxy(getFluidTank(), 1);
    }

    @Override
    protected boolean isInputValid() {
        return !(getItemHandler().getStackInSlot(INPUT_SLOT).isEmpty() || getFluidTank().isEmpty(0));
    }

    public MillenniumFluidStorage getFluidTank(){
        return this.cache.getFluidCapability();
    }
}