package kivo.millennium.milltek.pipe.client;

import kivo.millennium.milltek.init.MillenniumLevelNetworkType.LevelNetworkType;
import kivo.millennium.milltek.machine.EIOState;
import kivo.millennium.milltek.pipe.client.network.AbstractLevelNetwork;
import kivo.millennium.milltek.world.LevelNetworkSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;

import java.util.*;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import static kivo.millennium.milltek.machine.EIOState.getPropertyForDirection;

public abstract class PipeBE<T extends AbstractLevelNetwork> extends BlockEntity {
    private UUID networkUUID = null;
    private final LevelNetworkType<T> networkType;
    protected static final Logger logger = LogUtils.getLogger();
    // 预定义每tick最大输入/输出
    protected int maxInputPerTick = 100000;
    protected int maxOutputPerTick = 100000;

    // 缓存本tick需要输入/输出的目标
    protected final List<IOEntry> inputTargets = new ArrayList<>();
    protected final List<IOEntry> outputTargets = new ArrayList<>();

    /** 控制是否在tickServer时打印调试信息 */
    public static boolean DEBUG_TICK_LOG = false;

    public PipeBE(BlockEntityType<?> pType, LevelNetworkType<T> networkType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        this.networkType = networkType;
    }

    protected T getNetwork() {
        if (networkUUID == null) {
            return null;
        }
        return (T) getNetworkData().getNetworkByUuid(networkType, networkUUID);
    }

    protected LevelNetworkSavedData getNetworkData() {
        return LevelNetworkSavedData.get(getLevel());
    }

    protected void setNetworkUUID(UUID uuid) {
        if (!Objects.equals(this.networkUUID, uuid)) {
            logger.info("[PipeBE] setNetworkUUID: " + this.networkUUID + " -> " + uuid);
        }
        this.networkUUID = uuid;
    }

    /**
     * 方块实体的静态tick方法
     */
    public static <T extends AbstractLevelNetwork> void tick(Level level, BlockPos pos, BlockState state,
            PipeBE<T> be) {
        if (level != null && !level.isClientSide) {
            be.tickServer();
        }
    }

    /**
     * 服务端tick，直接与网络交互
     */
    protected void tickServer() {
        if (DEBUG_TICK_LOG) {
            System.out.println("[PipeBE] Pos: " + worldPosition +
                    ", NetworkUUID: " + networkUUID +
                    ", NetworkType: " + networkType +
                    ", Network: " + getNetwork());
        }

        inputTargets.clear();
        outputTargets.clear();
        updateConnections(inputTargets, outputTargets);

        T network = getNetwork();
        if (network != null) {
            if (DEBUG_TICK_LOG) {
                System.out.println("[PipeBE] InputTargets: " + inputTargets.size() +
                        ", OutputTargets: " + outputTargets.size());
            }
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
            EIOState pipeState = state.hasProperty(getPropertyForDirection(dir))
                    ? state.getValue(getPropertyForDirection(dir))
                    : EIOState.NONE;

            // connect 视为输出
            if (pipeState == EIOState.CONNECT || pipeState == EIOState.PUSH) {
                outputTargets.add(new IOEntry(neighbor, dir));
            }

            if (pipeState == EIOState.PULL) {
                inputTargets.add(new IOEntry(neighbor, dir));
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
        if (level == null || level.isClientSide)
            return;

        // 检查当前位置的 Block 是否还是本类型
        if (level.isLoaded(getBlockPos()) && level.getBlockState(getBlockPos()).getBlock() != getBlock()) {
            logger.info("[PipeBE] Block is not the same, removing networkUUID: " + networkUUID);
            // 方块被移除（破坏或替换），此时可安全进行网络分裂等逻辑
            // ...分裂/断网处理...
            if (level == null || level.isClientSide || networkUUID == null || isRemoved())
                return;
            UUID uuid = networkUUID;
            // 1. 收集所有与本方块同 networkUUID 的管道
            Set<BlockPos> allPipes = new HashSet<>();
            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = worldPosition.relative(dir);
                BlockEntity neighbor = level.getBlockEntity(neighborPos);
                if (neighbor instanceof PipeBE<?> neighborPipe
                        && neighborPipe.networkType == this.networkType
                        && networkUUID.equals(neighborPipe.networkUUID)) {
                    allPipes.add(neighborPos);
                }
            }

            // 2. Flood Fill/BFS分裂网络
            Set<BlockPos> visited = new HashSet<>();
            List<Set<BlockPos>> groups = new ArrayList<>();

            for (BlockPos start : allPipes) {
                if (visited.contains(start))
                    continue;
                Set<BlockPos> group = new HashSet<>();
                Queue<BlockPos> queue = new ArrayDeque<>();
                queue.add(start);

                while (!queue.isEmpty()) {
                    BlockPos pos = queue.poll();
                    if (!visited.add(pos))
                        continue;
                    group.add(pos);

                    for (Direction dir : Direction.values()) {
                        BlockPos next = pos.relative(dir);
                        if (visited.contains(next))
                            continue;
                        BlockEntity be = level.getBlockEntity(next);
                        if (be instanceof PipeBE<?> pipe
                                && pipe.networkType == this.networkType
                                && networkUUID.equals(pipe.networkUUID)) {
                            queue.add(next);
                        }
                    }
                }
                if (!group.isEmpty())
                    groups.add(group);
            }

            // 3. 为每个连通块分配新networkUUID和新网络，并委托子类迁移内容
            List<T> newNetworks = new ArrayList<>();
            List<List<PipeBE<T>>> pipeGroups = new ArrayList<>();
            for (Set<BlockPos> group : groups) {
                UUID newUUID = UUID.randomUUID();
                T newNetwork = networkType.create(newUUID);
                getNetworkData().addNetwork(networkType, newNetwork);
                List<PipeBE<T>> pipes = new ArrayList<>();
                for (BlockPos pos : group) {
                    BlockEntity be = level.getBlockEntity(pos);
                    if (be instanceof PipeBE<?> pipe && pipe.networkType == this.networkType) {
                        ((PipeBE<T>) pipe).setNetworkUUID(newUUID);
                        // be.setChanged();
                        pipes.add((PipeBE<T>) pipe);
                    }
                }
                newNetworks.add(newNetwork);
                pipeGroups.add(pipes);
            }
            // 委托子类实现内容迁移（如流体、能量等）
            redistributeNetworkContent(newNetworks, pipeGroups);

            // 4. 清除自身networkUUID
            this.networkUUID = null;
            // 5. 清除网络数据
            getNetworkData().removeNetwork(networkType, uuid);
        } else {
            // 只是区块卸载，不做分裂等破坏性操作
        }
        super.setRemoved();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level == null || level.isClientSide)
            return;
        if (this.networkUUID != null)
            return; // 已有网络，不要重复分配
        // 尝试连接周围相同Type的网络
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(dir);
            BlockEntity neighbor = level.getBlockEntity(neighborPos);
            if (neighbor instanceof PipeBE<?> neighborPipe) {
                if (neighborPipe.networkType == this.networkType) {
                    UUID neighborUUID = neighborPipe.networkUUID;
                    if (neighborUUID != null) {
                        this.setNetworkUUID(neighborUUID);
                        return;
                    }
                }
            }
        }
        // 若未找到可用网络，则新建
        T newNetwork = networkType.create(java.util.UUID.randomUUID());
        getNetworkData().addNetwork(networkType, newNetwork);
        this.setNetworkUUID(newNetwork.getUuid());
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (networkUUID != null) {
            tag.putUUID("networkUUID", networkUUID);
            if (DEBUG_TICK_LOG) {
                logger.info("[PipeBE] Saving networkUUID: " + networkUUID);
            }
        } else {
            tag.remove("networkUUID");
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.hasUUID("networkUUID")) {
            this.networkUUID = tag.getUUID("networkUUID");
            if (DEBUG_TICK_LOG) {
                logger.info("[PipeBE] Loading networkUUID: " + networkUUID);
            }
        } else {
            this.networkUUID = null;
        }
    }

    /**
     * 当网络分裂时，将原网络内容重新分配到新网络。
     * 
     * @param newNetworks 新分配的所有网络对象
     * @param groups      每个连通块内所有管道实体的列表，顺序与newNetworks一致
     */
    protected abstract void redistributeNetworkContent(List<T> newNetworks, List<List<PipeBE<T>>> groups);

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

    @Override
    public void handleUpdateTag(CompoundTag compoundTag) {
        super.handleUpdateTag(compoundTag);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.inputTargets.clear();
        this.outputTargets.clear();

        this.networkUUID = null;
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
    }

    protected abstract Block getBlock();
}