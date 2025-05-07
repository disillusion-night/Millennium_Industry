package kivo.millennium.milltek.init;

import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static kivo.millennium.milltek.Main.MODID;
import static kivo.millennium.milltek.item.Oopart.BAItemLevel.*;

import java.util.ArrayList;
import java.util.function.Supplier;

import kivo.millennium.milltek.item.Wrench;
import kivo.millennium.milltek.item.Oopart.BAItemLevel;
import kivo.millennium.milltek.item.Oopart.Oopart;
import kivo.millennium.milltek.item.battery.BaseBattery;

public class MillenniumItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static ArrayList<RegistryObject<Oopart>> OOPARTS = new ArrayList<>();
    public static ArrayList<RegistryObject<Item>> ENGINEERING_PARTS = new ArrayList<>();
    public static ArrayList<RegistryObject<Item>> MATERIALS = new ArrayList<>();
    public static ArrayList<RegistryObject<Item>> TOOLS = new ArrayList<>();


    public static final RegistryObject<Oopart> HighPurityWolfseggSteel = createOopart("high_purity_wolfsegg_steel", SSR);
    public static final RegistryObject<Oopart> LowPurityWolfseggSteel = createOopart("low_purity_wolfsegg_steel", SR);
    public static final RegistryObject<Oopart> WolfseggIron = createOopart("wolfsegg_iron", R);
    public static final RegistryObject<Oopart> WolfseggIronOre = createOopart("wolfsegg_iron_ore", N);

    public static final RegistryObject<BaseBattery> VRLA = createBattery("vrla", 10000, 1000);

    public static final RegistryObject<Item> STONE_DUST = createMaterial("stone_dust");

    public static final RegistryObject<Item> WRENCH = createTool("wrench", Wrench::new);

    public static final RegistryObject<Item> CARBON_DUST = createMaterial("carbon_dust");

    public static final RegistryObject<Item> COPPER_DUST = createMaterial("copper_dust");

    public static final RegistryObject<Item> IRON_DUST = createMaterial("iron_dust");
    public static final RegistryObject<Item> IRON_PANEL = createMaterial("iron_panel");
    public static final RegistryObject<Item> IRON_ROD = createMaterial("iron_rod");
    public static final RegistryObject<Item> IRON_PIPE = createMaterial("iron_pipe");
    public static final RegistryObject<Item> IRON_GEAR = createMaterial("iron_gear");

    public static final RegistryObject<Item> GOLD_DUST = createMaterial("gold_dust");
    public static final RegistryObject<Item> GOLD_PANEL = createMaterial("gold_panel");

    public static final RegistryObject<Item> LEAD_DUST = createMaterial("lead_dust");
    public static final RegistryObject<Item> LEAD_INGOT = createMaterial("lead_ingot");
    public static final RegistryObject<Item> LEAD_NUGGET = createMaterial("lead_nugget");
    public static final RegistryObject<Item> LEAD_PANEL = createMaterial("lead_panel");
    public static final RegistryObject<Item> LEAD_ROD = createMaterial("lead_rod");
    public static final RegistryObject<Item> LEAD_PIPE = createMaterial("lead_pipe");
    public static final RegistryObject<Item> RAW_LEAD = createMaterial("raw_lead");

    public static final RegistryObject<Item> ALUMINUM_ALLOY_INGOT = createMaterial("aluminum_alloy_ingot");
    public static final RegistryObject<Item> ALUMINUM_ALLOY_PANEL = createMaterial("aluminum_alloy_panel");
    public static final RegistryObject<Item> ALUMINUM_ALLOY_ROD = createMaterial("aluminum_alloy_rod");
    public static final RegistryObject<Item> ALUMINUM_ALLOY_PIPE = createMaterial("aluminum_alloy_pipe");
    public static final RegistryObject<Item> ALUMINUM_ALLOY_DUST = createMaterial("aluminum_alloy_dust");
    public static final RegistryObject<Item> ALUMINUM_ALLOY_NUGGET = createMaterial("aluminum_alloy_nugget");
    
    public static final RegistryObject<Item> TITANIUM_ALLOY_INGOT = createMaterial("titanium_alloy_ingot");
    public static final RegistryObject<Item> TITANIUM_ALLOY_PANEL = createMaterial("titanium_alloy_panel");
    public static final RegistryObject<Item> TITANIUM_ALLOY_ROD = createMaterial("titanium_alloy_rod");
    public static final RegistryObject<Item> TITANIUM_ALLOY_PIPE = createMaterial("titanium_alloy_pipe");
    public static final RegistryObject<Item> TITANIUM_ALLOY_DUST = createMaterial("titanium_alloy_dust");
    public static final RegistryObject<Item> TITANIUM_ALLOY_NUGGET = createMaterial("titanium_alloy_nugget");
    public static final RegistryObject<Item> TITANIUM_ALLOY_GEAR = createMaterial("titanium_alloy_gear");

    public static final RegistryObject<Item> TITANIUM_ALLOY_SWORD = createTool("titanium_alloy_sword", () -> new SwordItem(Tiers.DIAMOND, 3, -2.4F, (new Item.Properties()).fireResistant()));
    public static final RegistryObject<Item> TITANIUM_ALLOY_SHOVEL = createTool("titanium_alloy_shovel", () -> new ShovelItem(Tiers.DIAMOND, 1.5F, -3.0F, (new Item.Properties()).fireResistant()));
    public static final RegistryObject<Item> TITANIUM_ALLOY_PICKAXE = createTool("titanium_alloy_pickaxe",() -> new PickaxeItem(Tiers.DIAMOND, 1, -2.8F, (new Item.Properties()).fireResistant()));
    public static final RegistryObject<Item> TITANIUM_ALLOY_AXE = createTool("titanium_alloy_axe",() -> new AxeItem(Tiers.DIAMOND, 5.0F, -3.0F, (new Item.Properties()).fireResistant()));
    public static final RegistryObject<Item> TITANIUM_ALLOY_HOE = createTool("titanium_alloy_hoe",() -> new HoeItem(Tiers.DIAMOND, -4, 0.0F, (new Item.Properties()).fireResistant()));

    public static final RegistryObject<Item> ALUMINIUM_DUST = createMaterial("aluminum_dust");
    public static final RegistryObject<Item> ALUMINUM_NUGGET = createMaterial("aluminum_nugget");
    public static final RegistryObject<Item> ALUMINUM_INGOT = createMaterial("aluminum_ingot");
    public static final RegistryObject<Item> RAW_ALUMINUM = createMaterial("raw_aluminum");
    public static final RegistryObject<Item> RAW_ALUMINUM_DUST = createMaterial("raw_aluminum_dust");

    public static final RegistryObject<Item> CRYOLITE = createMaterial("cryolite");
    public static final RegistryObject<Item> CRYOLITE_DUST = createMaterial("cryolite_dust");

    public static final RegistryObject<Item> STEEL_DUST = createMaterial("steel_dust");
    public static final RegistryObject<Item> STEEL_INGOT = createMaterial("steel_ingot");
    public static final RegistryObject<Item> STEEL_NUGGET = createMaterial("steel_nugget");
    public static final RegistryObject<Item> STEEL_PANEL = createMaterial("steel_panel");
    public static final RegistryObject<Item> STEEL_ROD = createMaterial("steel_rod");
    public static final RegistryObject<Item> STEEL_PIPE = createMaterial("steel_pipe");
    public static final RegistryObject<Item> STEEL_GEAR = createMaterial("steel_gear");
    //public static final RegistryObject<Item> STEEL_FM = createMaterial("steel_bm");

    public static final RegistryObject<Item> STEEL_SWORD = createTool("steel_sword", () -> new SwordItem(Tiers.DIAMOND, 3, -2.4F, (new Item.Properties())));
    public static final RegistryObject<Item> STEEL_SHOVEL = createTool("steel_shovel", () -> new ShovelItem(Tiers.DIAMOND, 1.5F, -3.0F, (new Item.Properties())));
    public static final RegistryObject<Item> STEEL_PICKAXE = createTool("steel_pickaxe",() -> new PickaxeItem(Tiers.DIAMOND, 1, -2.8F, (new Item.Properties())));
    public static final RegistryObject<Item> STEEL_AXE = createTool("steel_axe",() -> new AxeItem(Tiers.DIAMOND, 5.0F, -3.0F, (new Item.Properties())));
    public static final RegistryObject<Item> STEEL_HOE = createTool("steel_hoe",() -> new HoeItem(Tiers.DIAMOND, -4, 0.0F, (new Item.Properties())));

    public static final RegistryObject<Item> WOLFRAM_STEEL_DUST = createMaterial("wolfram_steel_dust");
    public static final RegistryObject<Item> WOLFRAM_STEEL_INGOT = createMaterial("wolfram_steel_ingot");
    public static final RegistryObject<Item> WOLFRAM_STEEL_NUGGET = createMaterial("wolfram_steel_nugget");
    public static final RegistryObject<Item> WOLFRAM_STEEL_PANEL = createMaterial("wolfram_steel_panel");
    public static final RegistryObject<Item> WOLFRAM_STEEL_ROD = createMaterial("wolfram_steel_rod");
    public static final RegistryObject<Item> WOLFRAM_STEEL_PIPE = createMaterial("wolfram_steel_pipe");
    public static final RegistryObject<Item> WOLFRAM_STEEL_GEAR = createMaterial("wolfram_steel_gear");

    public static final RegistryObject<Item> WOLFRAM_STEEL_SWORD = createTool("wolfram_steel_sword", () -> new SwordItem(Tiers.NETHERITE, 3, -2.4F, (new Item.Properties()).fireResistant()));
    public static final RegistryObject<Item> WOLFRAM_STEEL_SHOVEL = createTool("wolfram_steel_shovel", () -> new ShovelItem(Tiers.NETHERITE, 1.5F, -3.0F, (new Item.Properties()).fireResistant()));
    public static final RegistryObject<Item> WOLFRAM_STEEL_PICKAXE = createTool("wolfram_steel_pickaxe",() -> new PickaxeItem(Tiers.NETHERITE, 1, -2.8F, (new Item.Properties()).fireResistant()));
    public static final RegistryObject<Item> WOLFRAM_STEEL_AXE = createTool("wolfram_steel_axe",() -> new AxeItem(Tiers.NETHERITE, 5.0F, -3.0F, (new Item.Properties()).fireResistant()));
    public static final RegistryObject<Item> WOLFRAM_STEEL_HOE = createTool("wolfram_steel_hoe",() -> new HoeItem(Tiers.NETHERITE, -4, 0.0F, (new Item.Properties()).fireResistant()));

    //public static final RegistryObject<Item> CABLE_BLOCK_ITEM = ITEMS.register("cable", () -> new BlockItem(CABLE_BLOCK.get(), new Item.Properties()));

   // public static final RegistryObject<Item> FACADE_BLOCK_ITEM = ITEMS.register("facade", () -> new FacadeBlockItem(FACADE_BLOCK.get(), new Item.Properties()));

    private static <T extends Item> RegistryObject<Item> createTool(String name, Supplier<T> supplier){
        RegistryObject<Item> tool = ITEMS.register(name, supplier);
        TOOLS.add(tool);
        return tool;
    }

    private static RegistryObject<Item> createMaterial(String name){
        RegistryObject<Item>material = ITEMS.register(name, () -> new Item(new Item.Properties()));
        MATERIALS.add(material);
        return material;
    }

    private static RegistryObject<Item> createMaterial(String name, Item.Properties properties){
        RegistryObject<Item>material = ITEMS.register(name, () -> new Item(properties));
        MATERIALS.add(material);
        return material;
    }


    private static RegistryObject<Oopart> createOopart(String name, BAItemLevel level){
         RegistryObject<Oopart> oopart = ITEMS.register(name, () -> new Oopart(new Item.Properties(), level));
         OOPARTS.add(oopart);
         return oopart;
    }


    private static RegistryObject<BaseBattery> createBattery(String name, int Capacity, int MaxTransfer){
        RegistryObject<BaseBattery> battery = ITEMS.register(name, () -> new BaseBattery(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1), Capacity, MaxTransfer));
        ENGINEERING_PARTS.add(RegistryObject.create(battery.getId(), ForgeRegistries.ITEMS));
        return battery;
    }
}
