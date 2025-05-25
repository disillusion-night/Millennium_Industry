package kivo.millennium.milltek.machine.FusionChamber;

import kivo.millennium.client.screen.AbstractDeviceSC;
import kivo.millennium.milltek.Main;
import kivo.millennium.milltek.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import static kivo.millennium.milltek.Main.getKey;

import org.joml.Vector2i;

@OnlyIn(Dist.CLIENT)
public class FusionChamberScreen extends AbstractDeviceSC<FusionChamberMenu> {
    private static final Vector2i ProgressPos = new Vector2i(80, 36);

    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(pGuiGraphics, mouseX, mouseY, partialTick);
    }

    public FusionChamberScreen(FusionChamberMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 176;
        this.imageHeight = 170;
        this.inventoryLabelY = this.imageHeight - 94;
        this.GUI_TEXTURE = Main.getRL( "textures/gui/container/fusion_chamber_gui.png");
    }

    @Override
    public void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY); // 调用父类方法渲染默认背景

        RenderUtils.renderProgress(pGuiGraphics, ProgressPos.x + leftPos, ProgressPos.y + topPos, menu.getProgress());
    }

}