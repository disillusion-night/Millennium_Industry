package kivo.millennium.millind.init;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.types.Type;
import kivo.millennium.millind.pipe.client.network.AbstractLevelNetwork;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.*;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Supplier;

import static kivo.millennium.millind.Main.MODID;
import static kivo.millennium.millind.Main.getRL;

public class MillenniumLevelNetworkType {
    public static final DeferredRegister<LevelNetworkType<?>> LEVEL_NETWORK_TYPES = DeferredRegister.create(getRL("level_network_types"), MODID);

    private static final Supplier<IForgeRegistry<LevelNetworkType<?>>> L = LEVEL_NETWORK_TYPES.makeRegistry(RegistryBuilder::new);

    public static class LevelNetworkType<T extends AbstractLevelNetwork> {
        private final LevelNetworkType.LevelNetworkSupplier<T> factory;

        public LevelNetworkType(LevelNetworkSupplier<T> pFactory) {
            this.factory = pFactory;
        }

        @Nullable
        public T create() {
            return this.factory.create();
        }

        @FunctionalInterface
        public interface LevelNetworkSupplier<T extends AbstractLevelNetwork> {
            T create();
        }

        public static final class Builder<T extends AbstractLevelNetwork> {
            private final LevelNetworkSupplier<T> factory;

            private Builder(LevelNetworkSupplier<T> pFactory) {
                this.factory = pFactory;
            }

            public static <T extends AbstractLevelNetwork> LevelNetworkType.Builder<T> of(LevelNetworkSupplier<T> pFactory) {
                return new LevelNetworkType.Builder<>(pFactory);
            }

            public LevelNetworkType<T> build() {
                return new LevelNetworkType<>(this.factory);
            }
        }
    }
}
