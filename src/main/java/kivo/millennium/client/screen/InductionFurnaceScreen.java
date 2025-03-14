package kivo.millennium.client.screen;

import kivo.millennium.millind.container.Device.AbstractDeviceMenu;
import kivo.millennium.millind.container.Device.InductionFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Vector2i;

import static kivo.millennium.millind.Main.getRL;

public class InductionFurnaceScreen extends AbstractDeviceSC<InductionFurnaceMenu> {
    private static Vector2i ProgressPos = new Vector2i(77, 34);
    private static Vector2i FlamePos = new Vector2i(82, 53);
    public InductionFurnaceScreen(InductionFurnaceMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.GUI_TEXTURE = getRL("textures/gui/container/induction_furnace.png");
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(pGuiGraphics, mouseX, mouseY, partialTick);
        renderProgress(pGuiGraphics);
    }

    public void renderProgress(GuiGraphics pGuiGraphics){
        int i = this.leftPos;
        int j = this.topPos;

        int l = (int)( (float) this.menu.getProgress() / 100 * 24);
        pGuiGraphics.blit(GUI_TEXTURE, i + ProgressPos.x, j + ProgressPos.y, 176, 14, l + 1, 16);
        if(l > 0) pGuiGraphics.blit(GUI_TEXTURE, i + FlamePos.x, j + FlamePos.y, 176, 0, 14, 14);
    }
}
