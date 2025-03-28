package kivo.millennium.millind.fluid;

import kivo.millennium.millind.init.MillenniumBlocks;
import kivo.millennium.millind.init.MillenniumFluidTypes;
import kivo.millennium.millind.init.MillenniumFluids;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.function.Consumer;

import static kivo.millennium.millind.Main.getRL;

public abstract class MoltenAluminumAlloyFL extends ForgeFlowingFluid {
    public static final Properties PROPERTIES = new Properties(MillenniumFluidTypes.MOLTEN_ALUMINUM_ALLOY_FT, MillenniumFluids.MOLTEN_ALUMINUM_ALLOY, MillenniumFluids.FLOWING_MOLTEN_ALUMINUM_ALLOY)
            .explosionResistance(100f).tickRate(5).block(MillenniumBlocks.MOLTEN_ALUMINUM_ALLOY_BL);

    protected MoltenAluminumAlloyFL() {
        super(PROPERTIES);
    }

    public static class Source extends MoltenAluminumAlloyFL {
        public int getAmount(FluidState state) {
            return 4;
        }

        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static class Flowing extends MoltenAluminumAlloyFL {
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


    public static class FT extends FluidType {
        public FT(Properties properties) {
            super(properties.temperature(2000));
        }

        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new IClientFluidTypeExtensions() {
                private static final ResourceLocation STILL_TEXTURE = getRL("block/fluid/lava_still_gray");
                private static final ResourceLocation FLOWING_TEXTURE =  getRL("block/fluid/lava_flow_gray");
                private static final ResourceLocation OVERLAY_TEXTURE = ResourceLocation.parse("minecraft:block/lava_overlay");

                @Override
                public ResourceLocation getStillTexture() {
                    return STILL_TEXTURE;
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return FLOWING_TEXTURE;
                }

                @Override
                public ResourceLocation getOverlayTexture() {
                    return OVERLAY_TEXTURE;
                }

                @Override
                public int getTintColor() {
                    return 0xA190c2ff;
                }

            });
        }
    }

}
