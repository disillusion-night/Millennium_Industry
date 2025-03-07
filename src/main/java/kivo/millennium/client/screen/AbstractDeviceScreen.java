package kivo.millennium.client.screen;

import kivo.millennium.millind.block.generator.GeneratorBE;
import kivo.millennium.millind.container.AbstractDeviceContainer;
import kivo.millennium.millind.container.GeneratorContainer;
import kivo.millennium.millind.util.NumberUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static kivo.millennium.millind.Main.getRL;

public abstract class AbstractDeviceScreen extends AbstractContainerScreen<AbstractDeviceContainer> {

    private static final int ENERGY_LEFT = 15;
    private static final int ENERGY_WIDTH = 4;
    private static final int ENERGY_TOP = 19;
    private static final int ENERGY_HEIGHT = 50;

    private final ResourceLocation GUI = getRL("textures/gui/container/heat_furnace.png");
    private final ResourceLocation POWER = getRL("textures/gui/container/power/power_horizontal");

    public AbstractDeviceScreen(AbstractDeviceContainer container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.inventoryLabelY = this.imageHeight - 110;
    }

    @Override
    protected abstract void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY);

    protected void renderPower(GuiGraphics graphics){
        int power = menu.getPower();
        int p = (int) ((power / (float) GeneratorBE.CAPACITY) * ENERGY_HEIGHT);
        int np = ENERGY_HEIGHT - p;
        graphics.blit(
                POWER,
                leftPos + ENERGY_LEFT,
                topPos + ENERGY_TOP + np,
                0, 0,
                ENERGY_WIDTH, p,
                ENERGY_WIDTH, p
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
