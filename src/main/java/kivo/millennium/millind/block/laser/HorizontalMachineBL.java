package kivo.millennium.millind.block.laser;

import kivo.millennium.millind.block.device.AbstractDeviceBL;
import kivo.millennium.millind.block.device.AbstractMachineBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

public abstract class HorizontalMachineBL<BE extends AbstractMachineBE> extends AbstractDeviceBL {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public HorizontalMachineBL(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public abstract BE newBlockEntity(BlockPos pPos, BlockState pState); // 强制子类实现创建 BlockEntity 的方法

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WORKING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(WORKING, false).setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }
}
