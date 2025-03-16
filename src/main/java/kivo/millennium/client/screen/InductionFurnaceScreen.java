package kivo.millennium.client.screen;

import kivo.millennium.millind.container.Device.AbstractDeviceMenu;
import kivo.millennium.millind.container.Device.InductionFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Vector2i;

import static kivo.millennium.millind.Main.getRL;

public class InductionFurnaceScreen extends AbstractDeviceSC<InductionFurnaceMenu> {
    private static final Vector2i ProgressPos = new Vector2i(77, 34);
    private static final Vector2i FlamePos = new Vector2i(82, 53);
    private static final ResourceLocation PROGRESS_TEXTURE = getRL("textures/gui/container/progress_arrow.png");
    private static final ResourceLocation STATUS_TEXTURE = getRL("textures/gui/container/furnace_flame.png");
    public InductionFurnaceScreen(InductionFurnaceMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.GUI_TEXTURE = getRL("textures/gui/container/induction_furnace_gui.png");
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
        pGuiGraphics.blit(PROGRESS_TEXTURE, i + ProgressPos.x, j + ProgressPos.y, 0, 0, l + 1, 16, 24, 17);
        if(this.menu.getLit()) pGuiGraphics.blit(STATUS_TEXTURE, i + FlamePos.x, j + FlamePos.y, 0, 0, 14, 14, 14, 14);
    }

}
