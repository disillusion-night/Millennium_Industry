package kivo.millennium.millind.init;

import kivo.millennium.millind.pipe.client.network.AbstractLevelNetwork;
import kivo.millennium.millind.pipe.client.network.FluidLevelNetwork;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;


import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

import static kivo.millennium.millind.Main.MODID;
import static kivo.millennium.millind.Main.getRL;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MillenniumLevelNetwork {
    public static final DeferredRegister<LevelNetworkType<?>> LEVEL_NETWORK_TYPES_RE =
            DeferredRegister.create(getRL("level_network_type"), MODID);

    public static final Supplier<IForgeRegistry<LevelNetworkType<?>>> REGISTRY = LEVEL_NETWORK_TYPES_RE.makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<LevelNetworkType<FluidLevelNetwork>> FLUID_LEVEL_NETWORK = register("fluid_level_network",FluidLevelNetwork::new);

    private static <T extends AbstractLevelNetwork> RegistryObject<LevelNetworkType<T>> register(String name, LevelNetworkType.LevelNetworkFactory<T> pFactory){
        return LEVEL_NETWORK_TYPES_RE.register(name,()  -> LevelNetworkType.Builder.of(pFactory, name).build());
    }

    public static Collection<RegistryObject<LevelNetworkType<?>>> getRegistryObjects() {
        if (LEVEL_NETWORK_TYPES_RE == null) {
            // TODO: 处理注册表未加载的情况，这不应该发生，或者延迟加载
            System.err.println("Network Type Registry not available during load!");
            return new ArrayList<>(); // 返回空数据，避免崩溃
        }
        return LEVEL_NETWORK_TYPES_RE.getEntries();
    }
    public static class LevelNetworkType<T extends AbstractLevelNetwork> {

        private final LevelNetworkFactory factory; // 创建 AbstractLevelNetwork 实例的工厂
        private final String name;
        /**
         * 创建一个 LevelNetworkType 实例。
         *
         * @param factory        用于创建该网络类型实例的工厂。
         */
        public LevelNetworkType(LevelNetworkFactory factory, String name) {
            this.factory = factory;
            this.name = name;
        }



        /**
         * 使用该类型创建 AbstractLevelNetwork 实例。
         *
         * @return 创建的 AbstractLevelNetwork 实例。
         */
        public T create() {
            // 工厂方法需要知道如何根据 CapabilityType 创建具体实例
            // 可以在 LevelNetworkType 的工厂中传入 CapabilityType
            // 或者要求具体的 LevelNetworkFactory 在创建时使用它
            // 这里假设 LevelNetworkFactory 的 create 方法已经处理了 CapabilityType
            AbstractLevelNetwork network = factory.create();
            if (network instanceof AbstractLevelNetwork) {
                return (T) network;
            }
            // TODO: 处理工厂创建了错误类型实例的情况，例如抛出异常或日志记录
            throw new IllegalStateException("Level network factory for type " + this.getClass().getName() + " did not create an AbstractLevelNetwork instance!");
        }

        public String getName() {
            return name;
        }


        public static final class Builder<T extends AbstractLevelNetwork> {
            private final LevelNetworkType.LevelNetworkFactory<? extends T> factory;
            private final String name;

            private Builder(LevelNetworkType.LevelNetworkFactory<? extends T> pFactory, String name) {
                this.factory = pFactory;
                this.name = name;
            }

            public static <T extends AbstractLevelNetwork> LevelNetworkType.Builder<T> of(LevelNetworkType.LevelNetworkFactory<? extends T> pFactory, String name) {
                return new LevelNetworkType.Builder<>(pFactory, name);
            }

            public LevelNetworkType<T> build() {
                return new LevelNetworkType<>(this.factory, name);
            }
        }

        @FunctionalInterface
        public interface LevelNetworkFactory<T extends AbstractLevelNetwork> {
            T create();
        }
    }
}