package kivo.millennium.client.render.PostRender;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import kivo.millennium.millind.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

import static kivo.millennium.millind.Main.getKey;

public class BlackHolePostRender {

    private final Minecraft minecraft;
    private final TextureManager textureManager;
    private final ResourceLocation shaderLocation = Main.getRL("shaders/post/black_hole_shader.json"); // 替换为您的着色器资源位置

    public BlackHolePostRender(Minecraft minecraft, TextureManager textureManager) {
        this.minecraft = minecraft;
        this.textureManager = textureManager;
    }

    public void render(GuiGraphics pGuiGraphics, float partialTick, Vec2[] points) {
        PoseStack poseStack = pGuiGraphics.pose();

        // ...
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        RenderSystem.setShader(GameRenderer::getPositionTexShader); // 使用默认着色器或您的自定义着色器
        RenderSystem.setShaderTexture(0, minecraft.getMainRenderTarget().getColorTextureId());

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(poseStack.last().pose(), -1.0F, -1.0F, 0.0F).uv(0.0F, 1.0F).endVertex();
        bufferbuilder.vertex(poseStack.last().pose(), 1.0F, -1.0F, 0.0F).uv(1.0F, 1.0F).endVertex();
        bufferbuilder.vertex(poseStack.last().pose(), 1.0F, 1.0F, 0.0F).uv(1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(poseStack.last().pose(), -1.0F, 1.0F, 0.0F).uv(0.0F, 0.0F).endVertex();
        tesselator.end();

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
}