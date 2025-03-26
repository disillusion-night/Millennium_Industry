package kivo.millennium.client.screen;

import kivo.millennium.millind.container.Device.AbstractDeviceMenu;
import kivo.millennium.millind.container.Device.InductionFurnaceMenu;
import kivo.millennium.millind.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Vector2i;

import static kivo.millennium.millind.Main.getRL;

public class InductionFurnaceScreen extends AbstractDeviceSC<InductionFurnaceMenu> {
    private static final Vector2i FlamePos = new Vector2i(82, 53);
    private static final ResourceLocation STATUS_TEXTURE = getRL("textures/gui/container/furnace_flame.png");
    public InductionFurnaceScreen(InductionFurnaceMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.GUI_TEXTURE = getRL("textures/gui/container/induction_furnace_gui.png");
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(pGuiGraphics, mouseX, mouseY, partialTick);
        RenderUtils.renderProgress(pGuiGraphics, 77 + leftPos, 34 + topPos, menu.getProgress());
        renderFlame(pGuiGraphics);
    }

    private void renderFlame(GuiGraphics pGuiGraphics) {
        if(this.menu.getLit()) {
            pGuiGraphics.blit(STATUS_TEXTURE, leftPos + FlamePos.x, topPos + FlamePos.y, 0, 0, 14, 14, 14, 14);
        }
    }
}
