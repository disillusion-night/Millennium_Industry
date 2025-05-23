package kivo.millennium.client.datagen.language;

import kivo.millennium.millind.machine.InductionFurnace.InductionFurnaceBL;
import kivo.millennium.millind.init.MillenniumBlocks;
import kivo.millennium.millind.init.MillenniumCreativeTab;
import kivo.millennium.millind.init.MillenniumFluidTypes;
import kivo.millennium.millind.init.MillenniumItems;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;

import static kivo.millennium.millind.Main.log;

public class SimplifiedChineseProvider extends MillenniumLanguageProvider {
    public SimplifiedChineseProvider(PackOutput output) {
        super(output, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        log("Collecting Chinese Translations");

        add(MillenniumBlocks.METAL_TANK_BL.get(), "金属液罐");
        add(MillenniumBlocks.HDEC_BL.get(), "高维工程核心");
        add(MillenniumBlocks.NETHER_STAR_LASER_BL.get(), "下界之星激光器");
        add(MillenniumBlocks.INDUCTION_FURNACE_BL.get(), "感应炉");
        add(MillenniumBlocks.CRUSHER_BL.get(), "粉碎机");
        add(MillenniumBlocks.FUSION_CHAMBER_BL.get(), "混融室");
        add(MillenniumBlocks.MELTING_FURNACE_BL.get(), "熔融炉");
        add(MillenniumBlocks.RESONANCE_CHAMBER_BL.get(), "谐振仓");
        add(MillenniumBlocks.CRYSTALLIZER_BL.get(), "结晶器");
        add(MillenniumBlocks.SOLAR_GENERATOR.get(), "太阳能发电机");

        add(MillenniumBlocks.ALERT_BLOCK.get(), "警示方块");

        add(MillenniumItems.HighPurityWolfseggSteel.get(), "高纯度沃普赛克钢铁");
        add(MillenniumItems.LowPurityWolfseggSteel.get(), "低纯度沃普赛克钢铁");
        add(MillenniumItems.WolfseggIron.get(), "沃普赛克铁块");
        add(MillenniumItems.WolfseggIronOre.get(), "沃普赛克铁矿");
        add(MillenniumItems.VRLA.get(), "铅酸蓄电池");

        add(MillenniumItems.ALUMINIUM_DUST.get(), "铝粉");
        add(MillenniumItems.ALUMINUM_INGOT.get(), "铝锭");
        add(MillenniumItems.ALUMINUM_NUGGET.get(), "铝粒");
        add(MillenniumItems.RAW_ALUMINUM.get(), "粗铝");
        add(MillenniumItems.RAW_ALUMINUM_DUST.get(), "粗铝矿粉");
        add(MillenniumBlocks.ALUMINUM_BLOCK.get(), "铝块");
        add(MillenniumBlocks.RAW_ALUMINUM_BLOCK.get(), "粗铝块");
        add(MillenniumBlocks.ALUMINUM_ORE.get(), "铝矿石");
        add(MillenniumBlocks.DEEPSLATE_ALUMINUM_ORE.get(), "深板岩铝矿");

        add(MillenniumBlocks.ALUMINUM_ALLOY_BLOCK.get(), "铝合金块");
        add(MillenniumItems.ALUMINUM_ALLOY_INGOT.get(), "铝合金锭");
        add(MillenniumItems.ALUMINUM_ALLOY_PANEL.get(), "铝合金板");
        add(MillenniumItems.ALUMINUM_ALLOY_PIPE.get(), "铝合金管");
        add(MillenniumItems.ALUMINUM_ALLOY_ROD.get(), "铝合金杆");
        add(MillenniumItems.ALUMINUM_ALLOY_NUGGET.get(), "铝合金粒");
        add(MillenniumItems.ALUMINUM_ALLOY_DUST.get(), "铝合金粉");

        add(MillenniumBlocks.TITANIUM_ALLOY_BLOCK.get(), "钛合金块");
        add(MillenniumItems.TITANIUM_ALLOY_INGOT.get(), "钛合金锭");
        add(MillenniumItems.TITANIUM_ALLOY_PANEL.get(), "钛合金板");
        add(MillenniumItems.TITANIUM_ALLOY_PIPE.get(), "钛合金管");
        add(MillenniumItems.TITANIUM_ALLOY_ROD.get(), "钛合金杆");
        add(MillenniumItems.TITANIUM_ALLOY_NUGGET.get(), "钛合金粒");
        add(MillenniumItems.TITANIUM_ALLOY_DUST.get(), "钛合金粉");
        add(MillenniumItems.TITANIUM_ALLOY_GEAR.get(), "钛合金齿轮");
        add(MillenniumItems.TITANIUM_ALLOY_AXE.get(), "钛合金斧");
        add(MillenniumItems.TITANIUM_ALLOY_HOE.get(), "钛合金锄");
        add(MillenniumItems.TITANIUM_ALLOY_PICKAXE.get(), "钛合金镐");
        add(MillenniumItems.TITANIUM_ALLOY_SHOVEL.get(), "钛合金锹");
        add(MillenniumItems.TITANIUM_ALLOY_SWORD.get(), "钛合金军刀");
        
        add(MillenniumItems.CARBON_DUST.get(), "碳粉");

        add(MillenniumItems.CRYOLITE.get(), "冰晶石");
        add(MillenniumItems.CRYOLITE_DUST.get(), "冰晶石粉");

        add(MillenniumItems.COPPER_DUST.get(), "铜粉");

        add(MillenniumItems.IRON_DUST.get(), "铁粉");
        add(MillenniumItems.IRON_PANEL.get(), "铁板");
        add(MillenniumItems.IRON_PIPE.get(), "铁管");
        add(MillenniumItems.IRON_ROD.get(), "铁杆");

        add(MillenniumItems.GOLD_DUST.get(), "金粉");
        add(MillenniumItems.GOLD_PANEL.get(), "金板");

        add(MillenniumItems.STONE_DUST.get(), "石头粉");

        add(MillenniumBlocks.STEEL_BLOCK.get(), "钢块");
        add(MillenniumItems.STEEL_DUST.get(), "钢粉");
        add(MillenniumItems.STEEL_INGOT.get(), "钢锭");
        add(MillenniumItems.STEEL_NUGGET.get(), "钢粒");
        add(MillenniumItems.STEEL_PANEL.get(), "钢板");
        add(MillenniumItems.STEEL_PIPE.get(), "钢管");
        add(MillenniumItems.STEEL_ROD.get(), "钢杆");
        add(MillenniumItems.STEEL_GEAR.get(), "钢齿轮");
        add(MillenniumItems.STEEL_AXE.get(), "钢斧");
        add(MillenniumItems.STEEL_HOE.get(), "钢锄");
        add(MillenniumItems.STEEL_PICKAXE.get(), "钢镐");
        add(MillenniumItems.STEEL_SHOVEL.get(), "钢锹");
        add(MillenniumItems.STEEL_SWORD.get(), "钢剑");

        add(MillenniumItems.LEAD_DUST.get(), "铅粉");
        add(MillenniumItems.LEAD_INGOT.get(), "铅锭");
        add(MillenniumItems.LEAD_NUGGET.get(), "铅粒");
        add(MillenniumItems.LEAD_PANEL.get(), "铅板");
        add(MillenniumItems.LEAD_PIPE.get(), "铅管");
        add(MillenniumItems.LEAD_ROD.get(), "铅杆");
        add(MillenniumItems.RAW_LEAD.get(), "粗铅");
        add(MillenniumBlocks.LEAD_BLOCK.get(), "铅块");
        add(MillenniumBlocks.RAW_LEAD_BLOCK.get(), "粗铅块");
        add(MillenniumBlocks.LEAD_ORE.get(), "铅矿石");
        add(MillenniumBlocks.DEEPSLATE_LEAD_ORE.get(), "深板岩铅矿");
        
        add(MillenniumBlocks.WOLFRAM_STEEL_BLOCK.get(), "钨钢块");
        add(MillenniumItems.WOLFRAM_STEEL_INGOT.get(), "钨钢锭");
        add(MillenniumItems.WOLFRAM_STEEL_PANEL.get(), "钨钢板");
        add(MillenniumItems.WOLFRAM_STEEL_PIPE.get(), "钨钢管");
        add(MillenniumItems.WOLFRAM_STEEL_ROD.get(), "钨钢杆");
        add(MillenniumItems.WOLFRAM_STEEL_NUGGET.get(), "钨钢粒");
        add(MillenniumItems.WOLFRAM_STEEL_DUST.get(), "钨钢粉");
        add(MillenniumItems.WOLFRAM_STEEL_GEAR.get(), "钨钢齿轮");
        add(MillenniumItems.WOLFRAM_STEEL_AXE.get(), "钨钢斧");
        add(MillenniumItems.WOLFRAM_STEEL_HOE.get(), "钨钢锄");
        add(MillenniumItems.WOLFRAM_STEEL_PICKAXE.get(), "钨钢镐");
        add(MillenniumItems.WOLFRAM_STEEL_SHOVEL.get(), "钨钢锹");
        add(MillenniumItems.WOLFRAM_STEEL_SWORD.get(), "钨钢军刀");
        
        add(MillenniumFluidTypes.ICY_WATER_FLUID_TYPE.get().getDescriptionId(), "冰水");
        add(MillenniumFluidTypes.MOLTEN_ALUMINUM_FT.get().getDescriptionId(), "熔融铝");
        add(MillenniumFluidTypes.MOLTEN_CRYOLITE_FT.get().getDescriptionId(), "熔融冰晶石");
        add(MillenniumFluidTypes.MOLTEN_IRON_FT.get().getDescriptionId(), "熔融铁");
        add(MillenniumFluidTypes.MOLTEN_STEEL_FT.get().getDescriptionId(), "熔融钢");
        add(MillenniumFluidTypes.RAW_MOLTEN_ALUMINUM_FT.get().getDescriptionId(), "粗熔融铝");
        add(MillenniumFluidTypes.MOLTEN_ALUMINUM_ALLOY_FT.get().getDescriptionId(), "熔融铝合金");



        add(MillenniumItems.WRENCH.get(), "扳手");

        add(Util.makeDescriptionId("item_group", BuiltInRegistries.CREATIVE_MODE_TAB.getKey(MillenniumCreativeTab.OOPARTS.get())), "千年科工 神秘古物");
        add(Util.makeDescriptionId("item_group", BuiltInRegistries.CREATIVE_MODE_TAB.getKey(MillenniumCreativeTab.ENGINEERING_PARTS.get())), "千年科工 工程组件");
        add(Util.makeDescriptionId("item_group", BuiltInRegistries.CREATIVE_MODE_TAB.getKey(MillenniumCreativeTab.MATERIALS.get())), "千年科工 材料");
        add(Util.makeDescriptionId("item_group", BuiltInRegistries.CREATIVE_MODE_TAB.getKey(MillenniumCreativeTab.TOOLS.get())), "千年科工 工具");
    }
}
