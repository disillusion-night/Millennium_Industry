package kivo.millennium.client.datagen.language;


import kivo.millennium.millind.init.MillenniumBlocks;
import kivo.millennium.millind.init.MillenniumCreativeTab;
import kivo.millennium.millind.init.MillenniumFluidTypes;
import kivo.millennium.millind.init.MillenniumItems;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;

public class EnglishProvider extends MillenniumLanguageProvider {
    public EnglishProvider(PackOutput output) {
        super(output, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(MillenniumBlocks.METAL_TANK_BL.get(), "Metal Tank");
        add(MillenniumBlocks.HDEC_BL.get(), "Hyper-Dimension Engineering Core");
        add(MillenniumBlocks.NETHER_STAR_LASER_BL.get(), "Nether Star Laser");

        add(MillenniumItems.HighPurityWolfseggSteel.get(), "High-Purity Wolfsegg Steel");
        add(MillenniumItems.LowPurityWolfseggSteel.get(), "Low-Purity Wolfsegg Steel");
        add(MillenniumItems.WolfseggIron.get(), "Wolfsegg Iron");
        add(MillenniumItems.WolfseggIronOre.get(), "Wolfsegg Iron Ore");

        add(MillenniumFluidTypes.ICY_WATER_FLUID_TYPE.get().getDescriptionId(), "Icy Water");

        add(Util.makeDescriptionId("item_group", BuiltInRegistries.CREATIVE_MODE_TAB.getKey(MillenniumCreativeTab.OOPARTS.get())), "Millennium Industry:Oopart");
        add(Util.makeDescriptionId("item_group", BuiltInRegistries.CREATIVE_MODE_TAB.getKey(MillenniumCreativeTab.ENGINEERING_PARTS.get())), "Millennium Industry:Engineering Parts");
    }


    private void addEnglishFromId(Item item){
        add(item, item.getDescriptionId());
    }
}
