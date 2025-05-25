package kivo.millennium.milltek.block.container.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeFluidContainerBE extends FluidContainerBE {
    public CreativeFluidContainerBE(BlockPos pos, BlockState state) {
        super(kivo.millennium.milltek.init.MillenniumBlockEntities.CREATIVE_FLUID_CONTAINER_BE.get(), pos, state);
    }

    public static int getMaxFluidAmount() {
        return Integer.MAX_VALUE;
    }

    public static int getMaxInputRate() {
        return Integer.MAX_VALUE;
    }

    public static int getMaxOutputRate() {
        return Integer.MAX_VALUE;
    }
}
