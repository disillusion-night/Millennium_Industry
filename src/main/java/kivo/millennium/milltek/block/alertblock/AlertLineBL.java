package kivo.millennium.milltek.block.alertblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AlertLineBL extends Block {

    public static final EnumProperty<LineShape> SHAPE = EnumProperty.create("shape", LineShape.class); // 方块形状属性

    private static final VoxelShape SHAPE_NORTH_SOUTH = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 2.0D, 16.0D); // 南北走向形状
    private static final VoxelShape SHAPE_EAST_WEST = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 2.0D, 10.0D); // 东西走向形状
    private static final VoxelShape SHAPE_CROSS = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D); // 十字交叉形状

    public AlertLineBL() {
        super(BlockBehaviour.Properties.of()
                .strength(0.5F)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(SHAPE, LineShape.NORTH_SOUTH)); // 默认形状为南北走向
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHAPE); // 注册形状属性
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        LineShape shape = state.getValue(SHAPE);
        switch (shape) {
            case EAST_WEST:
                return SHAPE_EAST_WEST;
            case CROSS:
                return SHAPE_CROSS;
            default:
                return SHAPE_NORTH_SOUTH;
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, Level world, BlockPos pos, BlockPos neighborPos) {
        boolean north = canConnectTo(world.getBlockState(pos.north()));
        boolean south = canConnectTo(world.getBlockState(pos.south()));
        boolean east = canConnectTo(world.getBlockState(pos.east()));
        boolean west = canConnectTo(world.getBlockState(pos.west()));

        LineShape newShape;
        if ((north || south) && (east || west)) {
            newShape = LineShape.CROSS; // 四面连接，十字交叉
        } else if (east || west) {
            newShape = LineShape.EAST_WEST; // 东西走向
        } else {
            newShape = LineShape.NORTH_SOUTH; // 默认南北走向
        }

        return state.setValue(SHAPE, newShape);
    }

    private boolean canConnectTo(BlockState state) {
        // 判断是否可以连接到指定方块
        return state.getBlock() instanceof AlertLineBL; // 仅连接到相同类型的方块
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState(); // 默认状态为南北走向
    }

    public enum LineShape {
        NORTH_SOUTH, // 南北走向
        EAST_WEST,   // 东西走向
        CROSS        // 十字交叉
    }
}
