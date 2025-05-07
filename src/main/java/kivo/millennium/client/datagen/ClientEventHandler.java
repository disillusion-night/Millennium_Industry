package kivo.millennium.client.datagen;

import kivo.millennium.milltek.init.MillenniumFluids;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onClientEvent(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // fluid
            ItemBlockRenderTypes.setRenderLayer(MillenniumFluids.ICY_WATER.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(MillenniumFluids.FLOWING_ICY_WATER.get(), RenderType.translucent());

        });

    }
}