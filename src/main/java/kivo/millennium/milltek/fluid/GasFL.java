package kivo.millennium.milltek.fluid;

import kivo.millennium.milltek.fluid.fluidType.AbstractOilFluidType;
import kivo.millennium.milltek.init.MillenniumBlocks;
import kivo.millennium.milltek.init.MillenniumFluidTypes;
import kivo.millennium.milltek.init.MillenniumFluids;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class GasFL extends ForgeFlowingFluid {
    public static final Properties PROPERTIES = new Properties(MillenniumFluidTypes.MOLTEN_ALUMINUM_ALLOY_FT, MillenniumFluids.MOLTEN_ALUMINUM_ALLOY, MillenniumFluids.FLOWING_MOLTEN_ALUMINUM_ALLOY)
            .explosionResistance(100f).tickRate(5).block(MillenniumBlocks.MOLTEN_ALUMINUM_ALLOY_BL);

    protected GasFL() {
        super(PROPERTIES);
    }

    public static class Source extends GasFL {
        public int getAmount(FluidState state) {
            return 4;
        }

        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static class Flowing extends GasFL {
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        public boolean isSource(FluidState state) {
            return false;
        }
    }


    public static class FT extends AbstractOilFluidType {
        public FT() {
            super(Properties.create().temperature(20), 0xe9dad7);
        }
    }
}
