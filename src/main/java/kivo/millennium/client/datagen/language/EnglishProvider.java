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
        add(MillenniumBlocks.GENERATOR_BL.get(), "Generator");
        add(MillenniumBlocks.INDUCTION_FURNACE_BL.get(), "Induction Furnace");
        add(MillenniumBlocks.CRUSHER_BL.get(), "Crusher");
        add(MillenniumBlocks.SOLAR_GENERATOR.get(), "Solar Generator");
        add(MillenniumBlocks.LEAD_BLOCK.get(), "Lead Block");
        add(MillenniumBlocks.RAW_LEAD_BLOCK.get(), "Raw Lead Block");
        add(MillenniumBlocks.LEAD_ORE.get(), "Lead Ore");
        add(MillenniumBlocks.DEEPSLATE_LEAD_ORE.get(), "Deepslate Lead Ore");
        add(MillenniumBlocks.ALUMINUM_BLOCK.get(), "Aluminum Block");
        add(MillenniumBlocks.RAW_ALUMINUM_BLOCK.get(), "Raw Aluminum Block");
        add(MillenniumBlocks.ALUMINUM_ORE.get(), "Aluminum Ore");
        add(MillenniumBlocks.DEEPSLATE_ALUMINUM_ORE.get(), "Deepslate Aluminum Ore");

        add(MillenniumItems.HighPurityWolfseggSteel.get(), "High-Purity Wolfsegg Steel");
        add(MillenniumItems.LowPurityWolfseggSteel.get(), "Low-Purity Wolfsegg Steel");
        add(MillenniumItems.WolfseggIron.get(), "Wolfsegg Iron");
        add(MillenniumItems.WolfseggIronOre.get(), "Wolfsegg Iron Ore");
        add(MillenniumItems.VRLA.get(), "Valve Regulated Lead Acid Battery");
        add(MillenniumItems.COPPER_DUST.get(), "Copper Dust");
        add(MillenniumItems.IRON_DUST.get(), "Iron Dust");
        add(MillenniumItems.GOLD_DUST.get(), "Gold Dust");
        add(MillenniumItems.LEAD_DUST.get(), "Lead Dust");
        add(MillenniumItems.LEAD_INGOT.get(), "Lead Ingot");
        add(MillenniumItems.LEAD_NUGGET.get(), "Lead Nugget");
        add(MillenniumItems.RAW_LEAD.get(), "Raw Lead");
        add(MillenniumItems.ALUMINUM_INGOT.get(), "Aluminum Ingot");
        add(MillenniumItems.ALUMINUM_NUGGET.get(), "Aluminum Nugget");
        add(MillenniumItems.RAW_ALUMINUM.get(), "Raw Aluminum");

        add(MillenniumFluidTypes.ICY_WATER_FLUID_TYPE.get().getDescriptionId(), "Icy Water");

        add(Util.makeDescriptionId("item_group", BuiltInRegistries.CREATIVE_MODE_TAB.getKey(MillenniumCreativeTab.OOPARTS.get())), "Millennium Industry:Oopart");
        add(Util.makeDescriptionId("item_group", BuiltInRegistries.CREATIVE_MODE_TAB.getKey(MillenniumCreativeTab.ENGINEERING_PARTS.get())), "Millennium Industry:Engineering Parts");
    }


    private void addEnglishFromId(Item item){
        add(item, item.getDescriptionId());
    }
}
