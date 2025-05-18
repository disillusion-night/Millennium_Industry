package kivo.millennium.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static kivo.millennium.milltek.Main.getKey;

import org.joml.Vector2i;

import kivo.millennium.milltek.Main;
import kivo.millennium.milltek.container.Device.AbstractDeviceMenu;
import kivo.millennium.milltek.util.NumberUtils;

public abstract class AbstractDeviceSC<C extends AbstractDeviceMenu> extends AbstractContainerScreen<C> {

    protected Vector2i renderPos;
    // 默认 GUI 纹理，子类可以覆写
    protected ResourceLocation GUI_TEXTURE;

    protected ResourceLocation ENERGY_AREA_BASE_TEXTURE;
    protected ResourceLocation BATTERY_OVERLAY_TEXTURE;
    protected ResourceLocation POWER_OVERLAY_TEXTURE;

    protected C menu;

    protected Vector2i EnergyAreaSize;
    protected Vector2i EnergyPercentOffset;

    protected AbstractDeviceSC(C pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.menu = pMenu;
        this.imageWidth = getImageSize().x;
        this.imageHeight = getImageSize().y;
        this.ENERGY_AREA_BASE_TEXTURE = Main.getRL("textures/gui/container/energy/energy_area.png");
        this.BATTERY_OVERLAY_TEXTURE = Main.getRL("textures/gui/container/energy/battery_overlay.png");
        this.POWER_OVERLAY_TEXTURE = Main.getRL("textures/gui/container/energy/has_power_supply.png");
        this.EnergyAreaSize = new Vector2i(29, 29);
        this.EnergyPercentOffset = new Vector2i(3, 18);
        this.renderPos = new Vector2i(leftPos, topPos);
    }

    protected Vector2i getPowerSlotSize() {
        return new Vector2i(21, 15);
    }

    protected Vector2i getImageSize() {
        return new Vector2i(176, 166);
    }

    public void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;

    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int i = this.leftPos;
        int j = this.topPos;
        pGuiGraphics.blit(this.GUI_TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth,
                this.imageHeight);
    }

    protected void renderEnergyArea(GuiGraphics pGuiGraphics) {
        int power = menu.getPower();
        int maxPower = menu.getMaxPower();
        float percent = (float) Math.floor(power / (float) maxPower * 100);

        Vector2i pos = getEnergyAreaPos();

        pGuiGraphics.blit(ENERGY_AREA_BASE_TEXTURE, pos.x, pos.y, 0, 0, EnergyAreaSize.x, EnergyAreaSize.y,
                EnergyAreaSize.x, EnergyAreaSize.y);

        if (true) {
            pGuiGraphics.blit(POWER_OVERLAY_TEXTURE, pos.x, pos.y, 0, 0, EnergyAreaSize.x, EnergyAreaSize.y,
                    EnergyAreaSize.x, EnergyAreaSize.y);
        }
        int p = getP(percent);

        pGuiGraphics.blit(BATTERY_OVERLAY_TEXTURE, pos.x, pos.y, 0, p * EnergyAreaSize.y, EnergyAreaSize.x,
                EnergyAreaSize.y, EnergyAreaSize.x, 29 * 5);

        pGuiGraphics.drawString(font, (int) Math.floor(percent) + "%", pos.x + EnergyPercentOffset.x,
                pos.y + EnergyPercentOffset.y, 0x6dc7dd, false);
    }

    protected Vector2i getEnergyAreaPos() {
        return new Vector2i(this.leftPos - this.EnergyAreaSize.x + 1,
                this.topPos + this.imageHeight - this.EnergyAreaSize.y);
    }

    protected int getP(float percent) {
        if (percent == 0) {
            return 0;
        } else if (percent < 50) {
            return 1;
        } else if (percent < 75) {
            return 2;
        } else if (percent < 100) {
            return 3;
        } else {
            return 4;
        }
    }

    protected void checkPowerTip(GuiGraphics pGuiGraphics, int mouseX, int mouseY) {
        if (mouseX >= getEnergyAreaPos().x
                && mouseX < getEnergyAreaPos().x + EnergyAreaSize.x
                && mouseY >= getEnergyAreaPos().y
                && mouseY < getEnergyAreaPos().y + EnergyAreaSize.y) {
            renderPowerTip(pGuiGraphics, mouseX, mouseY);
            ;
        }
    }

    protected void renderPowerTip(GuiGraphics pGuiGraphics, int mouseX, int mouseY) {
        int power = menu.getPower();
        int maxPower = menu.getMaxPower();
        pGuiGraphics.renderTooltip(this.font,
                Component.literal(NumberUtils.int2String(power) + "/" + NumberUtils.int2String(maxPower) + " FE"),
                mouseX, mouseY);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTick) {

        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(pGuiGraphics, mouseX, mouseY); // 渲染 Tooltip
        renderEnergyArea(pGuiGraphics);
        checkPowerTip(pGuiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false); // 渲染标题文本
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY,
                0x404040, false); // 渲染玩家物品栏标题文本
    }
}