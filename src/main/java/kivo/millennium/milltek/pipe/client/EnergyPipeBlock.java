package kivo.millennium.milltek.pipe.client;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import kivo.millennium.milltek.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class EnergyPipeBlock extends AbstractPipeBL {
    public EnergyPipeBlock() {
        super(Properties.of().noOcclusion());
    }

    @Override
    public double getDefaultWidth() {
        return 0.3;
    }

    @Override
    public boolean isSamePipe(Block target) {
        return target instanceof EnergyPipeBlock;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyPipeBE(pos, state);
    }

    @Override
    public BlockEntityType<EnergyPipeBE> blockEntityType() {
        return MillenniumBlockEntities.ENERGY_PIPE_BE.get();
    }

    @Override
    protected boolean connectionTest(BlockGetter level, BlockPos pos, BlockState state, Direction facing) {
        // 与自身类型管道连接且对方不是DISCONNECTED
        if (state.getBlock() instanceof EnergyPipeBlock
                && state.getValue(getPropertyForDirection(facing.getOpposite())) != EPipeState.DISCONNECTED) {
            return true;
        }
        // 其他方块，判断是否有能量能力
        BlockEntity be = level.getBlockEntity(pos);
        return be != null && be.getCapability(ForgeCapabilities.ENERGY, facing.getOpposite()).isPresent();
    }
}
