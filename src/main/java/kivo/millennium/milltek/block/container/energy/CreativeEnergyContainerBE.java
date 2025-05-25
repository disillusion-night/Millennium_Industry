package kivo.millennium.milltek.block.container.energy;

import kivo.millennium.milltek.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

public class CreativeEnergyContainerBE extends EnergyContainerBE {
    public CreativeEnergyContainerBE(BlockPos pos, BlockState state) {
        super(MillenniumBlockEntities.CREATIVE_ENERGY_CONTAINER_BE.get(), pos, state);
        this.energyHandler = LazyOptional.of(this::createEnergyStorage);
    }

    public static int getMaxEnergyStorage() {
        return Integer.MAX_VALUE;
    }

    public static int getMaxInputRate() {
        return Integer.MAX_VALUE;
    }

    public static int getMaxOutputRate() {
        return Integer.MAX_VALUE;
    }
}
