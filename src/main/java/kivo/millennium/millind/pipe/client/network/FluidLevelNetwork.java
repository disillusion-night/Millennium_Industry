package kivo.millennium.millind.pipe.client.network;

import kivo.millennium.millind.init.MillenniumLevelNetwork;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidLevelNetwork extends AbstractLevelNetwork {

    public FluidLevelNetwork() {
        super(MillenniumLevelNetwork.FLUID_LEVEL_NETWORK.get());
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return null;
    }


    @Override
    public void handleTick(ServerLevel level) {

    }

    @Override
    protected void saveNetworkData(CompoundTag tag) {

    }

    @Override
    protected void loadNetworkData(CompoundTag tag) {

    }
}
