package kivo.millennium.millind.pipe.client.network;

import kivo.millennium.millind.capability.CapabilityType;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.HashSet;


/**
 * 单维度网络系统的基类。
 */
public abstract class AbstractLevelNetwork {

    private final CapabilityType capabilityType; // 网络处理的可细分对象类型

    // 方块实体输入节点，按区块分组
    private final Map<ChunkPos, List<BlockNetworkTarget>> blockInputNodes = new HashMap<>();
    // 方块实体输出节点，按区块分组
    private final Map<ChunkPos, List<BlockNetworkTarget>> blockOutputNodes = new HashMap<>();

    // 实体输入节点
    private final List<EntityNetworkTarget> entityInputNodes = new ArrayList<>();
    // 实体输出节点
    private final List<EntityNetworkTarget> entityOutputNodes = new ArrayList<>();


    /**
     * 单维度网络系统的基类构造函数。
     *
     * @param capabilityType 网络处理的可细分对象类型。
     */
    public AbstractLevelNetwork(CapabilityType capabilityType) {
        this.capabilityType = capabilityType;
    }

    /**
     * 获取网络是否为全局可见（跨维度传输）。单维度网络固定为 false。
     *
     * @return 总是 false。
     */
    public final boolean isGlobal() {
        return false;
    }

    /**
     * 获取网络处理的可细分对象类型。
     *
     * @return 网络的 CapabilityType。
     */
    public final CapabilityType getCapabilityType() {
        return capabilityType;
    }

    /**
     * 添加一个输入节点。
     *
     * @param target 输入节点的网络目标。
     */
    public void addInputNode(NetworkTarget target) {
        if (target instanceof BlockNetworkTarget blockTarget) {
            // 使用原版 ChunkPos 从 BlockPos 转换
            ChunkPos chunkPos = new ChunkPos(blockTarget.getPos());
            // 检查是否已存在，避免重复添加
            if (!this.blockInputNodes.computeIfAbsent(chunkPos, k -> new ArrayList<>()).contains(blockTarget)) {
                this.blockInputNodes.get(chunkPos).add(blockTarget);
                // TODO: 通知 WorldSavedData 数据已更改
            }
        } else if (target instanceof EntityNetworkTarget entityTarget) {
            if (!this.entityInputNodes.contains(entityTarget)) {
                this.entityInputNodes.add(entityTarget);
                // TODO: 通知 WorldSavedData 数据已更改
            }
        }
    }

    /**
     * 移除一个输入节点。
     *
     * @param target 要移除的输入节点的网络目标。
     */
    public void removeInputNode(NetworkTarget target) {
        if (target instanceof BlockNetworkTarget blockTarget) {
            // 使用原版 ChunkPos 从 BlockPos 转换
            ChunkPos chunkPos = new ChunkPos(blockTarget.getPos());
            List<BlockNetworkTarget> nodesInChunk = this.blockInputNodes.get(chunkPos);
            if (nodesInChunk != null) {
                if (nodesInChunk.remove(blockTarget)) {
                    if (nodesInChunk.isEmpty()) {
                        this.blockInputNodes.remove(chunkPos); // 如果区块列表为空，移除 ChunkPos
                    }
                    // TODO: 通知 WorldSavedData 数据已更改
                }
            }
        } else if (target instanceof EntityNetworkTarget entityTarget) {
            if (this.entityInputNodes.remove(entityTarget)) {
                // TODO: 通知 WorldSavedData 数据已更改
            }
        }
    }

    /**
     * 获取所有输入节点的合并列表 (BlockNetworkTarget 和 EntityNetworkTarget)。
     *
     * @return 所有输入节点的列表。
     */
    public List<NetworkTarget> getInputNodes() {
        // 返回所有输入节点的合并列表 (BlockNetworkTarget 和 EntityNetworkTarget)
        List<NetworkTarget> allInputNodes = new ArrayList<>();
        this.blockInputNodes.values().forEach(allInputNodes::addAll); // 添加所有 BlockNetworkTarget
        allInputNodes.addAll(this.entityInputNodes); // 添加所有 EntityNetworkTarget
        return allInputNodes;
    }


    /**
     * 添加一个输出节点。
     *
     * @param target 输出节点的网络目标。
     */
    public void addOutputNode(NetworkTarget target) {
        if (target instanceof BlockNetworkTarget blockTarget) {
            // 使用原版 ChunkPos 从 BlockPos 转换
            ChunkPos chunkPos = new ChunkPos(blockTarget.getPos());
            // 检查是否已存在，避免重复添加
            if (!this.blockOutputNodes.computeIfAbsent(chunkPos, k -> new ArrayList<>()).contains(blockTarget)) {
                this.blockOutputNodes.get(chunkPos).add(blockTarget);
                // TODO: 通知 WorldSavedData 数据已更改
            }
        } else if (target instanceof EntityNetworkTarget entityTarget) {
            if (!this.entityOutputNodes.contains(entityTarget)) {
                this.entityOutputNodes.add(entityTarget);
                // TODO: 通知 WorldSavedData 数据已更改
            }
        }
    }

    /**
     * 移除一个输出节点。
     *
     * @param target 要移除的输出节点的网络目标。
     */
    public void removeOutputNode(NetworkTarget target) {
        if (target instanceof BlockNetworkTarget blockTarget) {
            // 使用原版 ChunkPos 从 BlockPos 转换
            ChunkPos chunkPos = new ChunkPos(blockTarget.getPos());
            List<BlockNetworkTarget> nodesInChunk = this.blockOutputNodes.get(chunkPos);
            if (nodesInChunk != null) {
                // 使用迭代器或 stream 来安全移除
                if (nodesInChunk.remove(blockTarget)) {
                    if (nodesInChunk.isEmpty()) {
                        this.blockOutputNodes.remove(chunkPos); // 如果区块列表为空，移除 ChunkPos
                    }
                    // TODO: 通知 WorldSavedData 数据已更改
                }
            }
        } else if (target instanceof EntityNetworkTarget entityTarget) {
            if (this.entityOutputNodes.remove(entityTarget)) {
                // TODO: 通知 WorldSavedData 数据已更改
            }
        }
    }

    /**
     * 获取所有输出节点的合并列表 (BlockNetworkTarget 和 EntityNetworkTarget)。
     *
     * @return 所有输出节点的列表。
     */
    public List<NetworkTarget> getOutputNodes() {
        // 返回所有输出节点的合并列表 (BlockNetworkTarget 和 EntityNetworkTarget)
        List<NetworkTarget> allOutputNodes = new ArrayList<>();
        this.blockOutputNodes.values().forEach(allOutputNodes::addAll); // 添加所有 BlockNetworkTarget
        allOutputNodes.addAll(this.entityOutputNodes); // 添加所有 EntityNetworkTarget
        return allOutputNodes;
    }

    /**
     * 获取指定区块中的输入方块实体节点列表。
     *
     * @param chunkPos 区块坐标。
     * @return 该区块中的输入方块实体节点列表的新副本，如果没有则返回新的空列表。
     */
    public List<BlockNetworkTarget> getBlockInputNodes(ChunkPos chunkPos) {
        return this.blockInputNodes.getOrDefault(chunkPos, new ArrayList<>()); // 返回新列表避免外部修改内部 Map
    }

    /**
     * 获取指定区块中的输出方块实体节点列表。
     *
     * @param chunkPos 区块坐标。
     * @return 该区块中的输出方块实体节点列表的新副本，如果没有则返回新的空列表。
     */
    public List<BlockNetworkTarget> getBlockOutputNodes(ChunkPos chunkPos) {
        return this.blockOutputNodes.getOrDefault(chunkPos, new ArrayList<>()); // 返回新列表避免外部修改内部 Map
    }

    /**
     * 获取所有的输入实体节点列表。
     *
     * @return 输入实体节点列表的新副本。
     */
    public List<EntityNetworkTarget> getEntityInputNodes() {
        return new ArrayList<>(this.entityInputNodes); // 返回新列表避免外部修改内部列表
    }

    /**
     * 获取所有的输出实体节点列表。
     *
     * @return 输出实体节点列表的新副本。
     */
    public List<EntityNetworkTarget> getEntityOutputNodes() {
        return new ArrayList<>(this.entityOutputNodes); // 返回新列表避免外部修改内部列表
    }

    /**
     * 获取所有包含节点的区块坐标。
     *
     * @return 所有包含节点的区块坐标集合。
     */
    public Set<ChunkPos> getContainedChunks() {
        Set<ChunkPos> containedChunks = new HashSet<>(this.blockInputNodes.keySet());
        containedChunks.addAll(this.blockOutputNodes.keySet());
        return containedChunks;
    }


    /**
     * 获取网络提供的 Capability。
     * 具体的 Capability 逻辑在子类中实现。
     *
     * @param cap  要获取的 Capability。
     * @param side 可能相关的方向。
     * @return 一个包含 Capability 实例的 LazyOptional，如果不支持该 Capability 则为空 LazyOptional。
     */
    @NotNull
    public abstract <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side);

    /**
     * 获取网络的唯一类型标识符，用于保存和加载。
     * 这个标识符是其注册名 (ResourceLocation 的路径)。
     *
     * @return 网络的唯一类型标识符字符串。
     */
    public abstract String getTypeIdentifier();

    /**
     * 处理网络的每个 Tick 逻辑。
     *
     * @param level 网络所在的 Level。
     */
    public abstract void handleTick(ServerLevel level);


    /**
     * 将网络数据保存到 CompoundTag。
     *
     * @param tag 要保存到的 CompoundTag。
     * @return 保存后的 CompoundTag。
     */
    public CompoundTag save(CompoundTag tag) {
        // isGlobal 固定为 false，不需要保存
        tag.putString("capability_type", this.capabilityType.name()); // 保存 CapabilityType
        tag.putString("type_identifier", getTypeIdentifier()); // 保存网络类型标识符

        // 保存方块实体输入节点
        CompoundTag blockInputNodesTag = new CompoundTag();
        this.blockInputNodes.forEach((chunkPos, nodes) -> {
            ListTag nodeListTag = new ListTag();
            nodes.forEach(node -> nodeListTag.add(node.save(new CompoundTag())));
            // 原版 ChunkPos 可以直接保存为 Long
            blockInputNodesTag.put(String.valueOf(chunkPos.toLong()), nodeListTag); // 使用 ChunkPos 的 Long 表示作为 key
        });
        tag.put("block_input_nodes", blockInputNodesTag);

        // 保存方块实体输出节点
        CompoundTag blockOutputNodesTag = new CompoundTag();
        this.blockOutputNodes.forEach((chunkPos, nodes) -> {
            ListTag nodeListTag = new ListTag();
            nodes.forEach(node -> nodeListTag.add(node.save(new CompoundTag())));
            // 原版 ChunkPos 可以直接保存为 Long
            blockOutputNodesTag.put(String.valueOf(chunkPos.toLong()), nodeListTag); // 使用 ChunkPos 的 Long 表示作为 key
        });
        tag.put("block_output_nodes", blockOutputNodesTag);


        // 保存实体输入节点
        ListTag entityInputListTag = new ListTag();
        this.entityInputNodes.forEach(node -> entityInputListTag.add(node.save(new CompoundTag())));
        tag.put("entity_input_nodes", entityInputListTag);

        // 保存实体输出节点
        ListTag entityOutputListTag = new ListTag();
        this.entityOutputNodes.forEach(node -> entityOutputListTag.add(node.save(new CompoundTag())));
        tag.put("entity_output_nodes", entityOutputListTag);

        // TODO: 保存其他单维度网络特有的数据

        // 返回完整的 tag
        return tag;
    }

    /**
     * 从 CompoundTag 加载网络数据。
     *
     * @param tag 要加载的 CompoundTag。
     */
    public void load(CompoundTag tag) {
        // capabilityType 在构造方法中设置，不需要从 tag 加载
        // type_identifier 也不需要加载，它用于创建网络实例

        // 加载方块实体输入节点
        CompoundTag blockInputNodesTag = tag.getCompound("block_input_nodes");
        blockInputNodesTag.getAllKeys().forEach(key -> {
            // 原版 ChunkPos 从 Long 表示加载
            long chunkPosLong = Long.parseLong(key);
            ChunkPos chunkPos = new ChunkPos(chunkPosLong);
            ListTag nodeListTag = blockInputNodesTag.getList(key, Tag.TAG_COMPOUND); // Tag.TAG_COMPOUND 表示列表包含 CompoundTag
            List<BlockNetworkTarget> nodesInChunk = this.blockInputNodes.computeIfAbsent(chunkPos, k -> new ArrayList<>());
            nodeListTag.forEach(nodeTag -> {
                NetworkTarget target = NetworkTarget.fromNbt((CompoundTag)nodeTag);
                if (target instanceof BlockNetworkTarget) {
                    nodesInChunk.add((BlockNetworkTarget)target);
                }
            });
        });


        // 加载方块实体输出节点
        CompoundTag blockOutputNodesTag = tag.getCompound("block_output_nodes");
        blockOutputNodesTag.getAllKeys().forEach(key -> {
            // 原版 ChunkPos 从 Long 表示加载
            long chunkPosLong = Long.parseLong(key);
            ChunkPos chunkPos = new ChunkPos(chunkPosLong);
            ListTag nodeListTag = blockOutputNodesTag.getList(key, Tag.TAG_COMPOUND);
            List<BlockNetworkTarget> nodesInChunk = this.blockOutputNodes.computeIfAbsent(chunkPos, k -> new ArrayList<>());
            nodeListTag.forEach(nodeTag -> {
                NetworkTarget target = NetworkTarget.fromNbt((CompoundTag)nodeTag);
                if (target instanceof BlockNetworkTarget) {
                    nodesInChunk.add((BlockNetworkTarget)target);
                }
            });
        });

        // 加载实体输入节点
        ListTag entityInputListTag = tag.getList("entity_input_nodes", Tag.TAG_COMPOUND);
        entityInputListTag.forEach(nodeTag -> {
            NetworkTarget target = NetworkTarget.fromNbt((CompoundTag)nodeTag);
            if (target instanceof EntityNetworkTarget) {
                this.entityInputNodes.add((EntityNetworkTarget)target);
            }
        });

        // 加载实体输出节点
        ListTag entityOutputListTag = tag.getList("entity_output_nodes", Tag.TAG_COMPOUND);
        entityOutputListTag.forEach(nodeTag -> {
            NetworkTarget target = NetworkTarget.fromNbt((CompoundTag)nodeTag);
            if (target instanceof EntityNetworkTarget) {
                this.entityOutputNodes.add((EntityNetworkTarget)target);
            }
        });


        // 加载子类特有的数据
        loadNetworkData(tag);
    }

    /**
     * 将子类特有的网络数据保存到 CompoundTag。
     * 由子类实现。
     *
     * @param tag 要保存到的 CompoundTag。
     */
    protected abstract void saveNetworkData(CompoundTag tag);

    /**
     * 从 CompoundTag 加载子类特有的网络数据。
     * 由子类实现。
     *
     * @param tag 要加载的 CompoundTag。
     */
    protected abstract void loadNetworkData(CompoundTag tag);

    // TODO: 添加其他单维度网络特有的方法或逻辑
}