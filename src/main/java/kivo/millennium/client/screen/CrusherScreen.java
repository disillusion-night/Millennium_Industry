package kivo.millennium.client.screen;

import kivo.millennium.millind.block.device.inductionFurnace.InductionFurnaceBL;
import kivo.millennium.millind.container.Device.AbstractDeviceMenu;
import kivo.millennium.millind.container.Device.CrusherContainer;
import kivo.millennium.millind.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Vector2i;

import static kivo.millennium.millind.Main.getRL;

public class CrusherScreen extends AbstractDeviceSC<CrusherContainer> {
    public CrusherScreen(CrusherContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.GUI_TEXTURE = getRL( "textures/gui/container/crusher_gui.png");
    }

    @Override
    public void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY); // 调用父类方法渲染默认背景

        RenderUtils.renderProgress(pGuiGraphics, 77 + leftPos, 34 + topPos, menu.getProgress());
    }
}