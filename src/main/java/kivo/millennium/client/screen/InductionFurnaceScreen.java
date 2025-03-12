package kivo.millennium.client.screen;

import kivo.millennium.millind.container.Device.AbstractDeviceMT;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Vector2i;

import static kivo.millennium.millind.Main.getRL;

public class InductionFurnaceScreen extends AbstractDeviceSC<AbstractDeviceMT> {
    public static Vector2i invlabel = new Vector2i(8, 82);
    public InductionFurnaceScreen(AbstractDeviceMT container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.imageHeight = 176;
        this.imageWidth = 176;
        this.BatteryPos = new Vector2i(152, 8);
        this.EnergyPos = new Vector2i(127, 7);
        this.inventoryLabelX = invlabel.x;
        this.inventoryLabelY = invlabel.y;
        this.GUI_TEXTURE = getRL("textures/gui/container/induction_furnace.png");
    }
}
