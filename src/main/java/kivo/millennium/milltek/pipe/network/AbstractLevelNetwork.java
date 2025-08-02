package kivo.millennium.milltek.pipe.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import kivo.millennium.milltek.init.MillenniumLevelNetworkType;
import kivo.millennium.milltek.init.MillenniumLevelNetworkType.LevelNetworkType;
import static kivo.millennium.milltek.pipe.network.EPipeState.DISCONNECT;
import kivo.millennium.milltek.world.LevelNetworkSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public abstract class AbstractLevelNetwork {
    protected boolean isDirty = false;
    protected final UUID uuid;
    protected final LevelNetworkType<?> levelNetworkType;
    private static final boolean DEBUG_LOG = true; // 控制是否打印调试信息
    // 存储管道数据的集合
    protected final HashMap<BlockPos, PipeData> pipeDataHashMap = new HashMap<>();

    public AbstractLevelNetwork(LevelNetworkType<?> levelNetworkType, UUID uuid) {
        this.levelNetworkType = levelNetworkType;
        this.uuid = uuid;
    }

    public void writeToNBT(CompoundTag compoundTag) {
        compoundTag.putUUID("uuid", uuid);
        // 存储所有管道数据
        var list = new net.minecraft.nbt.ListTag();
        for (BlockPos blockPos : pipeDataHashMap.keySet()) {
            CompoundTag tag = new CompoundTag();
            tag.put("pos", NbtUtils.writeBlockPos(blockPos));
            tag.put("data", pipeDataHashMap.get(blockPos).writeToNBT());
            list.add(tag);
        }
        compoundTag.put("pipes", list);
        // 可在子类中扩展更多数据存储
    }

    public AbstractLevelNetwork(LevelNetworkType<?> levelNetworkType, CompoundTag tag) {
        this.levelNetworkType = levelNetworkType;
        this.uuid = tag.getUUID("uuid");
        if (tag.contains("pipes")) {
            // 读取管道数据
            if (DEBUG_LOG) {
                System.out.println("Reading pipe data...");
            }

            var list = tag.getList("pipes", net.minecraft.nbt.Tag.TAG_COMPOUND);
            // 重建PipeData
            for (var i = 0; i < list.size(); i++) {
                var pipeTag = list.getCompound(i);
                BlockPos pos = NbtUtils.readBlockPos(pipeTag.getCompound("pos"));
                PipeData pipeData = new PipeData(pipeTag.getCompound("data"));
                pipeDataHashMap.put(pos, pipeData);
            }
        }
    }

    public static AbstractLevelNetwork createNetwork(String type, UUID uuid) {
        var registryObject = MillenniumLevelNetworkType.LEVEL_NETWORK_TYPES.getEntries().stream()
                .filter(entry -> entry.getId().getPath().equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown network type: " + type));

        return registryObject.get().create(uuid);
    }

    public abstract boolean canMerge(AbstractLevelNetwork other);

    public abstract <T extends AbstractLevelNetwork> T merge(ServerLevel level, T other);
    
    /**
     * 子类实现此方法来合并自己特有的能力数据（如能量存储、流体存储等）
     * @param other 要合并的其他网络
     */
    protected abstract void mergeCapabilities(AbstractLevelNetwork other);
    
    /**
     * 通用的合并方法，处理节点管理逻辑
     * @param level 服务器世界
     * @param other 要合并的其他网络
     * @return 合并后的网络
     */
    @SuppressWarnings("unchecked")
    protected <T extends AbstractLevelNetwork> T performMerge(ServerLevel level, T other) {
        if (!canMerge(other)) {
            throw new IllegalArgumentException("Cannot merge incompatible network types");
        }
        
        // 如果其他网络的管道数据更多，则将当前网络合并到其他网络
        if (other.pipeDataHashMap.size() > this.pipeDataHashMap.size()) {
            return other.performMerge(level, (T) this);
        }
        
        // 合并子类特有的能力数据
        mergeCapabilities(other);
        
        // 合并管道数据
        this.pipeDataHashMap.putAll(other.pipeDataHashMap);
        
        // 清理被合并的网络
        other.remove();
        
        // 更新所有管道的网络引用
        updatePipeNetworkReferences(level);
        
        setDirty();
        return (T) this;
    }
    
    /**
     * 更新所有管道的网络引用
     * @param level 服务器世界
     */
    private void updatePipeNetworkReferences(ServerLevel level) {
        for (BlockPos pos : this.pipeDataHashMap.keySet()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof PipeBE<?> pipeBE) {
                pipeBE.updateNetwork(uuid);
            }
        }
    }

    protected void setDirty() {
        isDirty = true;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void recalculateNetwork(LevelNetworkSavedData savedData, ServerLevel level) {
        // 如果网络为空，直接移除
        if (isEmpty()) {
            savedData.removeNetwork(levelNetworkType, uuid);
            return;
        }
        
        // 使用DFS遍历管道数据并裂变网络
        Set<BlockPos> visited = new HashSet<>();
        List<List<BlockPos>> connectedComponents = new ArrayList<>();
        
        for (BlockPos pos : pipeDataHashMap.keySet()) {
            if (!visited.contains(pos)) {
                List<BlockPos> component = new ArrayList<>();
                dfsTraversal(pos, visited, component);
                if (!component.isEmpty()) {
                    connectedComponents.add(component);
                }
            }
        }

        // 如果只有一个连通分量，网络不需要分裂
        if (connectedComponents.size() <= 1) {
            return;
        }

        // 创建子网络
        for (List<BlockPos> component : connectedComponents) {
            createSubNetwork(savedData, level, component);
        }

        // 清除当前网络并移除
        clear();
        savedData.removeNetwork(levelNetworkType, uuid);
        setDirty();
    }
    
    private void dfsTraversal(BlockPos startPos, Set<BlockPos> visited, List<BlockPos> component) {
        Stack<BlockPos> stack = new Stack<>();
        stack.push(startPos);
        
        while (!stack.isEmpty()) {
            BlockPos current = stack.pop();
            if (visited.contains(current)) continue;
            
            visited.add(current);
            component.add(current);
            
            PipeData pipeData = pipeDataHashMap.get(current);
            if (pipeData == null) continue;
            
            for (Direction direction : Direction.values()) {
                if (pipeData.getStateFromDirection(direction) == DISCONNECT) continue;
                
                BlockPos neighbor = current.relative(direction);
                if (pipeDataHashMap.containsKey(neighbor) && !visited.contains(neighbor)) {
                    stack.push(neighbor);
                }
            }
        }
    }
    public void createSubNetwork(LevelNetworkSavedData savedData, ServerLevel level, List<BlockPos> network) {
        if (network.isEmpty()) return;
        
        // 创建子网络
        AbstractLevelNetwork subNetwork = levelNetworkType.create(UUID.randomUUID());
        if (subNetwork == null) {
            throw new IllegalStateException("Failed to create sub-network of type: " + levelNetworkType);
        }
        
        for (BlockPos pos : network) {
            PipeData pipeData = pipeDataHashMap.get(pos);
            if (pipeData != null) {
                subNetwork.addPipe(level, pos, pipeData);
            }
        }

        // 将子网络添加到保存数据中
        savedData.addNetwork(levelNetworkType, subNetwork);
    }

    public abstract void handleNetworkInput(ServerLevel level);

    public abstract void handleNetworkOutput(ServerLevel level);

    public void tick(ServerLevel level){
        // 计算每个节点的输入 / 输出
        handleNetworkInput(level);
        handleNetworkOutput(level);
        setDirty();
    }

    public void updatePipeData(BlockPos pos, PipeData pipeData) {
        if (pipeDataHashMap.containsKey(pos)) {
            pipeDataHashMap.put(pos, pipeData);
        } else {
            addPipe(pos, pipeData);
        }
        setDirty();
    }

    public UUID getUUID() {
        return uuid;
    }

    public PipeData getPipeData(BlockPos pos) {
        return pipeDataHashMap.get(pos);
    }

    public void addPipe(BlockPos pos, PipeData pipe) {
        pipeDataHashMap.put(pos, pipe);
        setDirty();
    }

    public void addPipe(ServerLevel level, BlockPos pos, PipeData pipe) {
        if (level.getBlockEntity(pos) instanceof PipeBE<?> pipeBE){
            pipeBE.setNetworkUUID(uuid);
            pipeBE.setPipeData(pipe);
            pipeDataHashMap.put(pos, pipe);
        }
        setDirty();
    }

    public void remove(){
        this.pipeDataHashMap.clear();
    }

    public abstract <Cap> LazyOptional<Cap> getCapability(@NotNull Capability<Cap> cap, @Nullable Direction side);

    public void removePipe(LevelNetworkSavedData savedData, ServerLevel level, BlockPos pos) {
        pipeDataHashMap.remove(pos);
        recalculateNetwork(savedData, level);
    }

    public void removePipe(BlockPos pos) {
        pipeDataHashMap.remove(pos);
        setDirty();
    }

    public void clear() {
        pipeDataHashMap.clear();
        setDirty();
    }

    public boolean isEmpty() {
        return pipeDataHashMap.isEmpty();
    }

    @Override
    public String toString() {
        return "AbstractLevelNetwork{" +
                "uuid=" + uuid +
                ", levelNetworkType=" + levelNetworkType +
                ", pipeDataHashMap=" + pipeDataHashMap +
                '}';
    }
}