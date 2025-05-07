package kivo.millennium.milltek.machine.HydraulicPress;

import static kivo.millennium.milltek.Main.getKey;

import kivo.millennium.client.screen.AbstractDeviceSC;
import kivo.millennium.milltek.Main;
import kivo.millennium.milltek.container.Device.HydraulicPressMenu;
import kivo.millennium.milltek.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HydraulicPressScreen extends AbstractDeviceSC<HydraulicPressMenu> {
    int progressX = 77;
    int progressY = 34;
    public HydraulicPressScreen(HydraulicPressMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.GUI_TEXTURE = Main.getRL( "textures/gui/container/hydraulic_press_gui.png");
    }

    @Override
    public void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);

        RenderUtils.renderProgress(pGuiGraphics, 77 + leftPos, 34 + topPos, menu.getProgress());
    }
}