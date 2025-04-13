package kivo.millennium.millind.pipe.client;

import kivo.millennium.millind.Main;
import kivo.millennium.millind.init.MillenniumItems;
import kivo.millennium.millind.item.Wrench;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.ScheduledTick;
import org.checkerframework.checker.units.qual.C;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static kivo.millennium.millind.pipe.client.EPipeState.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public abstract class AbstractPipeBL extends Block implements SimpleWaterloggedBlock {

    // 连接状态属性
    public static final EnumProperty<EPipeState> NORTH = EnumProperty.create("north", EPipeState.class);
    public static final EnumProperty<EPipeState> EAST = EnumProperty.create("east", EPipeState.class);
    public static final EnumProperty<EPipeState> SOUTH = EnumProperty.create("south", EPipeState.class);
    public static final EnumProperty<EPipeState> WEST = EnumProperty.create("west", EPipeState.class);
    public static final EnumProperty<EPipeState> UP = EnumProperty.create("up", EPipeState.class);
    public static final EnumProperty<EPipeState> DOWN = EnumProperty.create("down", EPipeState.class);

    // 含水状态属性
    public static final BooleanProperty WATERLOGGED = BooleanProperty.create("waterlogged");

    public AbstractPipeBL(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, EPipeState.NONE)
                .setValue(EAST, EPipeState.NONE)
                .setValue(SOUTH, EPipeState.NONE)
                .setValue(WEST, EPipeState.NONE)
                .setValue(UP, EPipeState.NONE)
                .setValue(DOWN, EPipeState.NONE)
                .setValue(WATERLOGGED, false));
    }

    public abstract double getDefaultWidth();

    public boolean canConnectTo(BlockGetter level, BlockPos neighborPos, BlockState neighborState, Direction facing) {
        if(neighborState.isAir()) return false;
        if (neighborState.getBlock() instanceof AbstractPipeBL){
            if(neighborState.hasProperty(getPropertyForDirection(facing.getOpposite())) && neighborState.getValue(getPropertyForDirection(facing.getOpposite())).equals(EPipeState.DISCONNECTED)) return false;
            else return true;
        } else {
            return connectionTest(level, neighborPos, neighborState, facing);
        }
    }

    protected abstract boolean connectionTest(BlockGetter level, BlockPos pos, BlockState state, Direction facing);

    protected boolean isPipe(BlockState state, Block block) {
        if (block instanceof AbstractPipeBL){
            return true;
        }
        return false;
    }

    protected EPipeState getPipeStateForNeighbor(BlockGetter level, BlockPos pos, Direction facing) {

        BlockState currentState = level.getBlockState(pos);

        if (currentState.hasProperty(getPropertyForDirection(facing)) && currentState.getValue(getPropertyForDirection(facing)) == DISCONNECTED) {
            return EPipeState.DISCONNECTED;
        }


        BlockPos neighborPos = pos.relative(facing);
        BlockState neighborState = level.getBlockState(neighborPos);


        if (canConnectTo(level, neighborPos, neighborState, facing)) {

            if (currentState.hasProperty(getPropertyForDirection(facing))) {
                switch (currentState.getValue(getPropertyForDirection(facing))) {
                    case INSERT, OUTPUT: {
                        return isPipe(neighborState, neighborState.getBlock())?CONNECT:currentState.getValue(getPropertyForDirection(facing));
                    }
                }
            }
            return EPipeState.CONNECT;
        }
        // TODO: 添加判断 INSERT 和 OUTPUT 状态的逻辑
        return EPipeState.NONE;
    }

    @Override
    public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        //if (!level.isClientSide && level.getBlockEntity(pos) instanceof AbstractPipeBE cable) {
            //cable.markDirty();
        //}
        BlockState blockState = calculateState(level, pos, state);
        if (state != blockState) {
            level.setBlockAndUpdate(pos, blockState);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED);
    }

    @Nonnull
    @Override
    public BlockState updateShape(BlockState state, @Nonnull Direction direction, @Nonnull BlockState neighbourState, @Nonnull LevelAccessor world, @Nonnull BlockPos current, @Nonnull BlockPos offset) {
        if (state.getValue(WATERLOGGED)) {
            world.getFluidTicks().schedule(new ScheduledTick<>(Fluids.WATER, current, Fluids.WATER.getTickDelay(world), 0L));
        }
        return calculateStateWhenUpdate(world, current, state);
    }

    @Nullable
    public BlockState calculateStateWhenUpdate(LevelAccessor level, BlockPos pos, BlockState state) {
        return state
                .setValue(NORTH, getPipeStateForNeighbor(level, pos, Direction.NORTH))
                .setValue(EAST, getPipeStateForNeighbor(level, pos, Direction.EAST))
                .setValue(SOUTH, getPipeStateForNeighbor(level, pos, Direction.SOUTH))
                .setValue(WEST, getPipeStateForNeighbor(level, pos, Direction.WEST))
                .setValue(UP, getPipeStateForNeighbor(level, pos, Direction.UP))
                .setValue(DOWN, getPipeStateForNeighbor(level, pos, Direction.DOWN));
    }


    @Nullable
    public BlockState calculateState(LevelAccessor level, BlockPos pos, BlockState state) {
        return state
                .setValue(NORTH, getPipeStateForNeighbor(level, pos, Direction.NORTH))
                .setValue(EAST, getPipeStateForNeighbor(level, pos, Direction.EAST))
                .setValue(SOUTH, getPipeStateForNeighbor(level, pos, Direction.SOUTH))
                .setValue(WEST, getPipeStateForNeighbor(level, pos, Direction.WEST))
                .setValue(UP, getPipeStateForNeighbor(level, pos, Direction.UP))
                .setValue(DOWN, getPipeStateForNeighbor(level, pos, Direction.DOWN));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            Direction facing = Direction.getNearest(fromPos.getX() - pos.getX(), fromPos.getY() - pos.getY(), fromPos.getZ() - pos.getZ()).getOpposite();
            Main.log(pos.toString());
            Main.log(facing.toString());
            if (facing != null) {
                EnumProperty<EPipeState> property = getPropertyForDirection(facing);
                if (state.getValue(property) != EPipeState.DISCONNECTED) {
                    BlockState newState = state.setValue(property, getPipeStateForNeighbor(level, pos, facing));
                    if (newState.getValue(WATERLOGGED)) {
                        level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
                    }
                    if (newState != state) {
                        level.setBlock(pos, newState, 2); // 触发客户端更新
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
                if (newState != state) {
                    level.setBlock(pos, newState, 2); // 触发客户端更新
                }
            }
        }
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
    }


    public abstract boolean isSamePipe(Block target);
    // TODO: 添加设置和获取特定面连接状态的方法 (如果需要)


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        float width = (float) getDefaultWidth();
        float center = 0.5F;
        float radius = width / 2.0F;

        VoxelShape baseShape = Shapes.box(center - radius, center - radius, center - radius, center + radius, center + radius, center + radius);
        VoxelShape finalShape = baseShape;

        if (state.getValue(NORTH) != EPipeState.NONE && state.getValue(NORTH) != EPipeState.DISCONNECTED) {
            finalShape = Shapes.or(finalShape, Shapes.box(center - radius, center - radius, 0.0D, center + radius, center + radius, center - radius));
        }
        if (state.getValue(EAST) != EPipeState.NONE && state.getValue(EAST) != EPipeState.DISCONNECTED) {
            finalShape = Shapes.or(finalShape, Shapes.box(center + radius, center - radius, center - radius, 1.0D, center + radius, center + radius));
        }
        if (state.getValue(SOUTH) != EPipeState.NONE && state.getValue(SOUTH) != EPipeState.DISCONNECTED) {
            finalShape = Shapes.or(finalShape, Shapes.box(center - radius, center - radius, center + radius, center + radius, center + radius, 1.0D));
        }
        if (state.getValue(WEST) != EPipeState.NONE && state.getValue(WEST) != EPipeState.DISCONNECTED) {
            finalShape = Shapes.or(finalShape, Shapes.box(0.0D, center - radius, center - radius, center - radius, center + radius, center + radius));
        }
        if (state.getValue(UP) != EPipeState.NONE && state.getValue(UP) != EPipeState.DISCONNECTED) {
            finalShape = Shapes.or(finalShape, Shapes.box(center - radius, center + radius, center - radius, center + radius, 1.0D, center + radius));
        }
        if (state.getValue(DOWN) != EPipeState.NONE && state.getValue(DOWN) != EPipeState.DISCONNECTED) {
            finalShape = Shapes.or(finalShape, Shapes.box(center - radius, 0.0D, center - radius, center + radius, center - radius, center + radius));
        }

        return finalShape;
    }
}