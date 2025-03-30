package kivo.millennium.millind.fluid;

import kivo.millennium.millind.fluid.fluidType.AbstractMoltenFluidType;
import kivo.millennium.millind.init.MillenniumBlocks;
import kivo.millennium.millind.init.MillenniumFluidTypes;
import kivo.millennium.millind.init.MillenniumFluids;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class RawMoltenAluminumFL extends ForgeFlowingFluid {
    public static final Properties PROPERTIES = new Properties(MillenniumFluidTypes.RAW_MOLTEN_ALUMINUM_FT, MillenniumFluids.RAW_MOLTEN_ALUMINUM, MillenniumFluids.FLOWING_RAW_MOLTEN_ALUMINUM)
            .explosionResistance(100f).tickRate(5).block(MillenniumBlocks.RAW_MOLTEN_ALUMINUM_BL);

    protected RawMoltenAluminumFL() {
        super(PROPERTIES);
    }

    public static class Source extends RawMoltenAluminumFL {
        public int getAmount(FluidState state) {
            return 4;
        }

        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static class Flowing extends RawMoltenAluminumFL {
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
    public static class FT extends AbstractMoltenFluidType {
        public FT() {
            super(FluidType.Properties.create().temperature(2000), 0xe6e0dc);
        }

    }

}
