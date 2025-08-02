package kivo.millennium.client.render;

import static kivo.millennium.milltek.Main.MODID;
import static kivo.millennium.milltek.Main.getKey;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import kivo.millennium.client.render.blockEntity.HDECBERenderer;
import kivo.millennium.client.render.blockEntity.NetherStarLaserBER;
import kivo.millennium.client.render.entity.BlackHoleRenderer;
import kivo.millennium.client.render.entity.NuclearTargetRenderer;
import kivo.millennium.milltek.Main;
import kivo.millennium.milltek.init.MillenniumBlockEntities;
import kivo.millennium.milltek.init.MillenniumEntities;
import kivo.millennium.milltek.init.MillenniumMenuTypes;
import kivo.millennium.milltek.machine.Crusher.CrusherScreen;
import kivo.millennium.milltek.machine.Crystallizer.CrystallizerScreen;
import kivo.millennium.milltek.machine.Electrolyzer.ElectrolyzerScreen;
import kivo.millennium.milltek.machine.FusionChamber.FusionChamberScreen;
import kivo.millennium.milltek.machine.HydraulicPress.HydraulicPressScreen;
import kivo.millennium.milltek.machine.InductionFurnace.InductionFurnaceScreen;
import kivo.millennium.milltek.machine.MeltingFurnace.MeltingFurnaceScreen;
import kivo.millennium.milltek.machine.ResonanceChamber.ResonanceChamberBER;
import kivo.millennium.milltek.machine.ResonanceChamber.ResonanceChamberScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RendererSetup {

    public static final RenderType BLACK_HOLE_LAYER = RenderType.create("black_hole_layer",
            DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true,
            RenderType.CompositeState.builder()
                    //.setShaderState()
                    .setTextureState(new RenderStateShard.TextureStateShard(Main.getRL("textures/entity/black_hole/beam.png"), false, false))
                    .createCompositeState(true));


    @SubscribeEvent
    public static void onClientEvent(FMLClientSetupEvent event){
        event.enqueueWork(()->{
            EntityRenderers.register(MillenniumEntities.BLACK_HOLE.get(), BlackHoleRenderer::new);
            EntityRenderers.register(MillenniumEntities.NUCLEAR_TARGET.get(), NuclearTargetRenderer::new);
            BlockEntityRenderers.register(MillenniumBlockEntities.HDEC_BE.get(), HDECBERenderer::new);
            BlockEntityRenderers.register(MillenniumBlockEntities.NETHER_STAR_LASER_BE.get(), NetherStarLaserBER::new);
            BlockEntityRenderers.register(MillenniumBlockEntities.RESONANCE_CHAMBER_BE.get(), ResonanceChamberBER::new);
            MenuScreens.register(MillenniumMenuTypes.INDUCTION_FURNACE_MENU.get(), InductionFurnaceScreen::new);
            MenuScreens.register(MillenniumMenuTypes.HYDRAULIC_PRESS_MENU.get(), HydraulicPressScreen::new);
            MenuScreens.register(MillenniumMenuTypes.CRUSHER_MENU.get(), CrusherScreen::new);
            MenuScreens.register(MillenniumMenuTypes.FUSION_FURNACE_MENU.get(), FusionChamberScreen::new);
            MenuScreens.register(MillenniumMenuTypes.CRYSTALLIZER_MENU.get(), CrystallizerScreen::new);
            MenuScreens.register(MillenniumMenuTypes.MELTING_FURNACE_MENU.get(), MeltingFurnaceScreen::new);
            MenuScreens.register(MillenniumMenuTypes.RESONANCE_CHAMBER_MENU.get(), ResonanceChamberScreen::new);
            MenuScreens.register(MillenniumMenuTypes.ELECTOLYZER_MENU.get(), ElectrolyzerScreen::new);
            //IClientItemExtensions.of(MillenniumBlocks.HDEC_BL.get().asItem()).getCustomRenderer();
        });
    }

}
