package kivo.millennium.client.screen;

import kivo.millennium.millind.container.Device.AbstractDeviceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import static kivo.millennium.millind.Main.getRL;

public class CrusherScreen extends AbstractDeviceSC<AbstractDeviceMenu> {

    public CrusherScreen(AbstractDeviceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 176; // GUI 宽度 (纹理宽度)
        this.imageHeight = 166; // GUI 高度 (纹理高度)
        this.inventoryLabelX = 8; // 玩家物品栏标题 X 坐标
        this.inventoryLabelY = this.imageHeight - 94; // 玩家物品栏标题 Y 坐标 (根据纹理调整)
        this.titleLabelX = 6; // 标题文本 X 坐标
        this.titleLabelY = 6;  // 标题文本 Y 坐标
        this.GUI_TEXTURE = getRL( "textures/gui/container/crusher.png");
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(guiGraphics, pPartialTick, pMouseX, pMouseY); // 调用父类方法渲染默认背景

        // 可以在这里添加额外的渲染逻辑，例如渲染破碎进度条等
    }

    /*
    @Override
    protected void renderEnergyArea(GuiGraphics guiGraphics, int x, int y) {
        // 调用父类方法渲染默认能量条
        super.renderEnergyArea(guiGraphics, x, y);

        // 可以覆写父类方法，实现自定义的能量条渲染 (例如修改能量条颜色、位置等)
    }*/


    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        // 调用父类方法渲染默认标题和物品栏标题
        super.renderLabels(guiGraphics, pMouseX, pMouseY);

        // 可以覆写父类方法，添加额外的文本渲染逻辑 (例如显示破碎机状态信息等)
    }
}