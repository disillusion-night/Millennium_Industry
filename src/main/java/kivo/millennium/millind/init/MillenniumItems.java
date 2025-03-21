package kivo.millennium.millind.init;

import kivo.millennium.millind.item.Oopart.BAItemLevel;
import kivo.millennium.millind.item.Oopart.Oopart;
import kivo.millennium.millind.item.battery.BaseBattery;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;

import static kivo.millennium.millind.Main.MODID;
import static kivo.millennium.millind.item.Oopart.BAItemLevel.*;

public class MillenniumItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static ArrayList<RegistryObject<Oopart>> OOPARTS = new ArrayList<>();
    public static ArrayList<RegistryObject<Item>> ENGINEERING_PARTS = new ArrayList<>();
    public static ArrayList<RegistryObject<Item>> MATERIALS = new ArrayList<>();

    public static final RegistryObject<Oopart> HighPurityWolfseggSteel = createOopart("high_purity_wolfsegg_steel", SSR);
    public static final RegistryObject<Oopart> LowPurityWolfseggSteel = createOopart("low_purity_wolfsegg_steel", SR);
    public static final RegistryObject<Oopart> WolfseggIron = createOopart("wolfsegg_iron", R);
    public static final RegistryObject<Oopart> WolfseggIronOre = createOopart("wolfsegg_iron_ore", N);

    public static final RegistryObject<BaseBattery> VRLA = createBattery("vrla", 10000, 1000);

    public static final RegistryObject<Item> COPPER_DUST = createMaterial("copper_dust");
    public static final RegistryObject<Item> IRON_DUST = createMaterial("iron_dust");
    public static final RegistryObject<Item> IRON_PANEL = createMaterial("iron_panel");
    public static final RegistryObject<Item> IRON_ROD = createMaterial("iron_rod");
    public static final RegistryObject<Item> IRON_PIPE = createMaterial("iron_pipe");
    public static final RegistryObject<Item> GOLD_DUST = createMaterial("gold_dust");
    public static final RegistryObject<Item> GOLD_PANEL = createMaterial("gold_panel");

    public static final RegistryObject<Item> LEAD_DUST = createMaterial("lead_dust");
    public static final RegistryObject<Item> LEAD_INGOT = createMaterial("lead_ingot");
    public static final RegistryObject<Item> LEAD_NUGGET = createMaterial("lead_nugget");
    public static final RegistryObject<Item> LEAD_PANEL = createMaterial("lead_panel");
    public static final RegistryObject<Item> LEAD_ROD = createMaterial("lead_rod");
    public static final RegistryObject<Item> LEAD_PIPE = createMaterial("lead_pipe");
    public static final RegistryObject<Item> RAW_LEAD = createMaterial("raw_lead");


    public static final RegistryObject<Item> ALUMINIUM_DUST = createMaterial("aluminum_dust");
    public static final RegistryObject<Item> ALUMINUM_NUGGET = createMaterial("aluminum_nugget");
    public static final RegistryObject<Item> ALUMINUM_INGOT = createMaterial("aluminum_ingot");
    public static final RegistryObject<Item> ALUMINUM_ALLOY_INGOT = createMaterial("aluminum_alloy_ingot");
    public static final RegistryObject<Item> ALUMINUM_ALLOY_PANEL = createMaterial("aluminum_alloy_panel");
    public static final RegistryObject<Item> ALUMINUM_ALLOY_ROD = createMaterial("aluminum_alloy_rod");
    public static final RegistryObject<Item> ALUMINUM_ALLOY_PIPE = createMaterial("aluminum_alloy_pipe");
    public static final RegistryObject<Item> RAW_ALUMINUM = createMaterial("raw_aluminum");

    public static final RegistryObject<Item> STEEL_DUST = createMaterial("steel_dust");
    public static final RegistryObject<Item> STEEL_INGOT = createMaterial("steel_ingot");
    public static final RegistryObject<Item> STEEL_NUGGET = createMaterial("steel_nugget");
    public static final RegistryObject<Item> STEEL_PANEL = createMaterial("steel_panel");
    public static final RegistryObject<Item> STEEL_ROD = createMaterial("steel_rod");
    public static final RegistryObject<Item> STEEL_PIPE = createMaterial("steel_pipe");
    
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
