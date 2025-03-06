package kivo.millennium.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import kivo.millennium.client.render.blockEntity.HDECBERenderer;
import kivo.millennium.client.render.blockEntity.NetherStarLaserBERender;
import kivo.millennium.client.screen.GeneratorScreen;
import kivo.millennium.millind.entity.special.BlackHole;
import kivo.millennium.millind.init.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import kivo.millennium.client.render.entity.BlackHoleRenderer;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            MenuScreens.register(MillenniumMenuTypes.GENERATOR_MENU.get(), GeneratorScreen::new);
            //IClientItemExtensions.of(MillenniumBlocks.HDEC_BL.get().asItem()).getCustomRenderer();
        });
    }

}
