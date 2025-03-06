package kivo.millennium.millind.item.Oopart;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import static kivo.millennium.millind.Main.getRL;

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
        return getRL("textures/gui/background/"+this.level.toString().toLowerCase()+".png");
    }
}
