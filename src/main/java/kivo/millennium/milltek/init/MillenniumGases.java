package kivo.millennium.milltek.init;

import kivo.millennium.milltek.gas.Gas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import static kivo.millennium.milltek.Main.MODID;
import static kivo.millennium.milltek.Main.getRL;

import java.util.function.Supplier;

public class MillenniumGases {
    public static final DeferredRegister<Gas> GASES = DeferredRegister.create(getRL("gases"), MODID);

    public static final Supplier<IForgeRegistry<Gas>> GAS_REGISTRY = GASES.makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<Gas> OXYGEN = GASES.register("oxygen", () -> new Gas("oxygen", 0xFF0000));
    public static final RegistryObject<Gas> HYDROGEN = GASES.register("hydrogen", () -> new Gas("hydrogen", 0x00FF00));
    public static final RegistryObject<Gas> NITROGEN = GASES.register("nitrogen", () -> new Gas("nitrogen", 0x0000FF));
    public static final RegistryObject<Gas> CARBON_DIOXIDE = GASES.register("carbon_dioxide",
            () -> new Gas("carbon_dioxide", 0xFFFF00));
    public static final RegistryObject<Gas> METHANE = GASES.register("methane", () -> new Gas("methane", 0xFF00FF));
    public static final RegistryObject<Gas> AMMONIA = GASES.register("ammonia", () -> new Gas("ammonia", 0x00FFFF));
    public static final RegistryObject<Gas> SULFUR_DIOXIDE = GASES.register("sulfur_dioxide",
            () -> new Gas("sulfur_dioxide", 0xFF8800));
    public static final RegistryObject<Gas> CHLORINE = GASES.register("chlorine", () -> new Gas("chlorine", 0xFF8800));
    public static final RegistryObject<Gas> CARBON_MONOXIDE = GASES.register("carbon_monoxide",
            () -> new Gas("carbon_monoxide", 0xFF8800));
    public static final RegistryObject<Gas> ETHYLENE = GASES.register("ethylene", () -> new Gas("ethylene", 0xFF8800));
    public static final RegistryObject<Gas> PROPANE = GASES.register("propane", () -> new Gas("propane", 0xFF8800));
    public static final RegistryObject<Gas> STEAM = GASES.register("steam", () -> new Gas("steam", 0xB4E1FA));

    // getGas方法建议直接返回注册表ResourceLocation
    public static ResourceLocation getGas(Gas gas) {
        return gas.getRegistryName();
    }

    public static Gas getGasById(String id) {
        return GAS_REGISTRY.get().getValue(new ResourceLocation(MODID, id));
    }

    public static void register(IEventBus bus) {
        GASES.register(bus);
    }
}
