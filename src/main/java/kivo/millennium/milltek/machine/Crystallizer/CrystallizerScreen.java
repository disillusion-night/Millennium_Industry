package kivo.millennium.milltek.machine.Crystallizer;

import kivo.millennium.client.screen.AbstractDeviceSC;
import kivo.millennium.milltek.Main;
import kivo.millennium.milltek.container.Device.CrystallizerMenu;
import kivo.millennium.milltek.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import static kivo.millennium.milltek.Main.getKey;

import org.joml.Vector2i;

@OnlyIn(Dist.CLIENT)
public class CrystallizerScreen extends AbstractDeviceSC<CrystallizerMenu> {
    private static final Vector2i ProgressPos = new Vector2i(76, 36);
    private static final Vector2i fluidSlotPos = new Vector2i(44, 17);
    private static final Vector2i fluidSlotSize = new Vector2i(16,57);
    private static final Vector2i SnowPos = new Vector2i(80, 53);

    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(pGuiGraphics, mouseX, mouseY, partialTick);
        FluidStack fluidStack = getMenu().getFluid();
        if(!fluidStack.isEmpty()) checkfluidTip(pGuiGraphics, mouseX, mouseY);
        if(this.menu.getLit()) RenderUtils.renderSnow(pGuiGraphics, leftPos + SnowPos.x, topPos + SnowPos.y);
    }

    public CrystallizerScreen(CrystallizerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.GUI_TEXTURE = Main.getRL( "textures/gui/container/crystallizer_gui.png");
    }

    @Override
    public void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY); // 调用父类方法渲染默认背景

        RenderUtils.renderProgress(pGuiGraphics, 76 + leftPos, 36 + topPos, menu.getProgress());

        FluidStack fluidStack = getMenu().getFluid();

        RenderUtils.renderFluid(pGuiGraphics, fluidStack, fluidSlotPos.x + leftPos, fluidSlotPos.y + topPos, fluidSlotSize.x, fluidSlotSize.y,0, menu.getFluidCapacity());
    }

    protected void checkfluidTip(GuiGraphics pGuiGraphics, int mouseX, int mouseY){
        if (mouseX >= leftPos + fluidSlotPos.x
                && mouseX < leftPos + fluidSlotPos.x + fluidSlotSize.x
                && mouseY >= topPos + fluidSlotPos.y
                && mouseY < topPos + fluidSlotPos.y + fluidSlotSize.y
        ) {
          RenderUtils.renderfluidTip(pGuiGraphics,font, this.menu.getFluid(), this.menu.getFluidCapacity(), mouseX, mouseY);;
        }
    }

}