package kivo.millennium.milltek.init;

import net.minecraftforge.registries.*;

import javax.annotation.Nullable;

import kivo.millennium.milltek.pipe.network.AbstractLevelNetwork;
import kivo.millennium.milltek.pipe.network.FluidPipeNetwork;
import net.minecraft.nbt.CompoundTag;
import java.util.UUID;

import static kivo.millennium.milltek.Main.MODID;
import static kivo.millennium.milltek.Main.getRL;

import java.util.function.Supplier;

public class MillenniumLevelNetworkType {
    public static final DeferredRegister<LevelNetworkType<?>> LEVEL_NETWORK_TYPES = DeferredRegister
            .create(getRL("level_network_types"), MODID);

    private static final Supplier<IForgeRegistry<LevelNetworkType<?>>> L = LEVEL_NETWORK_TYPES
            .makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<LevelNetworkType<FluidPipeNetwork>> FLUID_PIPE_NETWORK = LEVEL_NETWORK_TYPES
            .register(
                    "fluid_pipe_network",
                    () -> LevelNetworkType.Builder.of(FluidPipeNetwork::new, FluidPipeNetwork::new).build());

    // 能量网络注册
    public static final RegistryObject<LevelNetworkType<kivo.millennium.milltek.pipe.network.EnergyPipeNetwork>> ENERGY_PIPE_NETWORK = LEVEL_NETWORK_TYPES
            .register(
                    "energy_pipe_network",
                    () -> LevelNetworkType.Builder.of(
                            kivo.millennium.milltek.pipe.network.EnergyPipeNetwork::new,
                            kivo.millennium.milltek.pipe.network.EnergyPipeNetwork::new).build());

    public static class LevelNetworkType<T extends AbstractLevelNetwork> {
        private final LevelNetworkSupplier<T> factory;
        private final LevelNetworkNBTFactory<T> nbtFactory;

        public LevelNetworkType(LevelNetworkSupplier<T> factory, LevelNetworkNBTFactory<T> nbtFactory) {
            this.factory = factory;
            this.nbtFactory = nbtFactory;
        }

        public static String getName(LevelNetworkType<?> pType) {
            return L.get().getKey(pType).getPath();
        }

        @Nullable
        public T create(UUID uuid) {
            return this.factory.create(uuid);
        }

        @Nullable
        public T create(CompoundTag tag) {
            return this.nbtFactory.create(tag);
        }

        @FunctionalInterface
        public interface LevelNetworkSupplier<T extends AbstractLevelNetwork> {
            T create(UUID uuid);
        }

        @FunctionalInterface
        public interface LevelNetworkNBTFactory<T extends AbstractLevelNetwork> {
            T create(CompoundTag tag);
        }

        public static final class Builder<T extends AbstractLevelNetwork> {
            private LevelNetworkSupplier<T> factory;
            private LevelNetworkNBTFactory<T> nbtFactory;

            private Builder(LevelNetworkSupplier<T> factory, LevelNetworkNBTFactory<T> nbtFactory) {
                this.factory = factory;
                this.nbtFactory = nbtFactory;
            }

            public static <T extends AbstractLevelNetwork> Builder<T> of(LevelNetworkSupplier<T> factory,
                    LevelNetworkNBTFactory<T> nbtFactory) {
                return new Builder<>(factory, nbtFactory);
            }

            public LevelNetworkType<T> build() {
                return new LevelNetworkType<>(this.factory, this.nbtFactory);
            }
        }
    }
}
