package kivo.millennium.client.datagen.language;

import static kivo.millennium.milltek.Main.getKey;
import static kivo.millennium.milltek.Main.log;

import kivo.millennium.milltek.Main;
import kivo.millennium.milltek.init.MillenniumBlocks;
import kivo.millennium.milltek.init.MillenniumCreativeTab;
import kivo.millennium.milltek.init.MillenniumFluidTypes;
import kivo.millennium.milltek.init.MillenniumGases;
import kivo.millennium.milltek.init.MillenniumItems;
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
        log("Collecting English Translations");

        add(MillenniumBlocks.METAL_TANK_BL.get(), "Metal Tank");
        add(MillenniumBlocks.HDEC_BL.get(), "Hyper-Dimension Engineering Core");
        add(MillenniumBlocks.NETHER_STAR_LASER_BL.get(), "Nether Star Laser");
        add(MillenniumBlocks.INDUCTION_FURNACE_BL.get(), "Induction Furnace");
        add(MillenniumBlocks.CRUSHER_BL.get(), "Crusher");
        add(MillenniumBlocks.FUSION_CHAMBER_BL.get(), "Fusion Chamber");
        add(MillenniumBlocks.MELTING_FURNACE_BL.get(), "Melting Furnace");
        add(MillenniumBlocks.RESONANCE_CHAMBER_BL.get(), "Resonance Chamber");
        add(MillenniumBlocks.CRYSTALLIZER_BL.get(), "Crystallizer");
        add(MillenniumBlocks.SOLAR_GENERATOR.get(), "Solar Generator");

        add(MillenniumBlocks.ALERT_BLOCK.get(), "Alert Block");
        // add(MillenniumBlocks.ALERT_LINE_BL.get(), "Alert Line Block");

        add(MillenniumItems.HighPurityWolfseggSteel.get(), "High-Purity Wolfsegg Steel");
        add(MillenniumItems.LowPurityWolfseggSteel.get(), "Low-Purity Wolfsegg Steel");
        add(MillenniumItems.WolfseggIron.get(), "Wolfsegg Iron");
        add(MillenniumItems.WolfseggIronOre.get(), "Wolfsegg Iron Ore");
        add(MillenniumItems.VRLA.get(), "Valve Regulated Lead Acid Battery");

        add(MillenniumItems.CARBON_DUST.get(), "Carbon Dust");

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

        add(MillenniumBlocks.TITANIUM_ALLOY_BLOCK.get(), "Titanium Alloy Block");
        add(MillenniumItems.TITANIUM_ALLOY_INGOT.get(), "Titanium Alloy Ingot");
        add(MillenniumItems.TITANIUM_ALLOY_PANEL.get(), "Titanium Alloy Panel");
        add(MillenniumItems.TITANIUM_ALLOY_PIPE.get(), "Titanium Alloy Pipe");
        add(MillenniumItems.TITANIUM_ALLOY_ROD.get(), "Titanium Alloy Rod");
        add(MillenniumItems.TITANIUM_ALLOY_NUGGET.get(), "Titanium Alloy Nugget");
        add(MillenniumItems.TITANIUM_ALLOY_DUST.get(), "Titanium Alloy Dust");
        add(MillenniumItems.TITANIUM_ALLOY_GEAR.get(), "Titanium Alloy Gear");
        add(MillenniumItems.TITANIUM_ALLOY_AXE.get(), "Titanium Alloy Axe");
        add(MillenniumItems.TITANIUM_ALLOY_HOE.get(), "Titanium Alloy Hoe");
        add(MillenniumItems.TITANIUM_ALLOY_PICKAXE.get(), "Titanium Alloy Pickaxe");
        add(MillenniumItems.TITANIUM_ALLOY_SHOVEL.get(), "Titanium Alloy Shovel");
        add(MillenniumItems.TITANIUM_ALLOY_SWORD.get(), "Titanium Alloy Sword");

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
        add(MillenniumItems.STEEL_GEAR.get(), "Steel Gear");
        add(MillenniumItems.STEEL_AXE.get(), "Steel Axe");
        add(MillenniumItems.STEEL_HOE.get(), "Steel Hoe");
        add(MillenniumItems.STEEL_PICKAXE.get(), "Steel Pickaxe");
        add(MillenniumItems.STEEL_SHOVEL.get(), "Steel Shovel");
        add(MillenniumItems.STEEL_SWORD.get(), "Steel Sword");

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

        add(MillenniumBlocks.COPPER_PIPE.get(), "Copper Pipe");
        add(MillenniumBlocks.ENERGY_PIPE.get(), "Energy Pipe");

        add(MillenniumBlocks.WOLFRAM_STEEL_BLOCK.get(), "Wolfram Steel Block");
        add(MillenniumItems.WOLFRAM_STEEL_INGOT.get(), "Wolfram Steel Ingot");
        add(MillenniumItems.WOLFRAM_STEEL_PANEL.get(), "Wolfram Steel Panel");
        add(MillenniumItems.WOLFRAM_STEEL_PIPE.get(), "Wolfram Steel Pipe");
        add(MillenniumItems.WOLFRAM_STEEL_ROD.get(), "Wolfram Steel Rod");
        add(MillenniumItems.WOLFRAM_STEEL_NUGGET.get(), "Wolfram Steel Nugget");
        add(MillenniumItems.WOLFRAM_STEEL_DUST.get(), "Wolfram Steel Dust");
        add(MillenniumItems.WOLFRAM_STEEL_GEAR.get(), "Wolfram Steel Gear");
        add(MillenniumItems.WOLFRAM_STEEL_AXE.get(), "Wolfram Steel Axe");
        add(MillenniumItems.WOLFRAM_STEEL_HOE.get(), "Wolfram Steel Hoe");
        add(MillenniumItems.WOLFRAM_STEEL_PICKAXE.get(), "Wolfram Steel Pickaxe");
        add(MillenniumItems.WOLFRAM_STEEL_SHOVEL.get(), "Wolfram Steel Shovel");
        add(MillenniumItems.WOLFRAM_STEEL_SWORD.get(), "Wolfram Steel Sword");

        add(MillenniumItems.WRENCH.get(), "Wrench");
        add(MillenniumGases.STEAM.get(), "Steam");

        add(MillenniumCreativeTab.OOPARTS.get(), "Millennium Industry:Ooparts");
        add(MillenniumCreativeTab.ENGINEERING_PARTS.get(), "Millennium Industry:Engineering Parts");
        add(MillenniumCreativeTab.MATERIALS.get(), "Millennium Industry:Materials");
        add(MillenniumCreativeTab.TOOLS.get(), "Millennium Industry:Tools");
        add(MillenniumCreativeTab.CONTAINERS.get(), "Millennium Industry:Containers");
    }

    private void addEnglishFromId(Item item) {
        add(item, Main.getKey(item).getPath());
    }

}
