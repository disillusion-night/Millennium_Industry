package kivo.millennium.client.datagen.language;


import kivo.millennium.millind.init.MillenniumBlocks;
import kivo.millennium.millind.init.MillenniumCreativeTab;
import kivo.millennium.millind.init.MillenniumFluidTypes;
import kivo.millennium.millind.init.MillenniumItems;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;

import static kivo.millennium.millind.Main.getRL;

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
        add(MillenniumBlocks.FUSION_CHAMBER_BL.get(), "Fusion Chamber");
        add(MillenniumBlocks.MELTING_FURNACE_BL.get(), "Melting Furnace");
        add(MillenniumBlocks.SOLAR_GENERATOR.get(), "Solar Generator");

        add(MillenniumItems.HighPurityWolfseggSteel.get(), "High-Purity Wolfsegg Steel");
        add(MillenniumItems.LowPurityWolfseggSteel.get(), "Low-Purity Wolfsegg Steel");
        add(MillenniumItems.WolfseggIron.get(), "Wolfsegg Iron");
        add(MillenniumItems.WolfseggIronOre.get(), "Wolfsegg Iron Ore");
        add(MillenniumItems.VRLA.get(), "Valve Regulated Lead Acid Battery");
        add(MillenniumItems.COPPER_DUST.get(), "Copper Dust");

        add(MillenniumBlocks.ALUMINUM_BLOCK.get(), "Aluminum Block");
        add(MillenniumItems.ALUMINIUM_DUST.get(), "Aluminum Dust");
        add(MillenniumItems.ALUMINUM_INGOT.get(), "Aluminum Ingot");
        add(MillenniumItems.ALUMINUM_NUGGET.get(), "Aluminum Nugget");
        add(MillenniumItems.RAW_ALUMINUM.get(), "Raw Aluminum");
        add(MillenniumItems.RAW_ALUMINUM_DUST.get(), "Raw Aluminum Dust");
        add(MillenniumBlocks.RAW_ALUMINUM_BLOCK.get(), "Raw Aluminum Block");
        add(MillenniumBlocks.ALUMINUM_ORE.get(), "Aluminum Ore");
        add(MillenniumBlocks.DEEPSLATE_ALUMINUM_ORE.get(), "Deepslate Aluminum Ore");

        add(MillenniumBlocks.ALUMINUM_ALLOY_BLOCK.get(), "Aluminum Alloy Block");
        add(MillenniumItems.ALUMINUM_ALLOY_INGOT.get(), "Aluminum Alloy Ingot");
        add(MillenniumItems.ALUMINUM_ALLOY_PANEL.get(), "Aluminum Alloy Panel");
        add(MillenniumItems.ALUMINUM_ALLOY_PIPE.get(), "Aluminum Alloy Pipe");
        add(MillenniumItems.ALUMINUM_ALLOY_ROD.get(), "Aluminum Alloy Rod");
        add(MillenniumItems.ALUMINUM_ALLOY_NUGGET.get(), "Aluminum Alloy Nugget");
        add(MillenniumItems.ALUMINUM_ALLOY_DUST.get(), "Aluminum Alloy Dust");

        add(MillenniumItems.CRYOLITE.get(), "Cryolite");
        add(MillenniumItems.CRYOLITE_DUST.get(), "Cryolite Dust");

        add(MillenniumItems.IRON_DUST.get(), "Iron Dust");
        add(MillenniumItems.IRON_PANEL.get(), "Iron Panel");
        add(MillenniumItems.IRON_PIPE.get(), "Iron Pipe");
        add(MillenniumItems.IRON_ROD.get(), "Iron Rod");

        add(MillenniumItems.GOLD_DUST.get(), "Gold Dust");
        add(MillenniumItems.GOLD_PANEL.get(), "Gold Panel");

        add(MillenniumItems.STONE_DUST.get(), "Stone Dust");

        add(MillenniumBlocks.STEEL_BLOCK.get(), "Steal Block");
        add(MillenniumItems.STEEL_DUST.get(), "Steel Dust");
        add(MillenniumItems.STEEL_INGOT.get(), "Steel Ingot");
        add(MillenniumItems.STEEL_NUGGET.get(), "Steel Nugget");
        add(MillenniumItems.STEEL_PANEL.get(), "Steel Panel");
        add(MillenniumItems.STEEL_PIPE.get(), "Steel Pipe");
        add(MillenniumItems.STEEL_ROD.get(), "Steel Rod");

        add(MillenniumItems.LEAD_DUST.get(), "Lead Dust");
        add(MillenniumItems.LEAD_INGOT.get(), "Lead Ingot");
        add(MillenniumItems.LEAD_NUGGET.get(), "Lead Nugget");
        add(MillenniumItems.LEAD_PANEL.get(), "Lead Panel");
        add(MillenniumItems.LEAD_PIPE.get(), "Lead Pipe");
        add(MillenniumItems.LEAD_ROD.get(), "Lead Rod");
        add(MillenniumItems.RAW_LEAD.get(), "Raw Lead");
        add(MillenniumBlocks.LEAD_BLOCK.get(), "Lead Block");
        add(MillenniumBlocks.RAW_LEAD_BLOCK.get(), "Raw Lead Block");
        add(MillenniumBlocks.LEAD_ORE.get(), "Lead Ore");
        add(MillenniumBlocks.DEEPSLATE_LEAD_ORE.get(), "Deepslate Lead Ore");

        add(MillenniumFluidTypes.ICY_WATER_FLUID_TYPE.get().getDescriptionId(), "Icy Water");
        add(MillenniumFluidTypes.MOLTEN_ALUMINUM_FT.get().getDescriptionId(), "Molten Aluminum");
        add(MillenniumFluidTypes.MOLTEN_CRYOLITE_FT.get().getDescriptionId(), "Molten Cryolite");
        add(MillenniumFluidTypes.MOLTEN_IRON_FT.get().getDescriptionId(), "Molten Iron");
        add(MillenniumFluidTypes.MOLTEN_STEEL_FT.get().getDescriptionId(), "Molten Steel");
        add(MillenniumFluidTypes.RAW_MOLTEN_ALUMINUM_FT.get().getDescriptionId(), "Raw Molten Aluminum");
        add(MillenniumFluidTypes.MOLTEN_ALUMINUM_ALLOY_FT.get().getDescriptionId(), "Molten Aluminum Alloy");

        add(Util.makeDescriptionId("item_group", BuiltInRegistries.CREATIVE_MODE_TAB.getKey(MillenniumCreativeTab.OOPARTS.get())), "Millennium Industry:Oopart");
        add(Util.makeDescriptionId("item_group", BuiltInRegistries.CREATIVE_MODE_TAB.getKey(MillenniumCreativeTab.ENGINEERING_PARTS.get())), "Millennium Industry:Engineering Parts");
        add(Util.makeDescriptionId("item_group", BuiltInRegistries.CREATIVE_MODE_TAB.getKey(MillenniumCreativeTab.MATERIALS.get())), "Millennium Industry:Materials");
    }


    private void addEnglishFromId(Item item){
        add(item, getRL(item).getPath());
    }
}
