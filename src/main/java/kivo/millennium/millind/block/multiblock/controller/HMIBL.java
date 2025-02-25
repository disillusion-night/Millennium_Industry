package kivo.millennium.millind.block.multiblock.controller;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class HMIBL extends HorizontalDirectionalBlock implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
    protected static final VoxelShape WEST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public HMIBL() {
        super(Properties.of().noOcclusion().sound(SoundType.GLASS));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // **简化 getShape 方法，只根据 FACING 返回 WALL 贴附面的碰撞箱**
        switch (state.getValue(FACING)) {
            case EAST:
                return EAST_AABB;
            case WEST:
                return WEST_AABB;
            case SOUTH:
                return SOUTH_AABB;
            default: // NORTH
                return NORTH_AABB;
        }
    }


    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getClickedFace();

        if (direction.getAxis().isHorizontal()) {
            return this.defaultBlockState().setValue(FACING, direction);
        } else {
            return null;
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            // **1. 获取方块实体**
            BlockEntity blockEntity = level.getBlockEntity(pos);

            // **2. 检查方块实体类型是否为 HMIBEBlockEntity (或你实际的 HMIBE 类名)**
            if (blockEntity instanceof HMIBE) {
                HMIBE hmibe = (HMIBE) blockEntity; // 类型转换

                // **3. 调用 HMIBEBlockEntity 中的方法处理右键事件**
                //  假设你在 HMIBEBlockEntity 类中创建了一个名为 handleRightClickEvent 的方法
                return hmibe.handleRightClickEvent(state, level, pos, player, hand, hit); // 调用 HMIBE 的事件处理方法并返回结果

                //  [如果你的 HMIBEBlockEntity 中没有自定义的事件处理方法，而是直接在 use() 中打开 GUI，则可以简化为:]
                // player.openMenu((HMIBEBlockEntity) blockEntity); //  直接打开 GUI，无需额外的事件处理方法
                // return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide()); // 客户端返回成功，服务端已处理
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new HMIBE(blockPos, blockState);
    }
}