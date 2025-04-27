package kivo.millennium.millind.init;

import kivo.millennium.millind.init.LevelNetworkType;
import kivo.millennium.millind.pipe.client.network.FluidLevelNetwork;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;


import java.util.function.Supplier;

import static kivo.millennium.millind.Main.MODID;
import static kivo.millennium.millind.Main.getRL;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NetworkInit {
    public static final DeferredRegister<LevelNetworkType<?>> LEVEL_NETWORK_TYPES_RE =
            DeferredRegister.create(getRL("level_network_type"), MODID);

    public static final Supplier<IForgeRegistry<LevelNetworkType<?>>> REGISTRY = LEVEL_NETWORK_TYPES_RE.makeRegistry(RegistryBuilder::new);

    // 自定义注册表的 RegistryKey
    public static final RegistryObject<LevelNetworkType<FluidLevelNetwork>> FLUID_LEVEL_NETWORK = LEVEL_NETWORK_TYPES_RE.register("fluid_level_network",()  -> LevelNetworkType.Builder.of(FluidLevelNetwork::new).build());


}