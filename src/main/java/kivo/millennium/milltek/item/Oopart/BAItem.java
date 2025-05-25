package kivo.millennium.milltek.item.Oopart;

import static kivo.millennium.milltek.Main.getKey;

import kivo.millennium.milltek.Main;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class BAItem extends Item implements IBABackground {
    private final BAItemLevel level;
    public BAItem(Properties pProperties, BAItemLevel _level) {
        super(pProperties);
        this.level = _level;
    }

    public BAItemLevel getLevel() {
        return this.level;
    }

    @Override
    public ResourceLocation getBackGround() {
        return Main.getRL("textures/gui/background/"+this.level.toString().toLowerCase()+".png");
    }
}
