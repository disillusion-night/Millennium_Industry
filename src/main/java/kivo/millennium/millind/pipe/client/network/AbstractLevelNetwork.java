package kivo.millennium.millind.pipe.client.network;

import kivo.millennium.millind.init.MillenniumLevelNetwork;
import net.minecraft.core.BlockPos;
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
import java.util.UUID; // 导入 UUID

/**
 * 单维度网络系统的基类。
 */
public abstract class AbstractLevelNetwork {

    private UUID uuid; // 添加 UUID 字段
    private final MillenniumLevelNetwork.LevelNetworkType<?> levelNetworkType;
    //private final CapabilityType capabilityType; // 网络处理的可细分对象类型

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
     * UUID 不在此处生成，而是在加载时或添加到管理器时设置。
     */
    public AbstractLevelNetwork(MillenniumLevelNetwork.LevelNetworkType<?> levelNetworkType) {
        this.levelNetworkType = levelNetworkType;
        // this.capabilityType = capabilityType; // 如果使用了，在这里初始化
    }

    /**
     * 获取此网络的 UUID。
     * @return UUID。如果尚未分配（应在添加到管理器或加载时分配），则可能为 null。
     */
    public UUID getUuid() {
        if (this.uuid == null) {
            // 这表明网络实例在使用前未完全初始化（UUID 为 null）。
            System.err.println("Accessing UUID on a network instance that hasn't been fully initialized (UUID is null)!");
            // 根据所需行为，你可以抛出错误、返回 null 或按需生成（有风险）。
            // 返回 null 或处理错误更安全。
        }
        return uuid;
    }

    /**
     * 设置此网络的 UUID。旨在用于加载或管理器进行初次创建时。
     * 理论上应只调用一次。
     * @param uuid 要设置的 UUID。
     */
    public void setUuid(UUID uuid) {
        if (this.uuid != null && !this.uuid.equals(uuid)) {
            System.err.println("Attempted to change UUID of a network instance from " + this.uuid + " to " + uuid);
        }
        this.uuid = uuid;
    }

    // 添加获取网络类型的方法（对管理器很有用）
    public MillenniumLevelNetwork.LevelNetworkType<?> getLevelNetworkType() {
        return levelNetworkType;
    }


    /**
     * 添加一个输入节点。
     *
     * @param target 输入节点的网络目标。
     */
    public void addInputNode(NetworkTarget target) {
        if (target instanceof BlockNetworkTarget blockTarget) {
            ChunkPos chunkPos = new ChunkPos(blockTarget.getPos());
            if (!this.blockInputNodes.computeIfAbsent(chunkPos, k -> new ArrayList<>()).contains(blockTarget)) {
                this.blockInputNodes.get(chunkPos).add(blockTarget);
                // TODO: 通知 WorldSavedData 数据已更改 (Mark SavedData dirty)
            }
        } else if (target instanceof EntityNetworkTarget entityTarget) {
            if (!this.entityInputNodes.contains(entityTarget)) {
                this.entityInputNodes.add(entityTarget);
                // TODO: 通知 WorldSavedData 数据已更改 (Mark SavedData dirty)
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
            ChunkPos chunkPos = new ChunkPos(blockTarget.getPos());
            List<BlockNetworkTarget> nodesInChunk = this.blockInputNodes.get(chunkPos);
            if (nodesInChunk != null) {
                if (nodesInChunk.remove(blockTarget)) {
                    if (nodesInChunk.isEmpty()) {
                        this.blockInputNodes.remove(chunkPos);
                    }
                    // TODO: 通知 WorldSavedData 数据已更改 (Mark SavedData dirty)
                }
            }
        } else if (target instanceof EntityNetworkTarget entityTarget) {
            if (this.entityInputNodes.remove(entityTarget)) {
                // TODO: 通知 WorldSavedData 数据已更改 (Mark SavedData dirty)
            }
        }
    }

    /**
     * 获取所有输入节点的合并列表 (BlockNetworkTarget 和 EntityNetworkTarget)。
     *
     * @return 所有输入节点的列表。
     */
    public List<NetworkTarget> getInputNodes() {
        List<NetworkTarget> allInputNodes = new ArrayList<>();
        this.blockInputNodes.values().forEach(allInputNodes::addAll);
        allInputNodes.addAll(this.entityInputNodes);
        return allInputNodes;
    }


    /**
     * 添加一个输出节点。
     *
     * @param target 输出节点的网络目标。
     */
    public void addOutputNode(NetworkTarget target) {
        if (target instanceof BlockNetworkTarget blockTarget) {
            ChunkPos chunkPos = new ChunkPos(blockTarget.getPos());
            if (!this.blockOutputNodes.computeIfAbsent(chunkPos, k -> new ArrayList<>()).contains(blockTarget)) {
                this.blockOutputNodes.get(chunkPos).add(blockTarget);
                // TODO: 通知 WorldSavedData 数据已更改 (Mark SavedData dirty)
            }
        } else if (target instanceof EntityNetworkTarget entityTarget) {
            if (!this.entityOutputNodes.contains(entityTarget)) {
                this.entityOutputNodes.add(entityTarget);
                // TODO: 通知 WorldSavedData 数据已更改 (Mark SavedData dirty)
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
            ChunkPos chunkPos = new ChunkPos(blockTarget.getPos());
            List<BlockNetworkTarget> nodesInChunk = this.blockOutputNodes.get(chunkPos);
            if (nodesInChunk != null) {
                if (nodesInChunk.remove(blockTarget)) {
                    if (nodesInChunk.isEmpty()) {
                        this.blockOutputNodes.remove(chunkPos);
                    }
                    // TODO: 通知 WorldSavedData 数据已更改 (Mark SavedData dirty)
                }
            }
        } else if (target instanceof EntityNetworkTarget entityTarget) {
            if (this.entityOutputNodes.remove(entityTarget)) {
                // TODO: 通知 WorldSavedData 数据已更改 (Mark SavedData dirty)
            }
        }
    }

    /**
     * 获取所有输出节点的合并列表 (BlockNetworkTarget 和 EntityNetworkTarget)。
     *
     * @return 所有输出节点的列表。
     */
    public List<NetworkTarget> getOutputNodes() {
        List<NetworkTarget> allOutputNodes = new ArrayList<>();
        this.blockOutputNodes.values().forEach(allOutputNodes::addAll);
        allOutputNodes.addAll(this.entityOutputNodes);
        return allOutputNodes;
    }

    /**
     * 获取指定区块中的输入方块实体节点列表。
     *
     * @param chunkPos 区块坐标。
     * @return 该区块中的输入方块实体节点列表的新副本，如果没有则返回新的空列表。
     */
    public List<BlockNetworkTarget> getBlockInputNodes(ChunkPos chunkPos) {
        return new ArrayList<>(this.blockInputNodes.getOrDefault(chunkPos, new ArrayList<>())); // 返回副本
    }

    /**
     * 获取指定区块中的输出方块实体节点列表。
     *
     * @param chunkPos 区块坐标。
     * @return 该区块中的输出方块实体节点列表的新副本，如果没有则返回新的空列表。
     */
    public List<BlockNetworkTarget> getBlockOutputNodes(ChunkPos chunkPos) {
        return new ArrayList<>(this.blockOutputNodes.getOrDefault(chunkPos, new ArrayList<>())); // 返回副本
    }

    /**
     * 获取所有的输入实体节点列表。
     *
     * @return 输入实体节点列表的新副本。
     */
    public List<EntityNetworkTarget> getEntityInputNodes() {
        return new ArrayList<>(this.entityInputNodes); // 返回副本
    }

    /**
     * 获取所有的输出实体节点列表。
     *
     * @return 输出实体节点列表的新副本。
     */
    public List<EntityNetworkTarget> getEntityOutputNodes() {
        return new ArrayList<>(this.entityOutputNodes); // 返回副本
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
     * Gets a combined list of all block input nodes across all chunks.
     * @return A new list containing all block input nodes.
     */
    public List<BlockNetworkTarget> getAllBlockInputNodes() {
        List<BlockNetworkTarget> allNodes = new ArrayList<>();
        this.blockInputNodes.values().forEach(allNodes::addAll);
        return allNodes;
    }

    /**
     * Gets a combined list of all block output nodes across all chunks.
     * @return A new list containing all block output nodes.
     */
    public List<BlockNetworkTarget> getAllBlockOutputNodes() {
        List<BlockNetworkTarget> allNodes = new ArrayList<>();
        this.blockOutputNodes.values().forEach(allNodes::addAll);
        return allNodes;
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
     * 处理网络的每个 Tick 逻辑。
     *
     * @param level 网络所在的 Level。
     */
    public abstract void handleTick(ServerLevel level);


    /**
     * 将网络数据保存到 CompoundTag。
     * 保存 UUID、节点列表，并调用 saveNetworkData。
     *
     * @param tag 要保存到的 CompoundTag。
     * @return 保存后的 CompoundTag。
     */
    public CompoundTag save(CompoundTag tag) {
        // 保存 UUID
        if (this.uuid != null) {
            tag.putUUID("uuid", this.uuid);
        } else {
            // 这表明网络实例在没有 UUID 的情况下被保存。
            // 如果网络被管理器正确管理，理论上不应该发生这种情况。
            System.err.println("Saving AbstractLevelNetwork with null UUID! Network Type: " + this.levelNetworkType.getName());
            // 根据 UUID 的重要性，你可以抛出错误或以其他方式处理。
            // 目前，我们只打印错误，并保存 null，这会导致加载时生成新的 UUID。
        }

        // 保存方块实体输入节点
        CompoundTag blockInputNodesTag = new CompoundTag();
        this.blockInputNodes.forEach((chunkPos, nodes) -> {
            ListTag nodeListTag = new ListTag();
            nodes.forEach(node -> nodeListTag.add(node.save(new CompoundTag())));
            blockInputNodesTag.put(String.valueOf(chunkPos.toLong()), nodeListTag);
        });
        tag.put("block_input_nodes", blockInputNodesTag);

        // 保存方块实体输出节点
        CompoundTag blockOutputNodesTag = new CompoundTag();
        this.blockOutputNodes.forEach((chunkPos, nodes) -> {
            ListTag nodeListTag = new ListTag();
            nodes.forEach(node -> nodeListTag.add(node.save(new CompoundTag())));
            blockOutputNodesTag.put(String.valueOf(chunkPos.toLong()), nodeListTag);
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

        // 保存子类特有的数据
        saveNetworkData(tag);

        return tag;
    }

    /**
     * 从 CompoundTag 加载网络数据。
     * 加载 UUID、节点列表，并调用 loadNetworkData。
     *
     * @param tag 要加载的 CompoundTag。
     */
    public void load(CompoundTag tag) {
        // 加载 UUID
        if (tag.hasUUID("uuid")) {
            this.uuid = tag.getUUID("uuid");
        } else {
            // 这表明保存的数据中缺少 UUID（例如，加载旧存档或错误）。
            // 这种情况下生成新的 UUID。
            System.err.println("Loading AbstractLevelNetwork with missing UUID! Generating new one. Network Type: " + this.levelNetworkType.getName());
            this.uuid = UUID.randomUUID();
        }

        // 加载方块实体输入节点
        this.blockInputNodes.clear(); // 加载前清空以防止重复
        CompoundTag blockInputNodesTag = tag.getCompound("block_input_nodes");
        blockInputNodesTag.getAllKeys().forEach(key -> {
            long chunkPosLong = Long.parseLong(key);
            ChunkPos chunkPos = new ChunkPos(chunkPosLong);
            ListTag nodeListTag = blockInputNodesTag.getList(key, Tag.TAG_COMPOUND);
            List<BlockNetworkTarget> nodesInChunk = this.blockInputNodes.computeIfAbsent(chunkPos, k -> new ArrayList<>());
            nodeListTag.forEach(nodeTag -> {
                NetworkTarget target = NetworkTarget.fromNbt((CompoundTag)nodeTag);
                if (target instanceof BlockNetworkTarget blockTarget) {
                    nodesInChunk.add(blockTarget);
                } else {
                    // 如果加载的目标不是预期的 BlockNetworkTarget，则记录错误。
                    System.err.println("Loaded unexpected NetworkTarget type in block input nodes: " + (target != null ? target.getClass().getName() : "null"));
                }
            });
        });

        // 加载方块实体输出节点
        this.blockOutputNodes.clear(); // 加载前清空
        CompoundTag blockOutputNodesTag = tag.getCompound("block_output_nodes");
        blockOutputNodesTag.getAllKeys().forEach(key -> {
            long chunkPosLong = Long.parseLong(key);
            ChunkPos chunkPos = new ChunkPos(chunkPosLong);
            ListTag nodeListTag = blockOutputNodesTag.getList(key, Tag.TAG_COMPOUND);
            List<BlockNetworkTarget> nodesInChunk = this.blockOutputNodes.computeIfAbsent(chunkPos, k -> new ArrayList<>());
            nodeListTag.forEach(nodeTag -> {
                NetworkTarget target = NetworkTarget.fromNbt((CompoundTag)nodeTag);
                if (target instanceof BlockNetworkTarget blockTarget) {
                    nodesInChunk.add(blockTarget);
                } else {
                    System.err.println("Loaded unexpected NetworkTarget type in block output nodes: " + (target != null ? target.getClass().getName() : "null"));
                }
            });
        });

        // 加载实体输入节点
        this.entityInputNodes.clear(); // 加载前清空
        ListTag entityInputListTag = tag.getList("entity_input_nodes", Tag.TAG_COMPOUND);
        entityInputListTag.forEach(nodeTag -> {
            NetworkTarget target = NetworkTarget.fromNbt((CompoundTag)nodeTag);
            if (target instanceof EntityNetworkTarget entityTarget) {
                this.entityInputNodes.add(entityTarget);
            } else {
                System.err.println("Loaded unexpected NetworkTarget type in entity input nodes: " + (target != null ? target.getClass().getName() : "null"));
            }
        });

        // 加载实体输出节点
        this.entityOutputNodes.clear(); // 加载前清空
        ListTag entityOutputListTag = tag.getList("entity_output_nodes", Tag.TAG_COMPOUND);
        entityOutputListTag.forEach(nodeTag -> {
            NetworkTarget target = NetworkTarget.fromNbt((CompoundTag)nodeTag);
            if (target instanceof EntityNetworkTarget entityTarget) {
                this.entityOutputNodes.add(entityTarget);
            } else {
                System.err.println("Loaded unexpected NetworkTarget type in entity output nodes: " + (target != null ? target.getClass().getName() : "null"));
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