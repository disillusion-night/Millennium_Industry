package kivo.millennium.client.screen;

import kivo.millennium.millind.container.AbstractDeviceMT;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static kivo.millennium.millind.Main.getRL;

public class InductionFurnaceScreen extends AbstractDeviceScreen{
    private int imgWidth = 176;
    private int imgHeight = 166;
    public InductionFurnaceScreen(AbstractDeviceMT container, Inventory inventory, Component title) {
        super(container, inventory, title);
    }

    private final ResourceLocation GUI = getRL("textures/gui/container/heat_furnace.png");

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(GUI, leftPos, topPos, 0, 0, imgWidth, imgHeight, imgWidth, imgHeight);
        //renderSlot(graphics, partialTicks, mouseX, mouseY);
    }


}
