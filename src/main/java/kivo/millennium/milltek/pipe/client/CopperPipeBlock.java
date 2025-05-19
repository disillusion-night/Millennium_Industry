package kivo.millennium.milltek.pipe.client;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import static kivo.millennium.milltek.machine.EIOState.getPropertyForDirection;

import org.jetbrains.annotations.Nullable;

import kivo.millennium.milltek.init.MillenniumBlockEntities;
import kivo.millennium.milltek.machine.EIOState;

public class CopperPipeBlock extends AbstractPipeBL {
    private static final double PIPE_WIDTH = 0.5;

    public CopperPipeBlock() {
        super(Properties.of().strength(2.0f));
    }

    @Override
    public double getDefaultWidth() {
        return PIPE_WIDTH;
    }

    @Override
    protected boolean connectionTest(BlockGetter level, BlockPos neighborPos, BlockState neighborState,
            Direction facing) {
        // 与自身类型管道连接且对方不是DISCONNECTED
        if (neighborState.getBlock() instanceof CopperPipeBlock
                && neighborState.getValue(getPropertyForDirection(facing.getOpposite())) != EIOState.DISCONNECTED) {
            return true;
        }
        // 其他方块，判断是否有流体能力
        BlockEntity be = level.getBlockEntity(neighborPos);
        return be != null && be.getCapability(ForgeCapabilities.FLUID_HANDLER, facing.getOpposite()).isPresent();
    }

    @Override
    public boolean isSamePipe(Block target) {
        return target instanceof CopperPipeBlock;
    }

    @Override
    public @Nullable CopperPipeBE newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CopperPipeBE(pPos, pState);
    }

    @Override
    public BlockEntityType<CopperPipeBE> blockEntityType() {
        return MillenniumBlockEntities.COPPER_PIPE_BE.get();
    }
}