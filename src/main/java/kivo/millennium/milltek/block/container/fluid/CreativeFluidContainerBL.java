package kivo.millennium.milltek.block.container.fluid;

import kivo.millennium.milltek.block.container.base.AbstractContainerBL;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CreativeFluidContainerBL extends AbstractContainerBL {
    public CreativeFluidContainerBL(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    protected CreativeFluidContainerBE createBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeFluidContainerBE(pos, state);
    }
}
