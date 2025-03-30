package kivo.millennium.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import kivo.millennium.client.render.blockEntity.HDECBERenderer;
import kivo.millennium.client.render.blockEntity.MeltingFurnaceBER;
import kivo.millennium.client.render.blockEntity.NetherStarLaserBERender;
import kivo.millennium.client.render.blockEntity.ResonanceChamberBER;
import kivo.millennium.client.render.entity.BlackHoleRenderer;
import kivo.millennium.client.screen.*;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import kivo.millennium.millind.init.MillenniumEntities;
import kivo.millennium.millind.init.MillenniumMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static kivo.millennium.millind.Main.MODID;
import static kivo.millennium.millind.Main.getRL;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RendererSetup {

    public static final RenderType BLACK_HOLE_LAYER = RenderType.create("black_hole_layer",
            DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true,
            RenderType.CompositeState.builder()
                    //.setShaderState()
                    .setTextureState(new RenderStateShard.TextureStateShard(getRL("textures/entity/black_hole/black_hole.png"), false, false))
                    .createCompositeState(true));


    @SubscribeEvent
    public static void onClientEvent(FMLClientSetupEvent event){
        event.enqueueWork(()->{
            EntityRenderers.register(MillenniumEntities.BLACK_HOLE.get(), BlackHoleRenderer::new);
            BlockEntityRenderers.register(MillenniumBlockEntities.HDEC_BE.get(), HDECBERenderer::new);
            BlockEntityRenderers.register(MillenniumBlockEntities.NETHER_STAR_LASER_BE.get(), NetherStarLaserBERender::new);
            BlockEntityRenderers.register(MillenniumBlockEntities.MELTING_FURNACE_BE.get(), MeltingFurnaceBER::new);
            BlockEntityRenderers.register(MillenniumBlockEntities.RESONANCE_CHAMBER_BE.get(), ResonanceChamberBER::new);
            MenuScreens.register(MillenniumMenuTypes.GENERATOR_MENU.get(), GeneratorScreen::new);
            MenuScreens.register(MillenniumMenuTypes.INDUCTION_FURNACE_MENU.get(), InductionFurnaceScreen::new);
            MenuScreens.register(MillenniumMenuTypes.HYDRAULIC_PRESS_MENU.get(), HydraulicPressScreen::new);
            MenuScreens.register(MillenniumMenuTypes.CRUSHER_CONTAINER.get(), CrusherScreen::new);
            MenuScreens.register(MillenniumMenuTypes.FUSION_FURNACE_MENU.get(), FusionChamberScreen::new);
            MenuScreens.register(MillenniumMenuTypes.MELTING_FURNACE_MENU.get(), MeltingFurnaceScreen::new);
            MenuScreens.register(MillenniumMenuTypes.RESONANCE_CHAMBER_MENU.get(), ResonanceChamberScreen::new);
            //IClientItemExtensions.of(MillenniumBlocks.HDEC_BL.get().asItem()).getCustomRenderer();
        });
    }

}
