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

    public static final RegistryObject<Oopart> HighPurityWolfseggSteel = createOopart("high_purity_wolfsegg_steel", SSR);
    public static final RegistryObject<Oopart> LowPurityWolfseggSteel = createOopart("low_purity_wolfsegg_steel", SR);
    public static final RegistryObject<Oopart> WolfseggIron = createOopart("wolfsegg_iron", R);
    public static final RegistryObject<Oopart> WolfseggIronOre = createOopart("wolfsegg_iron_ore", N);

    public static final RegistryObject<BaseBattery> VRLA = createBattery("vrla", 10000, 1000);

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
