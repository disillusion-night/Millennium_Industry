package kivo.millennium.client.screen;


import kivo.millennium.millind.block.generator.GeneratorBE;
import kivo.millennium.millind.container.Device.GeneratorMT;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static kivo.millennium.millind.Main.getRL;

public class GeneratorScreen extends AbstractContainerScreen<GeneratorMT> {

private static final int ENERGY_LEFT = 96;
private static final int ENERGY_WIDTH = 72;
private static final int ENERGY_TOP = 8;
private static final int ENERGY_HEIGHT = 8;

private final ResourceLocation GUI = getRL("textures/gui/container/generator.png");

public GeneratorScreen(GeneratorMT container, Inventory inventory, Component title) {
    super(container, inventory, title);
    this.inventoryLabelY = this.imageHeight - 110;
}

@Override
protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
    graphics.blit(GUI, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
    renderSlot(graphics, partialTicks, mouseX, mouseY);
    int power = menu.getPower();
    int p = (int) ((power / (float) GeneratorBE.CAPACITY) * ENERGY_WIDTH);
    graphics.fillGradient(leftPos + ENERGY_LEFT, topPos + ENERGY_TOP, leftPos + ENERGY_LEFT + p, topPos + ENERGY_TOP + ENERGY_HEIGHT, 0xffff0000, 0xff000000);
    graphics.fill(leftPos + ENERGY_LEFT + p, topPos + ENERGY_TOP, leftPos + ENERGY_LEFT + ENERGY_WIDTH, topPos + ENERGY_TOP + ENERGY_HEIGHT, 0xff330000);
}


protected void renderSlot(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
    graphics.blit(GUI, leftPos, topPos, 0, 0, 16, 16);
}


@Override
public void render(GuiGraphics graphics, int mousex, int mousey, float partialTick) {
    super.render(graphics, mousex, mousey, partialTick);
    // Render tooltip with power if in the energy box
    if (mousex >= leftPos + ENERGY_LEFT && mousex < leftPos + ENERGY_LEFT + ENERGY_WIDTH && mousey >= topPos + ENERGY_TOP && mousey < topPos + ENERGY_TOP + ENERGY_HEIGHT) {
        int power = menu.getPower();
        graphics.renderTooltip(this.font, Component.literal(power + " RF"), mousex, mousey);
    }
}
}