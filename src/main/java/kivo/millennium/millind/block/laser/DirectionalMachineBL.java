package kivo.millennium.millind.block.laser;

import kivo.millennium.millind.block.device.AbstractDeviceBL;
import kivo.millennium.millind.block.device.AbstractMachineBE;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

import java.util.function.ToIntFunction;

public abstract class DirectionalMachineBL<BE extends AbstractMachineBE> extends AbstractDeviceBL {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;


    public DirectionalMachineBL(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WORKING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(WORKING, false).setValue(FACING, pContext.getNearestLookingDirection().getOpposite());
    }
}
