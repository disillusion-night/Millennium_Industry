package kivo.millennium.milltek.block.container.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import kivo.millennium.milltek.block.container.base.AbstractContainerBL;

public class CreativeEnergyContainerBL extends AbstractContainerBL {
    public CreativeEnergyContainerBL(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    protected CreativeEnergyContainerBE createBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeEnergyContainerBE(pos, state);
    }
}
