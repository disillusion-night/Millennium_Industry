package kivo.millennium.millind.machine.ResonanceChamber;

import kivo.millennium.client.screen.AbstractDeviceSC;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static kivo.millennium.millind.Main.getRL;

@OnlyIn(Dist.CLIENT)
public class ResonanceChamberScreen extends AbstractDeviceSC<ResonanceChamberMenu> {
    public ResonanceChamberScreen(ResonanceChamberMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.GUI_TEXTURE = getRL( "textures/gui/container/resonance_chamber_gui.png");
    }

    @Override
    public void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY); // 调用父类方法渲染默认背景

        //RenderUtils.renderProgress(pGuiGraphics, 77 + leftPos, 34 + topPos, menu.getProgress());
    }
}