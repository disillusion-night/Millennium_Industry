package kivo.millennium.millind.machine.HydraulicPress;

import kivo.millennium.client.screen.AbstractDeviceSC;
import kivo.millennium.millind.Main;
import kivo.millennium.millind.container.Device.HydraulicPressMenu;
import kivo.millennium.millind.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static kivo.millennium.millind.Main.getKey;

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