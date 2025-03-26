package kivo.millennium.client.screen;

import kivo.millennium.millind.container.Device.MeltingFurnaceContainer;
import kivo.millennium.millind.util.NumberUtils;
import kivo.millennium.millind.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Vector2i;

import static kivo.millennium.millind.Main.getRL;

public class FusionFurnaceScreen extends AbstractDeviceSC<MeltingFurnaceContainer> {
    private static final Vector2i ProgressPos = new Vector2i(78, 38);
    private static final Vector2i fluidSlot1Pos = new Vector2i(25, 15);
    private static final Vector2i fluidSlot2Pos = new Vector2i(115, 15);
    private static final Vector2i fluidSlotSize = new Vector2i(16,57);

    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(pGuiGraphics, mouseX, mouseY, partialTick);
        FluidStack fluidStack1 = getMenu().getFluid();
        //if(!fluidStack1.isEmpty()) checkfluid1Tip(pGuiGraphics,fluidStack1,getMenu().getFluid1Capacity() mouseX, mouseY);
        FluidStack fluidStack2 = getMenu().getFluid();
        //if(!fluidStack2.isEmpty()) checkfluid2Tip(pGuiGraphics,fluidStack2,getMenu().getFluid2Capacity(), mouseX, mouseY);
    }

    public FusionFurnaceScreen(MeltingFurnaceContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.GUI_TEXTURE = getRL( "textures/gui/container/fusion_furnace_gui.png");
    }

    @Override
    public void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY); // 调用父类方法渲染默认背景

        RenderUtils.renderProgress(pGuiGraphics, 76 + leftPos, 36 + topPos, menu.getProgress());

        FluidStack fluidStack1 = getMenu().getFluid();

        RenderUtils.renderFluid(pGuiGraphics, fluidStack1, fluidSlot1Pos.x + leftPos, fluidSlot1Pos.y + topPos, fluidSlotSize.x, fluidSlotSize.y,0, menu.getFluidCapacity());

        FluidStack fluidStack2 = getMenu().getFluid();

        RenderUtils.renderFluid(pGuiGraphics, fluidStack2, fluidSlot2Pos.x + leftPos, fluidSlot2Pos.y + topPos, fluidSlotSize.x, fluidSlotSize.y,0, menu.getFluidCapacity());

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