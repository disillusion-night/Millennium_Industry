package kivo.millennium.client.screen;

import kivo.millennium.millind.block.device.inductionFurnace.InductionFurnaceBL;
import kivo.millennium.millind.container.Device.AbstractDeviceMenu;
import kivo.millennium.millind.container.Device.CrusherContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Vector2i;

import static kivo.millennium.millind.Main.getRL;

public class CrusherScreen extends AbstractDeviceSC<CrusherContainer> {
    private static final Vector2i ProgressPos = new Vector2i(77, 34);
    private static final ResourceLocation PROGRESS_TEXTURE = getRL("textures/gui/container/progress_arrow.png");
    public CrusherScreen(CrusherContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.GUI_TEXTURE = getRL( "textures/gui/container/crusher_gui.png");
    }

    @Override
    public void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY); // 调用父类方法渲染默认背景
        renderProgress(pGuiGraphics);
    }

    public void renderProgress(GuiGraphics pGuiGraphics) {
        int i = this.leftPos;
        int j = this.topPos;

        int l = (int) ((float) this.menu.getProgress() / 100 * 24);
        pGuiGraphics.blit(PROGRESS_TEXTURE, i + ProgressPos.x, j + ProgressPos.y, 0, 0, l + 1, 16, 24, 17);
    }

}