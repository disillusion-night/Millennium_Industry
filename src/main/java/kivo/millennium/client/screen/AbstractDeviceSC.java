package kivo.millennium.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import kivo.millennium.millind.container.Device.AbstractDeviceMenu;
import kivo.millennium.millind.util.NumberUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Vector2i;

import static kivo.millennium.millind.Main.getRL;

public abstract class AbstractDeviceSC<C extends AbstractDeviceMenu> extends AbstractContainerScreen<C> {

    // 默认 GUI 纹理，子类可以覆写
    protected ResourceLocation GUI_TEXTURE;
    protected ResourceLocation BATTERY_TEXTURE;
    protected ResourceLocation POWER_TEXTURE;


    protected ResourceLocation ENERGY_AREA_BASE_TEXTURE;
    protected ResourceLocation BATTERY_OVERLAY_TEXTURE;
    protected ResourceLocation POWER_OVERLAY_TEXTURE;

    protected C menu;

    protected Vector2i EnergyPos;
    protected Vector2i EnergySize;
    protected Vector2i PowerSlotSize;
    protected Vector2i BatteryPos;
    protected Vector2i EnergyAreaSize;
    protected Vector2i EnergyPercentOffset;

    protected AbstractDeviceSC(C pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.menu = pMenu;
        this.imageWidth = getImageSize().x;
        this.imageHeight = getImageSize().y;
        this.BATTERY_TEXTURE = getRL("textures/gui/container/power/battery.png");
        this.POWER_TEXTURE = getRL("textures/gui/container/power/power_slot.png");
        this.ENERGY_AREA_BASE_TEXTURE = getRL("textures/gui/container/energy/energy_area.png");
        this.BATTERY_OVERLAY_TEXTURE = getRL("textures/gui/container/energy/battery_overlay.png");
        this.POWER_OVERLAY_TEXTURE = getRL("textures/gui/container/energy/has_power_supply.png");
        this.BatteryPos = new Vector2i(153, 9);
        this.EnergyPos = getEnergyPos();
        this.PowerSlotSize = getPowerSlotSize();
        this.EnergySize = new Vector2i(44, 15);
        this.EnergyAreaSize = new Vector2i(29, 29);
        this.EnergyPercentOffset = new Vector2i(3, 18);
    }

    protected Vector2i getPowerSlotSize(){
        return new Vector2i(21, 15);
    }

    protected Vector2i getImageSize(){
        return new Vector2i(176, 166);
    }

    protected Vector2i getEnergyPos(){
        return new Vector2i(this.imageWidth - 26, 5);
    }

    public void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;

    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int i = this.leftPos;
        int j = this.topPos;
        pGuiGraphics.blit(this.GUI_TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        //renderEnergyArea(guiGraphics, x, y); // 渲染能量区域，子类可以覆写此方法自定义渲染
    }

    /*
    // 渲染能量区域，子类可以覆写此方法自定义渲染
    protected void renderEnergyArea(GuiGraphics guiGraphics, int x, int y) {
        int energyBarX = x + 8; // 能量条 X 坐标 (相对于 GUI 左上角)
        int energyBarY = y + 17; // 能量条 Y 坐标
        int energyBarWidth = 16; // 能量条宽度
        int energyBarHeight = 52; // 能量条高度

        int storedEnergy = menu.getEnergyStored(); // 获取当前存储能量
        int maxEnergy = menu.getMaxEnergyStored();   // 获取最大能量容量

        if (maxEnergy > 0) { // 只有当最大能量大于 0 时才渲染能量条，避免除以 0 错误
            int energyBarProgress = (int) (((float) storedEnergy / maxEnergy) * energyBarHeight); // 计算能量条填充高度
            guiGraphics.blit(GUI_TEXTURE, energyBarX, energyBarY + energyBarHeight - energyBarProgress, 176, 0, energyBarWidth, energyBarProgress); // 绘制能量条

            // 渲染能量文本 (可选)
            // guiGraphics.drawString(font, Component.literal(storedEnergy + "/" + maxEnergy + " FE"), energyBarX, energyBarY + energyBarHeight + 2, 0xFFFFFF, false); // 白色文本
        }
    }*/


    public void renderScaledText(GuiGraphics guiGraphics, String text, int x, int y, int color, float scale) {
        PoseStack poseStack = guiGraphics.pose();

        // 保存当前的 PoseStack 状态
        poseStack.pushPose();

        // 应用缩放
        poseStack.scale(scale, scale, 1.0F);

        // 调整渲染位置以适应缩放 (可能需要根据具体情况调整)
        float scaledX = x / scale;
        float scaledY = y / scale;

        // 渲染文字
        guiGraphics.drawString(font, text, (int)scaledX, (int)scaledY, color, false); // 或者使用 drawText

        // 恢复 PoseStack 到之前的状态
        poseStack.popPose();
    }

    public static void renderCenteredScaledText(GuiGraphics guiGraphics, Font font, String text, int areaX, int areaY, int areaWidth, int areaHeight, int color) {
        float scale = 0.6F;
        PoseStack poseStack = guiGraphics.pose();

        // 保存当前的 PoseStack 状态
        poseStack.pushPose();

        // 应用缩放
        poseStack.scale(scale, scale, 1.0F);

        // 计算缩放后的文本宽度和高度
        int scaledTextWidth = (int) (font.width(text) * scale);
        int scaledTextHeight = (int) (font.lineHeight * scale);

        // 计算居中的 x 和 y 坐标 (基于缩放后的尺寸)
        float centerX = (areaX + (areaWidth / 2.0F)) / scale;
        float centerY = (areaY + (areaHeight / 2.0F)) / scale;
        float textX = centerX - (scaledTextWidth / (2.0F * scale)); // 除以 scale 是因为之前已经缩放了
        float textY = centerY - (scaledTextHeight / (2.0F * scale)); // 同理

        // 渲染文字
        guiGraphics.drawString(font, text, (int) textX, (int) textY, color, false);

        // 恢复 PoseStack 到之前的状态
        poseStack.popPose();
    }

    protected void renderPowerSlot(GuiGraphics pGuiGraphics, Vector2i energyPos, Vector2i energySize){
        int power = menu.getPower();
        int maxPower = menu.getMaxPower();
        float percent = (float) Math.floor(power / (float) maxPower * 100);
        pGuiGraphics.blit(this.POWER_TEXTURE, leftPos+ getEnergyPos().x, topPos + getEnergyPos().y, 0, 0, this.PowerSlotSize.x,this.PowerSlotSize.y, this.PowerSlotSize.x, this.PowerSlotSize.y);

        renderCenteredScaledText(pGuiGraphics, font, (int) percent + "",leftPos + EnergyPos.x + 3, topPos + EnergyPos.y + 4, 15, 8, 0xc6c6c6);
        //graphics.drawString(this.font, (int) percent + "",leftPos + BatteryPos.x + 1, topPos + BatteryPos.y + 2, 0xc6c6c6, false);
    }

    /*
    protected void renderPower(GuiGraphics graphics, Vector2i energyPos, Vector2i energySize){
        int power = menu.getPower();
        int maxPower = menu.getMaxPower();
        float percent = (float) Math.floor(power / (float) maxPower * 100);
        graphics.drawString(this.font, (int) percent + "%",leftPos + energyPos.x + 1, topPos + energyPos.y + 2, 0xc6c6c6, false);
        renderBattery(graphics, percent, BatteryPos);
    }

    protected void renderBattery(GuiGraphics graphics, float percent, Vector2i pos){
        int p = getP(percent);
        graphics.blit(BATTERY_TEXTURE, leftPos + pos.x, topPos + pos.y, 0, 9 * p, 16, 9, 16, 45);
    }*/

    protected void renderEnergyArea(GuiGraphics pGuiGraphics){
        int power = menu.getPower();
        int maxPower = menu.getMaxPower();
        float percent = (float) Math.floor(power / (float) maxPower * 100);

        Vector2i pos = getEnergyAreaPos();

        pGuiGraphics.blit(ENERGY_AREA_BASE_TEXTURE, pos.x, pos.y, 0, 0, EnergyAreaSize.x, EnergyAreaSize.y, EnergyAreaSize.x, EnergyAreaSize.y);

        if (true){
            pGuiGraphics.blit(POWER_OVERLAY_TEXTURE, pos.x, pos.y, 0, 0, EnergyAreaSize.x, EnergyAreaSize.y, EnergyAreaSize.x, EnergyAreaSize.y);
        }
        int p = getP(percent);

        pGuiGraphics.blit(BATTERY_OVERLAY_TEXTURE, pos.x, pos.y, 0, p * EnergyAreaSize.y, EnergyAreaSize.x, EnergyAreaSize.y, EnergyAreaSize.x, 29 * 5);

        pGuiGraphics.drawString(font, (int) Math.floor(percent) + "%",  pos.x + EnergyPercentOffset.x, pos.y + EnergyPercentOffset.y, 0x6dc7dd, false);
    }

    protected Vector2i getEnergyAreaPos(){
        return new Vector2i(this.leftPos - this.EnergyAreaSize.x + 1, this.topPos + this.imageHeight - this.EnergyAreaSize.y);
    }

    protected int getP(float percent){
        if (percent == 0){
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

    protected void checkPowerTip(GuiGraphics pGuiGraphics, int mouseX, int mouseY){
        if (mouseX >= leftPos + EnergyPos.x
                && mouseX < leftPos + EnergyPos.x + EnergySize.x
                && mouseY >= topPos + EnergyPos.y
                && mouseY < topPos + EnergyPos.y + EnergySize.y
        ) {
            renderPowerTip(pGuiGraphics, mouseX, mouseY);;
        }
    }

    protected void renderPowerTip(GuiGraphics pGuiGraphics, int mouseX, int mouseY){
        int power = menu.getPower();
        int maxPower = menu.getMaxPower();
        pGuiGraphics.renderTooltip(this.font, Component.literal(NumberUtils.int2String(power) + "/" + NumberUtils.int2String(maxPower) + " FE"), mouseX, mouseY);
    }

    protected void setInvLabelPos(AbstractDeviceMenu pMenu){
        this.inventoryLabelX = pMenu.getPlayerInvPos().x;
        this.inventoryLabelY = pMenu.getPlayerInvPos().y - 12;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTick) {

        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(pGuiGraphics, mouseX, mouseY); // 渲染 Tooltip
        renderEnergyArea(pGuiGraphics);
        //renderPowerSlot(pGuiGraphics, this.EnergyPos, this.EnergySize);
        //checkPowerTip(pGuiGraphics, mouseX, mouseY);
    }


    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false); // 渲染标题文本
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false); // 渲染玩家物品栏标题文本
    }
}