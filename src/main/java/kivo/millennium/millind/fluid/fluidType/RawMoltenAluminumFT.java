package kivo.millennium.millind.fluid.fluidType;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;

import java.util.function.Consumer;

import static kivo.millennium.millind.Main.getRL;

public class RawMoltenAluminumFT extends FluidType {
    public RawMoltenAluminumFT(Properties properties) {
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
