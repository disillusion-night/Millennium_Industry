package kivo.millennium.millind.fluid.fluidType;

import kivo.millennium.millind.Main;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;

import java.util.function.Consumer;

import static kivo.millennium.millind.Main.getKey;

public abstract class AbstractMoltenFluidType extends FluidType {
    private final int tintColor;
    public AbstractMoltenFluidType(FluidType.Properties properties, int rgb) {
        super(properties);
        this.tintColor = 0xff000000 | rgb;
    }


    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            private static final ResourceLocation STILL_TEXTURE = Main.getRL("block/fluid/lava_still_gray");
            private static final ResourceLocation FLOWING_TEXTURE =  Main.getRL("block/fluid/lava_flow_gray");
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
                return tintColor;
            }
        });
    }
}
