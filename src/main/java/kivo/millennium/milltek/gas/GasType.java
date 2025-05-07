package kivo.millennium.milltek.gas;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;

import java.util.function.Consumer;

import kivo.millennium.milltek.Main;

public class GasType {
    private Gas gas;
    private final int tintColor;

    public GasType(Gas gas, int tintColor){
        this.tintColor = tintColor;
    }

    public int getTintColor() {
        return tintColor;
    }


    public int getColor() {
        return tintColor & 0x00ffffff;
    }

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
