package kivo.millennium.milltek.fluid;

import kivo.millennium.milltek.fluid.fluidType.AbstractMoltenFluidType;
import kivo.millennium.milltek.init.MillenniumBlocks;
import kivo.millennium.milltek.init.MillenniumFluidTypes;
import kivo.millennium.milltek.init.MillenniumFluids;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class MoltenIronFL extends ForgeFlowingFluid {
    public static final Properties PROPERTIES = new Properties(MillenniumFluidTypes.MOLTEN_IRON_FT, MillenniumFluids.MOLTEN_IRON, MillenniumFluids.FLOWING_MOLTEN_IRON)
            .explosionResistance(100f).tickRate(5).block(MillenniumBlocks.MOLTEN_IRON_BL);

    protected MoltenIronFL() {
        super(PROPERTIES);
    }

    public static class Source extends MoltenIronFL {
        public int getAmount(FluidState state) {
            return 4;
        }

        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static class Flowing extends MoltenIronFL {
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
            super(FluidType.Properties.create().temperature(2000), 0xa8a8a8);
        }

    }
}
