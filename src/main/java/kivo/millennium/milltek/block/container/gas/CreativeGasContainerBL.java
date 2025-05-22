package kivo.millennium.milltek.block.container.gas;

import kivo.millennium.milltek.block.container.base.AbstractContainerBL;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CreativeGasContainerBL extends AbstractContainerBL {
    public CreativeGasContainerBL(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    protected CreativeGasContainerBE createBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeGasContainerBE(pos, state);
    }
}
