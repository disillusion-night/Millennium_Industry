package kivo.millennium.milltek.pipe.client;

import kivo.millennium.milltek.init.MillenniumLevelNetworkType.LevelNetworkType;
import kivo.millennium.milltek.pipe.client.network.AbstractLevelNetwork;
import kivo.millennium.milltek.world.LevelNetworkSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public abstract class PipeBE<T extends AbstractLevelNetwork> extends BlockEntity {
    private UUID networkUUID = null;
    private final LevelNetworkType<T> networkType;

    // 预定义每tick最大输入/输出
    protected int maxInputPerTick = 100000;
    protected int maxOutputPerTick = 100000;

    // 缓存本tick需要输入/输出的目标
    protected final List<IOEntry> inputTargets = new ArrayList<>();
    protected final List<IOEntry> outputTargets = new ArrayList<>();

    public PipeBE(BlockEntityType<?> pType, LevelNetworkType<T> networkType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        this.networkType = networkType;
    }

    protected T getNetwork() {
        if (networkUUID == null) {
            return null;
        }
        return (T) LevelNetworkSavedData.getInstance().getNetworkByUuid(networkType, networkUUID);
    }

    protected void setNetworkUUID(UUID uuid) {
        this.networkUUID = uuid;
    }

    /**
     * 方块实体的静态tick方法
     */
    public static <T extends AbstractLevelNetwork> void tick(Level level, BlockPos pos, BlockState state,
            PipeBE<T> be) {
        if (!level.isClientSide) {
            be.tickServer();
        }
    }

    /**
     * 服务端tick，直接与网络交互
     */
    protected void tickServer() {
        // 每tick清空目标集合
        inputTargets.clear();
        outputTargets.clear();

        // 扫描并缓存输入/输出目标
        updateConnections(inputTargets, outputTargets);

        T network = getNetwork();
        if (network != null) {
            handleInputToNetwork(network, inputTargets);
            handleOutputFromNetwork(network, outputTargets);
        }
    }

    protected void updateConnections(List<IOEntry> inputTargets, List<IOEntry> outputTargets) {
        if (level == null)
            return;
        BlockState state = level.getBlockState(worldPosition);
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(dir);
            BlockEntity neighbor = level.getBlockEntity(neighborPos);
            BlockState neighborState = level.getBlockState(neighborPos);

            if (neighbor instanceof PipeBE)
                continue;

            // 判断本方块该面的连接状态
            EPipeState pipeState = state.hasProperty(AbstractPipeBL.getPropertyForDirection(dir))
                    ? state.getValue(AbstractPipeBL.getPropertyForDirection(dir))
                    : EPipeState.NONE;

            // connect 视为输出
            if (pipeState == EPipeState.CONNECT) {
                outputTargets.add(new IOEntry(neighbor, dir));
            }
        }
    }

    /**
     * 输入数据到网络，具体逻辑由子类实现
     */
    protected abstract void handleInputToNetwork(T network, List<IOEntry> inputTargets);

    /**
     * 从网络输出数据，具体逻辑由子类实现
     */
    protected abstract void handleOutputFromNetwork(T network, List<IOEntry> outputTargets);

    @Override
    public void setRemoved() {
        super.setRemoved();
        // 可选：移除与网络的关联，具体逻辑可由子类实现
    }

    /**
     * 输入/输出目标结构体
     */
    public static class IOEntry {
        public final BlockEntity be;
        public final Direction direction;

        public IOEntry(BlockEntity be, Direction direction) {
            this.be = be;
            this.direction = direction;
        }
    }
}