package kivo.millennium.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import kivo.millennium.millind.entity.special.BlackHole;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import kivo.millennium.millind.init.MillenniumBlocks;
import kivo.millennium.millind.init.MillenniumEntities;
import kivo.millennium.millind.init.MillenniumMenuTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
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
    public static void rendererRegister(EntityRenderersEvent.RegisterRenderers event) {
        //event.registerBlockEntityRenderer(MillenniumBlockEntities.METAL_FLUID_TANK_ENTITY.get(), MetalFluidTankEntityRenderer::new);
        event.registerEntityRenderer(MillenniumEntities.BLACK_HOLE.get(), BlackHoleRenderer::new);
        //entity


        MenuScreens.register(MillenniumMenuTypes.PROJECTOR_MENU.get(), ProjectorScreen::new);
    }


}
