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

public abstract class MoltenSteelFL extends ForgeFlowingFluid {
    public static final Properties PROPERTIES = new Properties(MillenniumFluidTypes.MOLTEN_STEEL_FT, MillenniumFluids.MOLTEN_STEEL, MillenniumFluids.FLOWING_MOLTEN_STEEL)
            .explosionResistance(100f).tickRate(5).block(MillenniumBlocks.MOLTEN_STEEL_BL);

    protected MoltenSteelFL() {
        super(PROPERTIES);
    }

    public static class Source extends MoltenSteelFL {
        public int getAmount(FluidState state) {
            return 4;
        }

        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static class Flowing extends MoltenSteelFL {
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
            super(FluidType.Properties.create().temperature(2000), 0xb4cae1);
        }

    }
}
