package kivo.millennium.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import kivo.millennium.millind.container.Device.CrusherContainer;
import kivo.millennium.millind.container.Device.MeltingFurnaceContainer;
import kivo.millennium.millind.util.NumberUtils;
import kivo.millennium.millind.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Matrix4f;
import org.joml.Vector2i;

import static kivo.millennium.millind.Main.getRL;

public class MeltingFurnaceScreen extends AbstractDeviceSC<MeltingFurnaceContainer> {
    private static final Vector2i ProgressPos = new Vector2i(77, 34);
    private static final Vector2i fluidSlotPos = new Vector2i(107, 16);
    private static final Vector2i fluidSlotSize = new Vector2i(16,57);

    private static final ResourceLocation PROGRESS_TEXTURE = getRL("textures/gui/container/progress_arrow.png");

    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(pGuiGraphics, mouseX, mouseY, partialTick);
        checkfluidTip(pGuiGraphics, mouseX, mouseY);
    }

    public MeltingFurnaceScreen(MeltingFurnaceContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.GUI_TEXTURE = getRL( "textures/gui/container/melting_furnace_gui.png");
    }

    @Override
    public void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY); // 调用父类方法渲染默认背景

        RenderUtils.renderProgress(pGuiGraphics, 77 + leftPos, 34 + topPos, menu.getProgress());

        FluidStack fluidStack = getMenu().getFluid();

        RenderUtils.renderFluid(pGuiGraphics, fluidStack, fluidSlotPos.x + leftPos, fluidSlotPos.y + topPos, fluidSlotSize.x, fluidSlotSize.y,0, menu.getFluidCapacity());
    }

    protected void checkfluidTip(GuiGraphics pGuiGraphics, int mouseX, int mouseY){
        if (mouseX >= leftPos + fluidSlotPos.x
                && mouseX < leftPos + fluidSlotPos.x + fluidSlotSize.x
                && mouseY >= topPos + fluidSlotPos.y
                && mouseY < topPos + fluidSlotPos.y + fluidSlotSize.y
        ) {
            renderfluidTip(pGuiGraphics, mouseX, mouseY);;
        }
    }
    protected void renderfluidTip(GuiGraphics pGuiGraphics, int mouseX, int mouseY){
        pGuiGraphics.renderTooltip(this.font, Component
                .literal(menu.getFluid().getDisplayName().getString())
                .append(NumberUtils.int2String(menu.getFluidAmount()) + "/" + NumberUtils.int2String(menu.getFluidCapacity()) + " mB")
                , mouseX, mouseY);
    }

}