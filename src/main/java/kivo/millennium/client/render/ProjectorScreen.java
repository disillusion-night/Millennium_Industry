package kivo.millennium.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import kivo.millennium.millind.Main;
import kivo.millennium.millind.block.projector.ProjectorMenu;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ProjectorScreen extends AbstractContainerScreen<ProjectorMenu> {
    private static final ResourceLocation TEXTURE = Main.getRL("textures/gui/projector_gui.png");

    public ProjectorScreen(ProjectorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 176; // GUI 纹理宽度
        this.imageHeight = 166; // GUI 纹理高度
    }


    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = leftPos;
        int y = topPos;
        PoseStack pPoseStack = pGuiGraphics.pose();
        //pGuiGraphics.blit(x, y, 0, 0, imageWidth, imageHeight); // 绘制背景纹理

        //  示例： 绘制进度条
        //renderProgressArrow(pPoseStack, x, y);
    }

    protected void renderInfo(GuiGraphics pGuiGraphics){
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        pGuiGraphics.drawCenteredString(this.font, "power:" + 1, leftPos, topPos, 0x00000000);
    }


    private void renderProgressArrow(GuiGraphics pGuiGraphics, int x, int y) {
        if(menu.getProgressScaled() > 0) {
            PoseStack pPoseStack = pGuiGraphics.pose();
            //pGuiGraphics.blit(x + 105, y + 34, 176, 0, menu.getProgressScaled() + 1, 16); // 绘制进度条贴图的一部分
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float delta) {
        renderInfo(pGuiGraphics);
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, mouseX, mouseY, delta);
        renderTooltip(pGuiGraphics, mouseX, mouseY);
    }
}