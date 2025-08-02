package kivo.millennium.milltek.fluid;

import kivo.millennium.milltek.init.MillenniumBlocks;
import kivo.millennium.milltek.init.MillenniumFluidTypes;
import kivo.millennium.milltek.init.MillenniumFluids;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class IcyWaterFluid extends ForgeFlowingFluid {
    public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(MillenniumFluidTypes.ICY_WATER_FLUID_TYPE, MillenniumFluids.ICY_WATER, MillenniumFluids.FLOWING_ICY_WATER)
            .explosionResistance(100f).tickRate(5).block(() -> (LiquidBlock) MillenniumBlocks.ICY_WATER_BL.get());

    protected IcyWaterFluid() {
        super(PROPERTIES);
    }

    public static class Source extends IcyWaterFluid {
        public int getAmount(FluidState state) {
            return 8;
        }

        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static class Flowing extends IcyWaterFluid {
        protected void createFluidStateDefinition(StateDefinition.Builder<net.minecraft.world.level.material.Fluid, FluidState> builder) {
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


}
