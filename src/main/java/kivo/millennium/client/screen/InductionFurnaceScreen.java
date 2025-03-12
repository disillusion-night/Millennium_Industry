package kivo.millennium.client.screen;

import kivo.millennium.millind.container.Device.AbstractDeviceMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Vector2i;

import static kivo.millennium.millind.Main.getRL;

public class InductionFurnaceScreen extends AbstractDeviceSC<AbstractDeviceMenu> {
    public InductionFurnaceScreen(AbstractDeviceMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.imageHeight = 176;
        this.imageWidth = 176;
        this.BatteryPos = new Vector2i(152, 8);
        this.EnergyPos = new Vector2i(127, 7);
        this.inventoryLabelY = 82;
        this.GUI_TEXTURE = getRL("textures/gui/container/induction_furnace.png");
    }
}
