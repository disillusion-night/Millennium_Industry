package kivo.millennium.milltek.pipe.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.ScheduledTick;

import static kivo.millennium.milltek.machine.EIOState.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import kivo.millennium.milltek.machine.EIOState;

public abstract class AbstractPipeBL extends Block implements SimpleWaterloggedBlock, EntityBlock {

    // 连接状态属性
    public static final EnumProperty<EIOState> NORTH = EnumProperty.create("north", EIOState.class);
    public static final EnumProperty<EIOState> EAST = EnumProperty.create("east", EIOState.class);
    public static final EnumProperty<EIOState> SOUTH = EnumProperty.create("south", EIOState.class);
    public static final EnumProperty<EIOState> WEST = EnumProperty.create("west", EIOState.class);
    public static final EnumProperty<EIOState> UP = EnumProperty.create("up", EIOState.class);
    public static final EnumProperty<EIOState> DOWN = EnumProperty.create("down", EIOState.class);

    // 含水状态属性
    public static final BooleanProperty WATERLOGGED = BooleanProperty.create("waterlogged");

    public AbstractPipeBL(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, EIOState.NONE)
                .setValue(EAST, EIOState.NONE)
                .setValue(SOUTH, EIOState.NONE)
                .setValue(WEST, EIOState.NONE)
                .setValue(UP, EIOState.NONE)
                .setValue(DOWN, EIOState.NONE)
                .setValue(WATERLOGGED, false));
    }

    public abstract double getDefaultWidth();

    public boolean canConnectTo(BlockGetter level, BlockPos neighborPos, BlockState neighborState, Direction facing) {
        if (neighborState.isAir())
            return false;
        if (neighborState.getBlock() instanceof AbstractPipeBL) {
            if (neighborState.hasProperty(getPropertyForDirection(facing.getOpposite())) && neighborState
                    .getValue(getPropertyForDirection(facing.getOpposite())).equals(EIOState.DISCONNECTED))
                return false;
            else
                return true;
        } else {
            return connectionTest(level, neighborPos, neighborState, facing);
        }
    }

    protected abstract boolean connectionTest(BlockGetter level, BlockPos pos, BlockState state, Direction facing);

    protected boolean isPipe(BlockState state, Block block) {
        if (block instanceof AbstractPipeBL) {
            return true;
        }
        return false;
    }

    protected EIOState getPipeStateForNeighbor(BlockGetter level, BlockPos pos, Direction facing) {

        BlockState currentState = level.getBlockState(pos);

        if (currentState.hasProperty(getPropertyForDirection(facing))
                && currentState.getValue(getPropertyForDirection(facing)) == DISCONNECTED) {
            return EIOState.DISCONNECTED;
        }

        BlockPos neighborPos = pos.relative(facing);
        BlockState neighborState = level.getBlockState(neighborPos);

        if (canConnectTo(level, neighborPos, neighborState, facing)) {

            if (currentState.hasProperty(getPropertyForDirection(facing))) {
                switch (currentState.getValue(getPropertyForDirection(facing))) {
                    case PULL, PUSH: {
                        return isPipe(neighborState, neighborState.getBlock()) ? CONNECT
                                : currentState.getValue(getPropertyForDirection(facing));
                    }
                }
            }
            return EIOState.CONNECT;
        }
        // TODO: 添加判断 INSERT 和 OUTPUT 状态的逻辑
        return EIOState.NONE;
    }

    @Override
    public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state,
            @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        BlockState blockState = calculateState(level, pos, state);
        // 只在BlockState实际变化时setBlockAndUpdate，避免死循环
        if (!state.equals(blockState)) {
            level.setBlockAndUpdate(pos, blockState);
        }
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED);
    }

    @Nonnull
    @Override
    public BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction direction,
            @Nonnull BlockState neighbourState,
            @Nonnull LevelAccessor world, @Nonnull BlockPos current, @Nonnull BlockPos offset) {
        if (state.getValue(WATERLOGGED)) {
            world.getFluidTicks()
                    .schedule(new ScheduledTick<>(Fluids.WATER, current, Fluids.WATER.getTickDelay(world), 0L));
        }
        BlockState newState = calculateStateWhenUpdate(world, current, state);
        // 只在BlockState实际变化时返回新state，否则返回原state
        return !state.equals(newState) ? newState : state;
    }

    @Nonnull
    public BlockState calculateStateWhenUpdate(@Nonnull LevelAccessor level, @Nonnull BlockPos pos,
            @Nonnull BlockState state) {
        BlockState newState = state
                .setValue(NORTH, getPipeStateForNeighbor(level, pos, Direction.NORTH))
                .setValue(EAST, getPipeStateForNeighbor(level, pos, Direction.EAST))
                .setValue(SOUTH, getPipeStateForNeighbor(level, pos, Direction.SOUTH))
                .setValue(WEST, getPipeStateForNeighbor(level, pos, Direction.WEST))
                .setValue(UP, getPipeStateForNeighbor(level, pos, Direction.UP))
                .setValue(DOWN, getPipeStateForNeighbor(level, pos, Direction.DOWN));
        return newState;
    }

    @Nonnull
    public BlockState calculateState(@Nonnull LevelAccessor level, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        BlockState newState = state
                .setValue(NORTH, getPipeStateForNeighbor(level, pos, Direction.NORTH))
                .setValue(EAST, getPipeStateForNeighbor(level, pos, Direction.EAST))
                .setValue(SOUTH, getPipeStateForNeighbor(level, pos, Direction.SOUTH))
                .setValue(WEST, getPipeStateForNeighbor(level, pos, Direction.WEST))
                .setValue(UP, getPipeStateForNeighbor(level, pos, Direction.UP))
                .setValue(DOWN, getPipeStateForNeighbor(level, pos, Direction.DOWN));
        return newState;
    }

    @Override
    @Nonnull
    public FluidState getFluidState(@Nonnull BlockState state) {
        // noinspection deprecation
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    @Deprecated
    public void neighborChanged(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos,
            @Nonnull Block block, @Nonnull BlockPos fromPos,
            boolean isMoving) {
        if (!level.isClientSide) {
            Direction facing = Direction
                    .getNearest(fromPos.getX() - pos.getX(), fromPos.getY() - pos.getY(), fromPos.getZ() - pos.getZ())
                    .getOpposite();

            if (facing != null) {
                EnumProperty<EIOState> property = getPropertyForDirection(facing);
                if (state.getValue(property) != EIOState.DISCONNECTED) {
                    BlockState newState = state.setValue(property, getPipeStateForNeighbor(level, pos, facing));
                    if (newState.getValue(WATERLOGGED)) {
                        level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
                    }
                    if (!newState.equals(state)) {
                        level.setBlock(pos, newState, 2); // 只在变化时setBlock，避免死循环
                    }
                }
            } else {
                BlockState newState = state
                        .setValue(NORTH, getPipeStateForNeighbor(level, pos, Direction.NORTH))
                        .setValue(EAST, getPipeStateForNeighbor(level, pos, Direction.EAST))
                        .setValue(SOUTH, getPipeStateForNeighbor(level, pos, Direction.SOUTH))
                        .setValue(WEST, getPipeStateForNeighbor(level, pos, Direction.WEST))
                        .setValue(UP, getPipeStateForNeighbor(level, pos, Direction.UP))
                        .setValue(DOWN, getPipeStateForNeighbor(level, pos, Direction.DOWN));
                if (newState.getValue(WATERLOGGED)) {
                    level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
                }
                if (!newState.equals(state)) {
                    level.setBlock(pos, newState, 2); // 只在变化时setBlock，避免死循环
                }
            }
        }
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
    }

    public abstract boolean isSamePipe(Block target);
    // TODO: 添加设置和获取特定面连接状态的方法 (如果需要)

    @Override
    @Nonnull
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos,
            @Nonnull CollisionContext context) {
        float width = (float) getDefaultWidth();
        float center = 0.5F;
        float radius = width / 2.0F;

        VoxelShape baseShape = Shapes.box(center - radius, center - radius, center - radius, center + radius,
                center + radius, center + radius);
        VoxelShape finalShape = baseShape;

        if (state.getValue(NORTH) != EIOState.NONE && state.getValue(NORTH) != EIOState.DISCONNECTED) {
            finalShape = Shapes.or(finalShape, Shapes.box(center - radius, center - radius, 0.0D, center + radius,
                    center + radius, center - radius));
        }
        if (state.getValue(EAST) != EIOState.NONE && state.getValue(EAST) != EIOState.DISCONNECTED) {
            finalShape = Shapes.or(finalShape, Shapes.box(center + radius, center - radius, center - radius, 1.0D,
                    center + radius, center + radius));
        }
        if (state.getValue(SOUTH) != EIOState.NONE && state.getValue(SOUTH) != EIOState.DISCONNECTED) {
            finalShape = Shapes.or(finalShape, Shapes.box(center - radius, center - radius, center + radius,
                    center + radius, center + radius, 1.0D));
        }
        if (state.getValue(WEST) != EIOState.NONE && state.getValue(WEST) != EIOState.DISCONNECTED) {
            finalShape = Shapes.or(finalShape, Shapes.box(0.0D, center - radius, center - radius, center - radius,
                    center + radius, center + radius));
        }
        if (state.getValue(UP) != EIOState.NONE && state.getValue(UP) != EIOState.DISCONNECTED) {
            finalShape = Shapes.or(finalShape, Shapes.box(center - radius, center + radius, center - radius,
                    center + radius, 1.0D, center + radius));
        }
        if (state.getValue(DOWN) != EIOState.NONE && state.getValue(DOWN) != EIOState.DISCONNECTED) {
            finalShape = Shapes.or(finalShape, Shapes.box(center - radius, 0.0D, center - radius, center + radius,
                    center - radius, center + radius));
        }

        return finalShape;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pPos, @Nonnull BlockState pState) {
        // 子类应重写此方法，返回对应的PipeBE实例
        throw new UnsupportedOperationException("Must be implemented by subclass");
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level pLevel, @Nonnull BlockState pState,
            @Nonnull BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide() ? null
                : this.createTickerHelper(pBlockEntityType, blockEntityType(), PipeBE::tick); // 服务端每 tick 调用
                                                                                              // BlockEntity 的 tick 方法
    }

    @SuppressWarnings("unchecked")
    protected <T extends BlockEntity, A extends PipeBE<?>> BlockEntityTicker<T> createTickerHelper(
            @Nonnull BlockEntityType<T> pServerType, @Nonnull BlockEntityType<A> pExpectedType,
            @Nullable BlockEntityTicker<? super A> pTicker) {
        return pExpectedType == pServerType ? (BlockEntityTicker<T>) pTicker : null;
    }

    @Nonnull
    public abstract BlockEntityType<? extends PipeBE<?>> blockEntityType(); // 强制子类提供其 BlockEntityType

}