package kivo.millennium.millind.block.device;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractDeviceBL extends Block implements EntityBlock {
    public static final BooleanProperty WORKING = MillenniumBlockProperty.WORKING;

    public AbstractDeviceBL(Properties properties) {
        super(properties.sound(SoundType.METAL));
        this.registerDefaultState(this.stateDefinition.any().setValue(WORKING, false));
    }

    @Nullable
    @Override
    public abstract BlockEntity newBlockEntity(BlockPos pPos, BlockState pState); // 强制子类实现创建 BlockEntity 的方法

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL; // 默认为模型渲染，可以根据需要修改为 INVISIBLE 或 ENTITYBLOCK_ANIMATED
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WORKING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(WORKING, false);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(net.minecraft.world.level.Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide() ? null : this.createTickerHelper(pBlockEntityType, blockEntityType(), AbstractMachineBE::tick); // 服务端每 tick 调用 BlockEntity 的 tick 方法
    }

    protected <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> pServerType, BlockEntityType<E> pExpectedType, BlockEntityTicker<? super E> pTicker) {
        return pExpectedType == pServerType ? (BlockEntityTicker<A>)pTicker : null;
    }

    protected abstract BlockEntityType<? extends AbstractMachineBE> blockEntityType(); // 强制子类提供其 BlockEntityType


    // 添加默认的 use 方法以打开 GUI
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) { // 确保在服务端执行
            BlockEntity be = pLevel.getBlockEntity(pPos);
            if(be instanceof AbstractMachineBE){
                MenuProvider menuProvider = this.getMenuProvider(pState, pLevel, be.getBlockPos()); // 获取 MenuProvider
                if (menuProvider != null && pPlayer instanceof ServerPlayer) { // 检查 MenuProvider 和玩家类型
                    NetworkHooks.openScreen((ServerPlayer) pPlayer, menuProvider, be.getBlockPos()); // 使用 NetworkHooks 打开 GUI
                } else {
                    throw new IllegalStateException("容器创建失败!"); // 如果 MenuProvider 为空，抛出异常
                }
            }
        }

        return InteractionResult.SUCCESS; // 标记交互成功，客户端不需要做更多处理
    }

    // 添加默认的 getMenuProvider 方法
    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
        return new MenuProvider() { // 创建匿名内部类 MenuProvider
            @Override
            public Component getDisplayName() {
                return Component.translatable(getDescriptionId()); // 默认使用方块的本地化名称作为 GUI 标题
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
                return createContainerMenu(pContainerId, pPlayerInventory, pPos, pPlayer); // 调用 createContainerMenu 创建容器
            }
        };
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof AbstractMachineBE) {
                if (pLevel instanceof ServerLevel) {
                    ((AbstractMachineBE) blockentity).drops();
                }

                pLevel.updateNeighbourForOutputSignal(pPos, this);
            }

            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }


    // 抽象方法 createContainerMenu，子类需要实现创建具体容器的逻辑
    protected abstract AbstractContainerMenu createContainerMenu(int containerId, Inventory playerInventory, BlockPos pos, Player player);
}