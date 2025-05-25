package kivo.millennium.milltek.machine.InductionFurnace;

import kivo.millennium.client.screen.AbstractDeviceSC;
import kivo.millennium.milltek.Main;
import kivo.millennium.milltek.container.Device.InductionFurnaceMenu;
import kivo.millennium.milltek.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static kivo.millennium.milltek.Main.getKey;

import org.joml.Vector2i;

@OnlyIn(Dist.CLIENT)
public class InductionFurnaceScreen extends AbstractDeviceSC<InductionFurnaceMenu> {
    private static final Vector2i FlamePos = new Vector2i(82, 53);
    public InductionFurnaceScreen(InductionFurnaceMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.GUI_TEXTURE = Main.getRL("textures/gui/container/induction_furnace_gui.png");
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(pGuiGraphics, mouseX, mouseY, partialTick);
        RenderUtils.renderProgress(pGuiGraphics, 77 + leftPos, 34 + topPos, menu.getProgress());
        if(this.menu.getLit()) RenderUtils.renderFlame(pGuiGraphics, leftPos + FlamePos.x, topPos + FlamePos.y);
    }
}
