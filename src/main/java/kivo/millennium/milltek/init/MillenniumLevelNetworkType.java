package kivo.millennium.milltek.init;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.types.Type;

import kivo.millennium.milltek.pipe.client.network.AbstractLevelNetwork;
import kivo.millennium.milltek.pipe.client.network.FluidPipeNetwork;
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

import org.antlr.v4.parse.ANTLRParser.prequelConstruct_return;

import static kivo.millennium.milltek.Main.MODID;
import static kivo.millennium.milltek.Main.getRL;

import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;

public class MillenniumLevelNetworkType {
    public static final DeferredRegister<LevelNetworkType<?>> LEVEL_NETWORK_TYPES = DeferredRegister.create(getRL("level_network_types"), MODID);

    private static final Supplier<IForgeRegistry<LevelNetworkType<?>>> L = LEVEL_NETWORK_TYPES.makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<LevelNetworkType<FluidPipeNetwork>> FLUID_PIPE_NETWORK = LEVEL_NETWORK_TYPES.register(
        "fluid_pipe_network", 
        () -> LevelNetworkType.Builder.of(FluidPipeNetwork::new).build());
    

    public static class LevelNetworkType<T extends AbstractLevelNetwork> {
        private final LevelNetworkType.LevelNetworkSupplier<T> factory;

        public LevelNetworkType(LevelNetworkSupplier<T> pFactory) {
            this.factory = pFactory;
        }

        public static String getName(LevelNetworkType<?> pType) {
            return L.get().getKey(pType).getPath();
        }

        @Nullable
        public T create(int id) {
            return this.factory.create(id);
        }

        @FunctionalInterface
        public interface LevelNetworkSupplier<T extends AbstractLevelNetwork> {
            T create(int id);
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
