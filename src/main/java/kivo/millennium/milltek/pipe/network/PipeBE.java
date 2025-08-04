package kivo.millennium.milltek.pipe.network;

import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import kivo.millennium.milltek.init.MillenniumLevelNetworkType.LevelNetworkType;
import static kivo.millennium.milltek.pipe.network.EPipeState.DISCONNECT;
import kivo.millennium.milltek.world.LevelNetworkSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;


public abstract class PipeBE<NET extends AbstractLevelNetwork> extends BlockEntity {
    private UUID networkUUID = null;
    private final LevelNetworkType<NET> networkType;
    protected static final Logger logger = LogUtils.getLogger();
    // 预定义每tick最大输入/输出
    protected int maxInputPerTick = 100000;
    protected int capacity; // 管道的容量，默认为100000
    protected int maxOutputPerTick = 100000;
    protected PipeData pipeData;

    /** 控制是否在tickServer时打印调试信息 */
    public static boolean DEBUG_TICK_LOG = true;

    public PipeBE(BlockEntityType<?> pType, LevelNetworkType<NET> networkType, BlockPos pPos,
                  BlockState pBlockState, int capacity) {
        super(pType, pPos, pBlockState);
        this.networkType = networkType;
        this.capacity = capacity;
        this.pipeData = new PipeData(capacity);
    }

    public NET getNetwork() {
        if (networkUUID == null) {
            return null;
        }
        
        return getNetworkData().getNetwork(networkType, networkUUID);
    }

    protected LevelNetworkSavedData getNetworkData() {
        LevelNetworkSavedData savedData = LevelNetworkSavedData.get(getLevel());
        //savedData.debugPrintNetworks();
        return savedData;
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

    protected void tickServer() {
    }

    public void updateNetwork(UUID networkUUID) {
        this.networkUUID = networkUUID;
    }

    public void syncBlockStateFromNetwork() {
        pipeData = getNetwork().getPipeData(worldPosition);
        BlockState newState = getBlockState();
        newState.setValue(AbstractPipeBL.UP, pipeData.getUP());
        newState.setValue(AbstractPipeBL.DOWN, pipeData.getDOWN());
        newState.setValue(AbstractPipeBL.NORTH, pipeData.getNORTH());
        newState.setValue(AbstractPipeBL.SOUTH, pipeData.getSOUTH());
        newState.setValue(AbstractPipeBL.EAST, pipeData.getEAST());
        newState.setValue(AbstractPipeBL.WEST, pipeData.getWEST());
        level.setBlockAndUpdate(worldPosition, newState);
    }

    @Override
    public void setRemoved() {
        if (level == null || level.isClientSide)
            return;
        ServerLevel serverLevel = (ServerLevel) level;
        if (networkUUID != null) {
            NET network = getNetwork();
            if (network != null) {
                network.removePipe(getNetworkData(),serverLevel, worldPosition);
                if (DEBUG_TICK_LOG) {
                    logger.info("[PipeBE] Removed pipe from network: " + networkUUID);
                }
            }
            this.networkUUID = null; // 清除网络UUID
        }
        super.setRemoved();
    }

    public void onCreate(ServerLevel level, BlockState pState) {
        // 尝试向六个方向寻找网络
        for (Direction direction : Direction.values()) {
            if (level.getBlockState(worldPosition.relative(direction)).is(Blocks.AIR)) {
                // 如果这个方向是空气，则不处理
                continue;
            }
            // 如果这个方向的方块没有be，则不处理
            if (!level.getBlockState(worldPosition.relative(direction)).hasBlockEntity()) {
                continue;
            }
            // 获取邻居方块实体
            BlockEntity neighborBE = level.getBlockEntity(worldPosition.relative(direction));

            if (neighborBE instanceof PipeBE<?> neighborPipe) {
                if (neighborPipe.getPipeData().getStateFromDirection(direction.getOpposite()) != DISCONNECT) {
                    // 如果邻居管道的状态不是断开，且自己没有网络，则加入它的网络
                    if (networkUUID == null) {
                        joinNetwork((NET) neighborPipe.getNetwork());
                        pipeData.setStateFromDirection(direction, EPipeState.PIPE);
                        pState = pState.setValue(AbstractPipeBL.getPropertyForDirection(direction), EPipeState.PIPE);
                        if (DEBUG_TICK_LOG) {
                            //打印设置连接状态信息
                            logger.info("[PipeBE] Joining network: " + neighborPipe.networkUUID + " from direction: " + direction);
                        }
                    } else {
                        if (networkUUID != ((PipeBE<?>) neighborBE).networkUUID && getNetwork().canMerge(neighborPipe.getNetwork())) {
                            NET mergedNetwork = (NET) getNetwork().merge(level, neighborPipe.getNetwork());
                            if (DEBUG_TICK_LOG) {
                                logger.info("[PipeBE] Merging networks: " + networkUUID + " and " + neighborPipe.networkUUID);
                            }
                            this.networkUUID = mergedNetwork.getUUID();
                            getNetworkData().addNetwork(networkType, mergedNetwork);
                            pState = pState.setValue(AbstractPipeBL.getPropertyForDirection(direction), EPipeState.PIPE);
                        }
                    }
                }
            } else if (neighborBE.getCapability(getCapabilityType(), direction.getOpposite()).isPresent()) {
                this.pipeData.setStateFromDirection(direction, EPipeState.CONNECT);
                pState = pState.setValue(AbstractPipeBL.getPropertyForDirection(direction), EPipeState.CONNECT);
            }
        }

        // 如果没有网络UUID，则创建一个新的网络
        if (networkUUID == null) {
            this.networkUUID = UUID.randomUUID();
            NET newNetwork = networkType.create(networkUUID);
            getNetworkData().addNetwork(networkType, newNetwork);
            if (DEBUG_TICK_LOG) {
                logger.info("[PipeBE] Created new network with UUID: " + networkUUID + " at position: " + worldPosition);
            }
            newNetwork.addPipe(worldPosition, pipeData);
        }
        level.setBlock(worldPosition, pState, 2);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level == null || level.isClientSide)
            return;
            
        // 在世界加载完成后同步管道数据
        if (networkUUID != null) {
            NET network = getNetwork();
            if (network != null) {
                PipeData loadedData = network.getPipeData(worldPosition);
                if (loadedData != null) {
                    this.pipeData = loadedData;
                    if (DEBUG_TICK_LOG) {
                        logger.info("[PipeBE] Synchronized pipe data from network: " + networkUUID + " at position: " + worldPosition);
                    }
                } else {
                    // 网络中没有找到此位置的管道数据，重新添加
                    network.addPipe(worldPosition, pipeData);
                    if (DEBUG_TICK_LOG) {
                        logger.info("[PipeBE] Re-added pipe data to network: " + networkUUID + " at position: " + worldPosition);
                    }
                }
            } else {
                // 网络不存在，清空UUID
                logger.warn("[PipeBE] Network not found for UUID: " + networkUUID + ", clearing UUID at position: " + worldPosition);
                this.networkUUID = null;
            }
        }
    }

    protected void joinNetwork(NET network) {
        if (network == null) {
            logger.warn("[PipeBE] Attempted to join a null network");
            return;
        }
        this.networkUUID = network.getUUID();
        getNetworkData().getNetwork(networkType, networkUUID).addPipe(worldPosition, pipeData);

        if (DEBUG_TICK_LOG) {
            logger.info("[PipeBE] Joined network with UUID: " + networkUUID + " at position: " + worldPosition);
        }
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
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
    public void load(@Nonnull CompoundTag tag) {
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

    @Override
    public void handleUpdateTag(CompoundTag compoundTag) {
        super.handleUpdateTag(compoundTag);
    }

    public void setPipeData(PipeData pipeData) {
        this.pipeData = pipeData;
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();

        this.networkUUID = null;
    }

    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == getCapabilityType()) {

            if (networkUUID == null || (side != null && pipeData.getStateFromDirection(side) == DISCONNECT)) {
                return LazyOptional.empty();
            }
            NET network = getNetwork();
        
            if (network == null) {
                return LazyOptional.empty();
            }
            return network.getCapability(cap, side);
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
    }

    public PipeData getPipeData(){
        return pipeData;
    }

    protected abstract <B extends AbstractPipeBL> B getBlock();

    protected abstract Capability<?> getCapabilityType();
}