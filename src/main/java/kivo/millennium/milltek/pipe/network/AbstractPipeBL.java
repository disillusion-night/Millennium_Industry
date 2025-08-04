package kivo.millennium.milltek.pipe.network;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraftforge.common.capabilities.Capability;
import org.slf4j.Logger;

import java.util.Objects;

public abstract class AbstractPipeBL extends Block implements SimpleWaterloggedBlock, EntityBlock {

    // 连接状态属性
    public static final EnumProperty<EPipeState> NORTH = EnumProperty.create("north", EPipeState.class);
    public static final EnumProperty<EPipeState> EAST = EnumProperty.create("east", EPipeState.class);
    public static final EnumProperty<EPipeState> SOUTH = EnumProperty.create("south", EPipeState.class);
    public static final EnumProperty<EPipeState> WEST = EnumProperty.create("west", EPipeState.class);
    public static final EnumProperty<EPipeState> UP = EnumProperty.create("up", EPipeState.class);
    public static final EnumProperty<EPipeState> DOWN = EnumProperty.create("down", EPipeState.class);

    private static final boolean DEBUG_LOG = true; // 控制是否打印调试信息
    private static final Logger logger = LogUtils.getLogger();

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

    @Override
    public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state,
            @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide()) {
            return; // 客户端不处理
        }
        ((PipeBE<?>) Objects.requireNonNull(level.getBlockEntity(pos))).onCreate((ServerLevel) level, state);
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
        if (state.getValue(getPropertyForDirection(direction)) == EPipeState.DISCONNECT) {
            return state;
        }

        if (neighbourState.isAir()){
            if (state.getValue(getPropertyForDirection(direction)).isNormal()) {
                // 如果当前状态是连接状态，但邻居是空气，则断开连接
                if(!world.isClientSide())
                    ((PipeBE<?>) world.getBlockEntity(current)).getPipeData().setStateFromDirection(direction, EPipeState.NONE);
                return state.setValue(getPropertyForDirection(direction), EPipeState.NONE);
            }
            return state;
        }

        if (!neighbourState.hasBlockEntity()){
            // 如果没有 BlockEntity，直接返回当前状态
            return state;
        }

        if (neighbourState.getBlock() instanceof AbstractPipeBL) {
            if (neighbourState.getValue(getPropertyForDirection(direction.getOpposite())) == EPipeState.PIPE) {
                if (!world.isClientSide()) {
                    // 如果邻居管道的对应方向是Pipe状态，则连接
                    ((PipeBE<?>) world.getBlockEntity(current)).getPipeData().setStateFromDirection(direction, EPipeState.PIPE);
                }
                return state.setValue(getPropertyForDirection(direction), EPipeState.PIPE);
            }
        }

        BlockEntity neighbourBE = world.getBlockEntity(offset);
        if (neighbourBE.getCapability(getCapabilityType(), direction.getOpposite()).isPresent()) {
            // 如果邻居有对应的能力，则连接
            if (!world.isClientSide()) {
                ((PipeBE<?>) world.getBlockEntity(current)).getPipeData().setStateFromDirection(direction, EPipeState.CONNECT);
            }
            return state.setValue(getPropertyForDirection(direction), EPipeState.CONNECT);
        } else {
            // 否则断开连接
            if (!world.isClientSide()) {
                ((PipeBE<?>) world.getBlockEntity(current)).getPipeData().setStateFromDirection(direction, EPipeState.NONE);
            }
            return state.setValue(getPropertyForDirection(direction), EPipeState.NONE);
        }
    }

    public abstract Capability<?> getCapabilityType();

    @Override
    @Nonnull
    public FluidState getFluidState(@Nonnull BlockState state) {
        // noinspection deprecation
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public abstract boolean isSamePipe(Block target);

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

        if (state.getValue(NORTH) != EPipeState.NONE && state.getValue(NORTH) != EPipeState.DISCONNECT) {
            finalShape = Shapes.or(finalShape, Shapes.box(center - radius, center - radius, 0.0D, center + radius,
                    center + radius, center - radius));
        }
        if (state.getValue(EAST) != EPipeState.NONE && state.getValue(EAST) != EPipeState.DISCONNECT) {
            finalShape = Shapes.or(finalShape, Shapes.box(center + radius, center - radius, center - radius, 1.0D,
                    center + radius, center + radius));
        }
        if (state.getValue(SOUTH) != EPipeState.NONE && state.getValue(SOUTH) != EPipeState.DISCONNECT) {
            finalShape = Shapes.or(finalShape, Shapes.box(center - radius, center - radius, center + radius,
                    center + radius, center + radius, 1.0D));
        }
        if (state.getValue(WEST) != EPipeState.NONE && state.getValue(WEST) != EPipeState.DISCONNECT) {
            finalShape = Shapes.or(finalShape, Shapes.box(0.0D, center - radius, center - radius, center - radius,
                    center + radius, center + radius));
        }
        if (state.getValue(UP) != EPipeState.NONE && state.getValue(UP) != EPipeState.DISCONNECT) {
            finalShape = Shapes.or(finalShape, Shapes.box(center - radius, center + radius, center - radius,
                    center + radius, 1.0D, center + radius));
        }
        if (state.getValue(DOWN) != EPipeState.NONE && state.getValue(DOWN) != EPipeState.DISCONNECT) {
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
                : this.createTickerHelper(pBlockEntityType, blockEntityType(), PipeBE::tick); // 服务端每 tick 调用 BlockEntity 的 tick 方法
    }

    @SuppressWarnings("unchecked")
    protected <T extends BlockEntity, A extends BlockEntity> BlockEntityTicker<T> createTickerHelper(
            @Nonnull BlockEntityType<T> pServerType, @Nonnull BlockEntityType<A> pExpectedType,
            @Nullable BlockEntityTicker<? super A> pTicker) {
        return pExpectedType == pServerType ? (BlockEntityTicker<T>) pTicker : null;
    }

    public static EnumProperty<EPipeState> getPropertyForDirection(Direction direction) {
        switch (direction) {
            case NORTH:
                return NORTH;
            case EAST:
                return EAST;
            case SOUTH:
                return SOUTH;
            case WEST:
                return WEST;
            case UP:
                return UP;
            case DOWN:
                return DOWN;
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }

    @Nonnull
    public abstract BlockEntityType<? extends PipeBE<?>> blockEntityType(); // 强制子类提供其 BlockEntityType
}