package kivo.millennium.client.screen;

import kivo.millennium.millind.container.Device.FusionChamberContainer;
import kivo.millennium.millind.util.NumberUtils;
import kivo.millennium.millind.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Vector2i;

import static kivo.millennium.millind.Main.getRL;

public class FusionFurnaceScreen extends AbstractDeviceSC<FusionChamberContainer> {
    private static final Vector2i ProgressPos = new Vector2i(80, 36);
    private static final Vector2i fluidSlot1Pos = new Vector2i(26, 16);
    private static final Vector2i fluidSlot2Pos = new Vector2i(116, 16);
    private static final Vector2i fluidSlotSize = new Vector2i(16,57);

    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(pGuiGraphics, mouseX, mouseY, partialTick);

        FluidStack fluidStackIn = getMenu().getFluidIn();
        if(!fluidStackIn.isEmpty()) checkfluid1Tip(pGuiGraphics,fluidStackIn,getMenu().getFluidCapacityIn(), mouseX, mouseY);
        FluidStack fluidStackOut = getMenu().getFluidOut();
        if(!fluidStackOut.isEmpty()) checkfluid2Tip(pGuiGraphics,fluidStackOut,getMenu().getFluidCapacityOut(), mouseX, mouseY);
    }

    public FusionFurnaceScreen(FusionChamberContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.GUI_TEXTURE = getRL( "textures/gui/container/fusion_furnace_gui.png");
    }

    @Override
    public void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY); // 调用父类方法渲染默认背景

        RenderUtils.renderProgress(pGuiGraphics, ProgressPos.x + leftPos, ProgressPos.y + topPos, menu.getProgress());

        FluidStack fluidStack1 = getMenu().getFluidIn();

        RenderUtils.renderFluid(pGuiGraphics, fluidStack1, fluidSlot1Pos.x + leftPos, fluidSlot1Pos.y + topPos, fluidSlotSize.x, fluidSlotSize.y,0, menu.getFluidCapacityIn());

        FluidStack fluidStack2 = getMenu().getFluidOut();

        RenderUtils.renderFluid(pGuiGraphics, fluidStack2, fluidSlot2Pos.x + leftPos, fluidSlot2Pos.y + topPos, fluidSlotSize.x, fluidSlotSize.y,0, menu.getFluidCapacityOut());

    }

    protected void checkfluid1Tip(GuiGraphics pGuiGraphics,FluidStack fluidStack,int capacity, int mouseX, int mouseY){
        if (mouseX >= leftPos + fluidSlot1Pos.x
                && mouseX < leftPos + fluidSlot1Pos.x + fluidSlotSize.x
                && mouseY >= topPos + fluidSlot1Pos.y
                && mouseY < topPos + fluidSlot1Pos.y + fluidSlotSize.y
        ) {
            renderfluidTip(pGuiGraphics,fluidStack,capacity, mouseX, mouseY);;
        }
    }
    protected void checkfluid2Tip(GuiGraphics pGuiGraphics,FluidStack fluidStack,int capacity,  int mouseX, int mouseY){
        if (mouseX >= leftPos + fluidSlot2Pos.x
                && mouseX < leftPos + fluidSlot2Pos.x + fluidSlotSize.x
                && mouseY >= topPos + fluidSlot2Pos.y
                && mouseY < topPos + fluidSlot2Pos.y + fluidSlotSize.y
        ) {
            renderfluidTip(pGuiGraphics,fluidStack, capacity, mouseX, mouseY);;
        }
    }
    protected void renderfluidTip(GuiGraphics pGuiGraphics,FluidStack fluidStack,int capacity, int mouseX, int mouseY){
        pGuiGraphics.renderTooltip(this.font, Component
                .literal(fluidStack.getDisplayName().getString())
                .append(NumberUtils.int2String(fluidStack.getAmount()) + "/" + NumberUtils.int2String(capacity) + " mB")
                , mouseX, mouseY);
    }

}