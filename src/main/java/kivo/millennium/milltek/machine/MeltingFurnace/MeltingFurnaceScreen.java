package kivo.millennium.milltek.machine.MeltingFurnace;

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
public class MeltingFurnaceScreen extends AbstractDeviceSC<MeltingFurnaceMenu> {
    private static final Vector2i ProgressPos = new Vector2i(78, 38);
    private static final Vector2i FlamePos = new Vector2i(81, 53);

    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(pGuiGraphics, mouseX, mouseY, partialTick);
        if(this.menu.getLit()) RenderUtils.renderFlame(pGuiGraphics, leftPos + FlamePos.x, topPos + FlamePos.y);
    }

    public MeltingFurnaceScreen(MeltingFurnaceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.GUI_TEXTURE = Main.getRL( "textures/gui/container/melting_furnace_gui.png");
    }

    @Override
    public void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY); // 调用父类方法渲染默认背景

        RenderUtils.renderProgress(pGuiGraphics, 76 + leftPos, 36 + topPos, menu.getProgress());
    }
}