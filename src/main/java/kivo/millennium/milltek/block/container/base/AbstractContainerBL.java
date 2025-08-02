package kivo.millennium.milltek.block.container.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nonnull;
import kivo.millennium.milltek.block.property.EFaceMode;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractContainerBL extends Block implements EntityBlock {
    public static final EnumProperty<EFaceMode> FACE_MODE_NORTH = EnumProperty.create("face_mode_north",
            EFaceMode.class);
    public static final EnumProperty<EFaceMode> FACE_MODE_SOUTH = EnumProperty.create("face_mode_south",
            EFaceMode.class);
    public static final EnumProperty<EFaceMode> FACE_MODE_EAST = EnumProperty.create("face_mode_east", EFaceMode.class);
    public static final EnumProperty<EFaceMode> FACE_MODE_WEST = EnumProperty.create("face_mode_west", EFaceMode.class);
    public static final EnumProperty<EFaceMode> FACE_MODE_UP = EnumProperty.create("face_mode_up", EFaceMode.class);
    public static final EnumProperty<EFaceMode> FACE_MODE_DOWN = EnumProperty.create("face_mode_down", EFaceMode.class);

    private static final Map<Direction, EnumProperty<EFaceMode>> FACE_MODE_MAP = new EnumMap<>(Direction.class);
    static {
        FACE_MODE_MAP.put(Direction.NORTH, FACE_MODE_NORTH);
        FACE_MODE_MAP.put(Direction.SOUTH, FACE_MODE_SOUTH);
        FACE_MODE_MAP.put(Direction.EAST, FACE_MODE_EAST);
        FACE_MODE_MAP.put(Direction.WEST, FACE_MODE_WEST);
        FACE_MODE_MAP.put(Direction.UP, FACE_MODE_UP);
        FACE_MODE_MAP.put(Direction.DOWN, FACE_MODE_DOWN);
    }

    public AbstractContainerBL(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACE_MODE_NORTH, FACE_MODE_SOUTH, FACE_MODE_EAST, FACE_MODE_WEST, FACE_MODE_UP, FACE_MODE_DOWN);
    }

    public EFaceMode getFaceMode(BlockState state, Direction dir) {
        EnumProperty<EFaceMode> prop = FACE_MODE_MAP.get(dir);
        if (prop != null) {
            return state.getValue(prop);
        }
        return EFaceMode.NONE;
    }

    public BlockState setFaceMode(BlockState state, Direction dir, EFaceMode mode) {
        EnumProperty<EFaceMode> prop = FACE_MODE_MAP.get(dir);
        if (prop != null) {
            return state.setValue(prop, mode);
        }
        return state;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return createBlockEntity(pos, state);
    }

    protected abstract BlockEntity createBlockEntity(BlockPos pos, BlockState state);

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> type) {
        return (lvl, pos, st, be) -> {
            if (be instanceof AbstractContainerBE containerBE) {
                containerBE.tickServer();
            }
        };
    }
}
