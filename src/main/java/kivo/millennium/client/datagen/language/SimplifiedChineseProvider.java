package kivo.millennium.client.datagen.language;

import kivo.millennium.millind.block.device.inductionFurnace.InductionFurnaceBL;
import kivo.millennium.millind.init.MillenniumBlocks;
import kivo.millennium.millind.init.MillenniumCreativeTab;
import kivo.millennium.millind.init.MillenniumFluidTypes;
import kivo.millennium.millind.init.MillenniumItems;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;

public class SimplifiedChineseProvider extends MillenniumLanguageProvider {
    public SimplifiedChineseProvider(PackOutput output) {
        super(output, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        add(MillenniumBlocks.METAL_TANK_BL.get(), "金属液罐");
        add(MillenniumBlocks.HDEC_BL.get(), "高维工程核心");
        add(MillenniumBlocks.NETHER_STAR_LASER_BL.get(), "下界之星激光器");
        add(MillenniumBlocks.GENERATOR_BL.get(), "发电机");
        add(MillenniumBlocks.INDUCTION_FURNACE_BL.get(), "感应炉");
        add(MillenniumBlocks.CRUSHER_BL.get(), "粉碎机");
        add(MillenniumBlocks.SOLAR_GENERATOR.get(), "太阳能发电机");
        //add(MillenniumBlocks.HMI_BL.get(), "千禧年人机接口");
        //add(MillenniumBlocks.PROJECTOR_BL.get(), "千禧年蓝图投影仪");

        add(MillenniumItems.HighPurityWolfseggSteel.get(), "高纯度沃普赛克钢铁");
        add(MillenniumItems.LowPurityWolfseggSteel.get(), "低纯度沃普赛克钢铁");
        add(MillenniumItems.WolfseggIron.get(), "沃普赛克铁块");
        add(MillenniumItems.WolfseggIronOre.get(), "沃普赛克铁矿");
        add(MillenniumItems.VRLA.get(), "铅酸蓄电池");

        add(MillenniumFluidTypes.ICY_WATER_FLUID_TYPE.get().getDescriptionId(), "冰水");

        add(InductionFurnaceBL.SCREEN_INDUCTION_FURNACE.getString(), "感应炉");

        add(Util.makeDescriptionId("item_group", BuiltInRegistries.CREATIVE_MODE_TAB.getKey(MillenniumCreativeTab.OOPARTS.get())), "千年科工 神秘古物");
        add(Util.makeDescriptionId("item_group", BuiltInRegistries.CREATIVE_MODE_TAB.getKey(MillenniumCreativeTab.ENGINEERING_PARTS.get())), "千年科工 工程组件");
    }
}
