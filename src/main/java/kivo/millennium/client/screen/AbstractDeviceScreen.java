package kivo.millennium.client.screen;

import kivo.millennium.millind.block.generator.GeneratorBE;
import kivo.millennium.millind.container.AbstractDeviceMT;
import kivo.millennium.millind.util.NumberUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static kivo.millennium.millind.Main.getRL;

public abstract class AbstractDeviceScreen extends AbstractContainerScreen<AbstractDeviceMT> {

    private static final int ENERGY_LEFT = 14;
    private static final int ENERGY_WIDTH = 4;
    private static final int ENERGY_TOP = 18;
    private static final int ENERGY_HEIGHT = 50;

    private final ResourceLocation POWER = getRL("textures/gui/container/power/power_horizontal.png");

    public AbstractDeviceScreen(AbstractDeviceMT container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.inventoryLabelY = this.imageHeight - 110;
    }

    @Override
    protected abstract void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY);

    protected void renderPower(GuiGraphics graphics){
        int power = menu.getPower();
        int maxPower = menu.getMaxPower();
        int p = (int) Math.floor((power / (float) maxPower) * ENERGY_HEIGHT);
        int np = ENERGY_HEIGHT - p;
        graphics.blit(
                POWER,
                leftPos + ENERGY_LEFT,
                topPos + ENERGY_TOP + np,
                0, np,
                ENERGY_WIDTH, p,
                ENERGY_WIDTH, ENERGY_HEIGHT
        );
    }

    protected void checkPowerTip(GuiGraphics pGuiGraphics, int mouseX, int mouseY){
        if (mouseX >= leftPos + ENERGY_LEFT
                && mouseX < leftPos + ENERGY_LEFT + ENERGY_WIDTH
                && mouseY >= topPos + ENERGY_TOP
                && mouseY < topPos + ENERGY_TOP + ENERGY_HEIGHT
        ) {
            renderPowerTip(pGuiGraphics, mouseX, mouseY);;
        }
    }

    protected void renderPowerTip(GuiGraphics pGuiGraphics, int mouseX, int mouseY){
        int power = menu.getPower();
        int maxPower = menu.getMaxPower();
        pGuiGraphics.renderTooltip(this.font, Component.literal(NumberUtils.int2String(power) + "/" + NumberUtils.int2String(maxPower) + " FE"), mouseX, mouseY);
    }

    @Override
    public void render(GuiGraphics graphics, int mousex, int mousey, float partialTick) {
        super.render(graphics, mousex, mousey, partialTick);
        renderPower(graphics);
        checkPowerTip(graphics, mousex, mousey);
    }
}
