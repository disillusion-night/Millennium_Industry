package kivo.millennium.millind.machine.MeltingFurnace;

import kivo.millennium.client.screen.AbstractDeviceSC;
import kivo.millennium.millind.Main;
import kivo.millennium.millind.container.Device.MeltingFurnaceMenu;
import kivo.millennium.millind.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Vector2i;

import static kivo.millennium.millind.Main.getKey;

@OnlyIn(Dist.CLIENT)
public class MeltingFurnaceScreen extends AbstractDeviceSC<MeltingFurnaceMenu> {
    private static final Vector2i ProgressPos = new Vector2i(78, 38);
    private static final Vector2i fluidSlotPos = new Vector2i(107, 16);
    private static final Vector2i fluidSlotSize = new Vector2i(16,57);
    private static final Vector2i FlamePos = new Vector2i(81, 53);

    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(pGuiGraphics, mouseX, mouseY, partialTick);
        FluidStack fluidStack = getMenu().getFluid();
        if(!fluidStack.isEmpty()) checkfluidTip(pGuiGraphics, mouseX, mouseY);
        if(this.menu.getLit()) RenderUtils.renderFlame(pGuiGraphics, leftPos + FlamePos.x, topPos + FlamePos.y);
    }

    public MeltingFurnaceScreen(MeltingFurnaceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.GUI_TEXTURE = Main.getRL( "textures/gui/container/melting_furnace_gui.png");
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
           RenderUtils.renderfluidTip(pGuiGraphics,font,this.menu.getFluid(),this.menu.getFluidCapacity(), mouseX, mouseY);;
        }
    }

}