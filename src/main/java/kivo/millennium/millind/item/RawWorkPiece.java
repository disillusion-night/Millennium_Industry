package kivo.millennium.millind.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RawWorkPiece extends Item {
    int progress;
    int total;
    public RawWorkPiece(Properties pProperties, int progress, int total) {
        super(pProperties);
        this.progress = progress;
        this.total = total;

    }

    public RawWorkPiece(Properties pProperties, int total) {
        super(pProperties);
        this.progress = 0;
        this.total = total;

    }

    public void setTotal(){
        this.total = total;
    }

    public int process(){
        progress ++;
        return total - progress;
    }

}
