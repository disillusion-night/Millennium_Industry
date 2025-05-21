package kivo.millennium.milltek.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;

import kivo.millennium.milltek.Main;
import kivo.millennium.milltek.gas.GasStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Matrix4f;

import static kivo.millennium.milltek.Main.getKey;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class RenderUtils {
    private static final ResourceLocation PROGRESS_TEXTURE = Main.getRL("textures/gui/container/progress_arrow.png");

    private static final ResourceLocation FLAME_TEXTURE = Main.getRL("textures/gui/container/furnace_flame.png");

    private static final ResourceLocation SNOW_TEXTURE = Main.getRL("textures/gui/container/snow.png");

    public static void renderFluid(GuiGraphics guiGraphics, FluidStack fluidStack, int x, int y, int width, int height,
            int blitOffset, int capacity) {
        if (fluidStack.isEmpty())
            return;

        IClientFluidTypeExtensions renderer = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        ResourceLocation textureLocation = renderer.getStillTexture(fluidStack);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                .apply(textureLocation);
        int color = renderer.getTintColor(fluidStack);

        RenderSystem.setShaderTexture(0, TextureAtlas.NOT_ASSIGNED);
        RenderSystem.setShaderColor(((color >> 16) & 0xFF) / 255.0f,
                ((color >> 8) & 0xFF) / 255.0f,
                (color & 0xFF) / 255.0f, 1.0f);

        int fluidHeight = (int) (height * (double) fluidStack.getAmount() / capacity);
        int numFullTiles = fluidHeight / 16;
        int remainingHeight = fluidHeight % 16;

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        // 绘制完整的 16x16 图块
        for (int i = 0; i < numFullTiles; i++) {
            int yOffset = y + height - (i + 1) * 16;
            innerBlit(guiGraphics, sprite.atlasLocation(), x, x + width, yOffset, yOffset + 16, blitOffset, u0, u1, v0,
                    v1);
        }

        // 绘制顶部的剩余部分
        if (remainingHeight > 0) {
            int topY = y + height - numFullTiles * 16 - remainingHeight;
            float vTop = v1;
            float vBottom = v1 + (v0 - v1) * ((float) remainingHeight / 16.0f);

            innerBlit(guiGraphics, sprite.atlasLocation(), x, x + width, topY, topY + remainingHeight, blitOffset, u0,
                    u1, vTop, vBottom);
        }

        // 恢复颜色
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void innerBlit(GuiGraphics guiGraphics, ResourceLocation pAtlasLocation, int pX1, int pX2, int pY1,
            int pY2, int pBlitOffset, float pMinU, float pMaxU, float pMinV, float pMaxV) {
        RenderSystem.setShaderTexture(0, pAtlasLocation);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        com.mojang.blaze3d.vertex.BufferBuilder bufferbuilder = com.mojang.blaze3d.vertex.Tesselator.getInstance()
                .getBuilder();
        bufferbuilder.begin(com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS,
                com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix4f, (float) pX1, (float) pY1, (float) pBlitOffset).uv(pMinU, pMinV).endVertex();
        bufferbuilder.vertex(matrix4f, (float) pX1, (float) pY2, (float) pBlitOffset).uv(pMinU, pMaxV).endVertex();
        bufferbuilder.vertex(matrix4f, (float) pX2, (float) pY2, (float) pBlitOffset).uv(pMaxU, pMaxV).endVertex();
        bufferbuilder.vertex(matrix4f, (float) pX2, (float) pY1, (float) pBlitOffset).uv(pMaxU, pMinV).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    public static void renderProgress(GuiGraphics pGuiGraphics, int x, int y, int progress) {
        int l = (int) ((float) progress / 100 * 24);
        pGuiGraphics.blit(PROGRESS_TEXTURE, x, y, 0, 0, l + 1, 16, 24, 17);
    }

    public static void renderFlame(GuiGraphics pGuiGraphics, int x, int y) {
        pGuiGraphics.blit(FLAME_TEXTURE, x, y, 0, 0, 14, 14, 14, 14);
    }

    public static void renderSnow(GuiGraphics pGuiGraphics, int x, int y) {
        pGuiGraphics.blit(SNOW_TEXTURE, x, y, 0, 0, 14, 14, 14, 14);
    }

    public static void renderfluidTip(GuiGraphics pGuiGraphics, Font font, FluidStack fluidStack, int capability,
            int mouseX, int mouseY) {
        pGuiGraphics.renderTooltip(font, List.of(Component
                .literal(fluidStack.getDisplayName().getString()).getVisualOrderText(),
                Component
                        .literal(NumberUtils.int2String(fluidStack.getAmount()) + "/"
                                + NumberUtils.int2String(capability) + " mB")
                        .withStyle(ChatFormatting.GRAY).getVisualOrderText()),
                mouseX, mouseY);
    }
}
