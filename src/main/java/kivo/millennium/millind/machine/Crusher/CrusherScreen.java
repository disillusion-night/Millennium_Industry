package kivo.millennium.millind.machine.Crusher;

import kivo.millennium.client.screen.AbstractDeviceSC;
import kivo.millennium.millind.Main;
import kivo.millennium.millind.container.Device.CrusherMenu;
import kivo.millennium.millind.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static kivo.millennium.millind.Main.getKey;

@OnlyIn(Dist.CLIENT)
public class CrusherScreen extends AbstractDeviceSC<CrusherMenu> {
    int progressX = 77;
    int progressY = 34;
    public CrusherScreen(CrusherMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.GUI_TEXTURE = Main.getRL( "textures/gui/container/crusher_gui.png");
    }

    @Override
    public void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY); // 调用父类方法渲染默认背景

        RenderUtils.renderProgress(pGuiGraphics, progressX + leftPos, progressY + topPos, menu.getProgress());
    }
}