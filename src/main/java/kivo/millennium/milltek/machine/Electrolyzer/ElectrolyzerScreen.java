package kivo.millennium.milltek.machine.Electrolyzer;

import kivo.millennium.client.screen.AbstractDeviceSC;
import kivo.millennium.milltek.Main;
import kivo.millennium.milltek.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Vector2i;

@OnlyIn(Dist.CLIENT)
public class ElectrolyzerScreen extends AbstractDeviceSC<ElectrolyzerMenu> {
  private static final Vector2i ProgressPos = new Vector2i(76, 36);

  public ElectrolyzerScreen(ElectrolyzerMenu menu, Inventory playerInventory, Component title) {
    super(menu, playerInventory, title);
    this.GUI_TEXTURE = Main.getRL("textures/gui/container/electrolyzer_gui.png");
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    super.render(guiGraphics, mouseX, mouseY, partialTick);
  }
}