package kivo.millennium.milltek.datagen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import kivo.millennium.milltek.Main;
import kivo.millennium.milltek.init.MillenniumItems;

import static kivo.millennium.milltek.Main.MODID;
import static kivo.millennium.milltek.Main.getKey;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends TagsProvider<Item> {


    public ItemTagProvider(PackOutput p_256596_, CompletableFuture p_256513_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_256596_, Registries.ITEM, p_256513_, MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider p_256380_) {
        TagKey<Item> OopartTag = ItemTags.create(Main.getRL("oopart"));

        MillenniumItems.OOPARTS.forEach(oopartRegistryObject -> {
            this.tag(OopartTag).add(TagEntry.element(oopartRegistryObject.getId()));
        });

        //TagKey<Item>
    }
}
