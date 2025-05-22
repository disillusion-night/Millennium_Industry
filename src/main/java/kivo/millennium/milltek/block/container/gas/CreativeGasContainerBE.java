package kivo.millennium.milltek.block.container.gas;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeGasContainerBE extends GasContainerBE {
    public CreativeGasContainerBE(BlockPos pos, BlockState state) {
        super(kivo.millennium.milltek.init.MillenniumBlockEntities.CREATIVE_GAS_CONTAINER_BE.get(), pos, state);
    }

    public static int getMaxGasAmount() {
        return Integer.MAX_VALUE;
    }

    public static int getMaxInputRate() {
        return Integer.MAX_VALUE;
    }

    public static int getMaxOutputRate() {
        return Integer.MAX_VALUE;
    }
}
