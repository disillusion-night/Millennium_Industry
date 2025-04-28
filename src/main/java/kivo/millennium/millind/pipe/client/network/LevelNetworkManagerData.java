package kivo.millennium.millind.pipe.client.network;

import kivo.millennium.millind.init.MillenniumLevelNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.block.state.BlockState; // 导入 BlockState
import net.minecraft.world.level.block.Block; // 导入 Block
import kivo.millennium.millind.pipe.client.AbstractPipeBL; // 导入 AbstractPipeBL

import java.util.*;
import java.util.function.Supplier;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.Nullable;

/**
 * Manages and persists network data for a specific Level (dimension).
 * This version correctly handles per-dimension data using SavedData.
 */
public class LevelNetworkManagerData extends SavedData {
    private static final String DATA_NAME = "millind_level_network_manager";

    private final Map<UUID, AbstractLevelNetwork> networksByUuid = new HashMap<>();
    private final Map<String, ArrayList<AbstractLevelNetwork>> networksByType = new HashMap<>();
    // Map to store which network UUID each pipe block position belongs to
    private final Map<BlockPos, UUID> pipePositionToNetworkUuid = new HashMap<>();

    // TODO: Consider a map for node positions if nodes are also identified by position without BEs.
    // private final Map<BlockPos, NetworkTarget> nodePositionToNode = new HashMap<>();


    public LevelNetworkManagerData() {
    }

    // --- Static Factory Method for Loading ---
    public static LevelNetworkManagerData load(CompoundTag tag) {
        LevelNetworkManagerData data = new LevelNetworkManagerData();
        data.loadInternal(tag);
        return data;
    }

    // --- Internal Loading Logic ---
    private void loadInternal(CompoundTag tag) {
        this.networksByUuid.clear();
        this.networksByType.clear();
        this.pipePositionToNetworkUuid.clear();

        if (tag.contains("level_networks", Tag.TAG_COMPOUND)) {
            CompoundTag networkTypesTag = tag.getCompound("level_networks");
            Supplier<IForgeRegistry<MillenniumLevelNetwork.LevelNetworkType<?>>> registrySupplier = MillenniumLevelNetwork.REGISTRY;
            IForgeRegistry<MillenniumLevelNetwork.LevelNetworkType<?>> networkTypeRegistry = registrySupplier.get();

            networkTypesTag.getAllKeys().forEach(networkTypeName -> {
                if (networkTypesTag.contains(networkTypeName, Tag.TAG_LIST)) {
                    ListTag networkListTag = networkTypesTag.getList(networkTypeName, Tag.TAG_COMPOUND);
                    ResourceLocation networkTypeRL = ResourceLocation.tryParse(networkTypeName);
                    if (networkTypeRL == null) {
                        System.err.println("Failed to parse ResourceLocation for network type name during loading: " + networkTypeName);
                        return;
                    }
                    MillenniumLevelNetwork.LevelNetworkType<?> networkType = networkTypeRegistry.getValue(networkTypeRL);

                    if (networkType != null) {
                        ArrayList<AbstractLevelNetwork> networkList = this.networksByType.computeIfAbsent(networkTypeName, k -> new ArrayList<>());

                        networkListTag.forEach(networkTag -> {
                            if (networkTag.getId() == Tag.TAG_COMPOUND) {
                                CompoundTag instanceTag = (CompoundTag) networkTag;
                                AbstractLevelNetwork network = networkType.create();
                                network.load(instanceTag);

                                if (network.getUuid() != null) {
                                    if (!this.networksByUuid.containsKey(network.getUuid())) {
                                        this.networksByUuid.put(network.getUuid(), network);
                                        networkList.add(network);
                                    } else {
                                        System.err.println("Loaded a network with duplicate UUID: " + network.getUuid() + ". Skipping addition for this instance.");
                                    }
                                } else {
                                    System.err.println("Loaded a network of type '" + networkTypeName + "' with a null UUID after loading! Skipping addition to manager maps.");
                                }
                            } else {
                                System.err.println("Unexpected NBT tag type in network list for type '" + networkTypeName + "' during loading: " + networkTag.getId());
                            }
                        });
                    } else {
                        System.err.println("Failed to load networks for type '" + networkTypeName + "'. Type not found in registry during loading.");
                    }
                } else {
                    System.err.println("Expected ListTag for network type '" + networkTypeName + "' during loading, but found different tag type: " + networkTypesTag.get(networkTypeName).getId());
                }
            });
        }
        // Load pipePositionToNetworkUuid map explicitly
        if (tag.contains("pipe_positions", Tag.TAG_LIST)) {
            ListTag pipePositionsList = tag.getList("pipe_positions", Tag.TAG_COMPOUND);
            pipePositionsList.forEach(pipeEntryTag -> {
                if (pipeEntryTag.getId() == Tag.TAG_COMPOUND) {
                    CompoundTag pipeEntry = (CompoundTag) pipeEntryTag;
                    if (pipeEntry.contains("pos", Tag.TAG_LONG) && pipeEntry.contains("uuid", Tag.TAG_STRING)) {
                        BlockPos pos = BlockPos.of(pipeEntry.getLong("pos"));
                        UUID uuid = pipeEntry.getUUID("uuid");
                        // Only add mapping if the network UUID exists (network might have failed to load)
                        if (this.networksByUuid.containsKey(uuid)) {
                            this.pipePositionToNetworkUuid.put(pos, uuid);
                        } else {
                            System.err.println("Loaded pipe position " + pos + " with network UUID " + uuid + " but network was not found! Skipping mapping.");
                        }
                    } else {
                        System.err.println("Invalid data in pipe_positions list tag.");
                    }
                }
            });
        }
    }

    // --- Instance Save Method ---
    @Override
    public CompoundTag save(CompoundTag pCompoundTag) {
        CompoundTag networkTypesTag = new CompoundTag();

        this.networksByType.forEach((String networkTypeName, ArrayList<AbstractLevelNetwork> networkList) -> {
            ListTag networkListTag = new ListTag();
            networkList.forEach(network -> {
                CompoundTag networkTag = new CompoundTag();
                network.save(networkTag);
                networkListTag.add(networkTag);
            });
            networkTypesTag.put(networkTypeName, networkListTag);
        });
        pCompoundTag.put("level_networks", networkTypesTag);

        // Save pipePositionToNetworkUuid map explicitly
        ListTag pipePositionsList = new ListTag();
        this.pipePositionToNetworkUuid.forEach((pos, uuid) -> {
            CompoundTag pipeEntry = new CompoundTag();
            pipeEntry.putLong("pos", pos.asLong());
            pipeEntry.putUUID("uuid", uuid);
            pipePositionsList.add(pipeEntry);
        });
        pCompoundTag.put("pipe_positions", pipePositionsList);


        return pCompoundTag;
    }


    // --- Static Method to Get the Manager Instance for a Level ---
    public static LevelNetworkManagerData get(ServerLevel level) {
        DimensionDataStorage storage = level.getChunkSource().getDataStorage();
        return storage.computeIfAbsent(
                LevelNetworkManagerData::load,
                () -> new LevelNetworkManagerData(),
                DATA_NAME
        );
    }


    // --- Methods for runtime management ---

    /**
     * Add a network instance to the manager.
     * Generates a UUID if the network doesn't have one (for new networks).
     * @param network The network instance to add.
     */
    public void addNetwork(AbstractLevelNetwork network) {
        String typeName = network.getLevelNetworkType().getName();

        if (network.getUuid() == null) {
            network.setUuid(UUID.randomUUID());
            // TODO: Pass level context to print meaningful logs
            System.out.println("Generated new UUID for network: " + network.getUuid() + " of type " + typeName /* + " in level " + level.dimension().location() */);
        }

        if (this.networksByUuid.containsKey(network.getUuid())) {
            System.err.println("Attempted to add network with already existing UUID: " + network.getUuid() + ". Skipping.");
            return;
        }
        this.networksByUuid.put(network.getUuid(), network);
        this.networksByType.computeIfAbsent(typeName, k -> new ArrayList<>()).add(network);

        this.setDirty();
    }

    /**
     * Remove a network instance from the manager.
     * Does NOT remove associated pipe position mappings. Use this after merging.
     * @param network The network instance to remove.
     */
    private void removeNetworkInstance(AbstractLevelNetwork network) {
        if (network == null || network.getUuid() == null) {
            System.err.println("Attempted to remove null or null-UUID network instance.");
            return;
        }
        AbstractLevelNetwork removedByUuid = this.networksByUuid.remove(network.getUuid());
        if (removedByUuid != null) {
            String typeName = removedByUuid.getLevelNetworkType().getName();
            ArrayList<AbstractLevelNetwork> networks = this.networksByType.get(typeName);
            if (networks != null) {
                networks.remove(removedByUuid);
                if (networks.isEmpty()) {
                    this.networksByType.remove(typeName);
                }
                // setDirty() is called by removeNetwork which uses this private method
            } else {
                System.err.println("Network instance with UUID " + network.getUuid() + " found in UUID map but its type list (" + typeName + ") is missing in type map. Data inconsistency!");
            }
        } else {
            System.err.println("Attempted to remove network instance with UUID " + network.getUuid() + " but it was not found in the UUID map.");
        }
    }


    /**
     * Remove a network instance from the manager and clean up associated pipe position mappings.
     * Called when a network is truly gone (e.g., after merging, or complete destruction).
     * @param network The network instance to remove.
     */
    public void removeNetwork(AbstractLevelNetwork network) {
        if (network == null || network.getUuid() == null) {
            System.err.println("Attempted to remove null or null-UUID network.");
            return;
        }
        UUID networkUuid = network.getUuid();

        // Remove associated pipe positions first
        this.pipePositionToNetworkUuid.entrySet().removeIf(entry -> entry.getValue().equals(networkUuid));

        // Then remove the network instance itself from the manager maps
        this.removeNetworkInstance(network);

        this.setDirty();
    }


    /**
     * Get a network by its UUID for this dimension.
     * @param uuid The UUID of the network.
     * @return The network instance, or null if not found in this dimension.
     */
    public AbstractLevelNetwork getNetworkByUuid(UUID uuid) {
        return this.networksByUuid.get(uuid);
    }

    /**
     * Get the network UUID associated with a specific pipe block position.
     * @param pipePos The BlockPos of the pipe.
     * @return The network UUID, or null if no network is associated with this position.
     */
    public UUID getNetworkUuidForPipe(BlockPos pipePos) {
        return this.pipePositionToNetworkUuid.get(pipePos);
    }

    /**
     * Add a mapping from a pipe block position to a network UUID.
     * Called when a pipe joins/creates a network.
     * @param pipePos The BlockPos of the pipe.
     * @param networkUuid The UUID of the network the pipe belongs to.
     */
    public void addPipeToNetwork(BlockPos pipePos, UUID networkUuid) {
        if (this.pipePositionToNetworkUuid.containsKey(pipePos)) {
            // This pipe position is already mapped, indicates a logic error or concurrent modification?
            // Or maybe intentional if a pipe is switching networks (e.g. during merge handled externally?)
            // For now, just overwrite if necessary, but log a warning.
            System.err.println("Attempted to add pipe position " + pipePos + " to network " + networkUuid + ", but it's already associated with network " + this.pipePositionToNetworkUuid.get(pipePos));
        }
        this.pipePositionToNetworkUuid.put(pipePos, networkUuid);
        this.setDirty();
    }

    /**
     * Remove the mapping for a pipe block position.
     * Called when a pipe is removed or leaves a network.
     * @param pipePos The BlockPos of the pipe.
     */
    public void removePipeFromNetwork(BlockPos pipePos) {
        if (this.pipePositionToNetworkUuid.remove(pipePos) != null) {
            this.setDirty();
        } else {
            System.err.println("Attempted to remove pipe position " + pipePos + " from network mapping, but it was not found.");
        }
    }

    /**
     * Merge a secondary network into a primary network.
     * This is a complex operation.
     * @param primaryUuid The UUID of the network to merge into.
     * @param secondaryUuid The UUID of the network to merge from.
     */
    public void mergeNetworks(UUID primaryUuid, UUID secondaryUuid) {
        AbstractLevelNetwork primaryNetwork = this.networksByUuid.get(primaryUuid);
        AbstractLevelNetwork secondaryNetwork = this.networksByUuid.get(secondaryUuid);

        if (primaryNetwork == null || secondaryNetwork == null) {
            System.err.println("Attempted to merge non-existent networks: Primary=" + primaryUuid + ", Secondary=" + secondaryUuid);
            return;
        }
        if (primaryUuid.equals(secondaryUuid)) {
            System.err.println("Attempted to merge a network with itself: " + primaryUuid);
            return;
        }
        if (!primaryNetwork.getLevelNetworkType().equals(secondaryNetwork.getLevelNetworkType())) { // Use equals for type check
            System.err.println("Attempted to merge networks of different types: Primary=" + primaryNetwork.getLevelNetworkType().getName() + ", Secondary=" + secondaryNetwork.getLevelNetworkType().getName());
            return;
        }

        System.out.println("Merging network " + secondaryUuid + " into " + primaryUuid);

        // Step 1: Merge network-specific data (e.g., fluid tanks, energy buffers)
        primaryNetwork.mergeDataFrom(secondaryNetwork);

        // Step 2: Transfer nodes (BlockNetworkTarget and EntityNetworkTarget) from secondary to primary network instance.
        secondaryNetwork.getInputNodes().forEach(primaryNetwork::addInputNode);
        secondaryNetwork.getOutputNodes().forEach(primaryNetwork::addOutputNode);

        // Step 3: Update pipePositionToNetworkUuid for all pipes previously in the secondary network
        // Use entrySet().forEach() to modify the map while iterating
        this.pipePositionToNetworkUuid.entrySet().forEach(entry -> {
            if (entry.getValue().equals(secondaryUuid)) {
                entry.setValue(primaryUuid); // Update the UUID reference
            }
        });

        // Step 4: Remove the secondary network instance from the manager's maps.
        this.removeNetworkInstance(secondaryNetwork); // Use the private method that doesn't clean up pipe mappings

        this.setDirty();
    }


    /**
     * Finds all connected pipes and nodes starting from a given position within a specific network.
     * Performs a Breadth-First Search (BFS).
     * Traverses primarily through pipes, collecting attached nodes.
     *
     * @param level The ServerLevel the network exists in.
     * @param startPipePos The starting BlockPos (must be a pipe in the network).
     * @param originalNetworkUuid The UUID of the network being traversed.
     * @param originalPipePositionsInNetwork Set of all pipe positions initially in the network.
     * @param originalNodesInNetwork Set of all nodes initially in the network.
     * @param removedPipePos The position of the pipe that was removed/disconnected (to avoid traversing through it). Can be null.
     * @param visitedPipesGlobal Set of pipes already visited across ALL components found so far.
     * @param visitedNodesGlobal Set of nodes already visited across ALL components found so far.
     * @return A NetworkComponent containing the pipes and nodes found in this component.
     */
    private NetworkComponent findConnectedComponent(ServerLevel level, BlockPos startPipePos, UUID originalNetworkUuid,
                                                    Set<BlockPos> originalPipePositionsInNetwork, Set<NetworkTarget> originalNodesInNetwork,
                                                    @Nullable BlockPos removedPipePos,
                                                    Set<BlockPos> visitedPipesGlobal, Set<NetworkTarget> visitedNodesGlobal) {

        NetworkComponent currentComponent = new NetworkComponent();
        Queue<BlockPos> pipeQueue = new LinkedList<>();
        // No need for local visited sets, use global ones and check before adding to queue/component

        pipeQueue.add(startPipePos);
        visitedPipesGlobal.add(startPipePos); // Mark as visited globally

        while (!pipeQueue.isEmpty()) {
            BlockPos currentPipePos = pipeQueue.poll();
            currentComponent.addPipePosition(currentPipePos);

            // Check neighbors of the current pipe
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = currentPipePos.relative(direction);

                // Ensure neighbor is loaded
                if (level.isLoaded(neighborPos)) {
                    BlockState neighborState = level.getBlockState(neighborPos);
                    Block neighborBlock = neighborState.getBlock();

                    // --- Check for connected Pipes ---
                    // Check if neighbor is a pipe and was part of the *original* network
                    if (originalPipePositionsInNetwork.contains(neighborPos)) {
                        // Check if it's not the removed pipe's position
                        if (removedPipePos == null || !neighborPos.equals(removedPipePos)) {
                            // Check connectivity between current pipe and neighbor pipe
                            Block currentBlock = level.getBlockState(currentPipePos).getBlock();
                            if (currentBlock instanceof AbstractPipeBL<?> currentPipeBlock && neighborBlock instanceof AbstractPipeBL<?> neighborPipeBlock) {
                                // Assuming canConnectTo checks both directions or is symmetric for pipes
                                if (currentPipeBlock.canConnectTo(level, neighborPos, neighborState, direction) /* && neighborPipeBlock.canConnectTo(level, currentPipePos, level.getBlockState(currentPos), direction.getOpposite())*/) {
                                    // If neighbor is a connectable pipe in the original network and not visited globally
                                    if (!visitedPipesGlobal.contains(neighborPos)) {
                                        pipeQueue.add(neighborPos);
                                        visitedPipesGlobal.add(neighborPos); // Mark as visited globally
                                    }
                                }
                            }
                        }
                    }

                    // --- Check for connected Nodes ---
                    // Check if the neighbor position corresponds to a node in the original network
                    // Iterate originalNodesInNetwork to find a matching BlockNetworkTarget
                    NetworkTarget connectedNode = null;
                    for (NetworkTarget node : originalNodesInNetwork) {
                        if (node instanceof BlockNetworkTarget blockTarget && blockTarget.getPos().equals(neighborPos)) {
                            // Found a BlockNetworkTarget at the neighbor position
                            // Check if the current pipe can connect to this node
                            Block currentBlock = level.getBlockState(currentPipePos).getBlock();
                            if (currentBlock instanceof AbstractPipeBL<?> currentPipeBlock) {
                                if (currentPipeBlock.canConnectTo(level, neighborPos, neighborState, direction)) {
                                    // Found a connected node that was in the original network and not visited globally
                                    if (!visitedNodesGlobal.contains(node)) {
                                        connectedNode = node;
                                        break; // Found the node, no need to check others in originalNodesInNetwork for this position
                                    }
                                }
                            }
                        }
                        // TODO: Handle EntityNetworkTargets connected to pipes (requires knowing entity position/id)
                    }

                    // If a connected node was found and not visited globally
                    if (connectedNode != null) { // Global visited check is inside the loop
                        currentComponent.addNode(connectedNode);
                        visitedNodesGlobal.add(connectedNode); // Mark as visited globally

                        // TODO: If nodes can connect to other pipes/nodes, add connected pipes/nodes
                        // reachable from this node to the queue/list for traversal.
                        // This requires getting connectivity from the node's perspective.
                        // For now, traversal is primarily through pipes.
                    }
                }
            }
        }
        return currentComponent;
    }

    // Define the NetworkComponent inner class
    public static class NetworkComponent {
        private final Set<BlockPos> pipePositions = new HashSet<>();
        private final Set<NetworkTarget> nodes = new HashSet<>();

        public Set<BlockPos> getPipePositions() { return pipePositions; }
        public Set<NetworkTarget> getNodes() { return nodes; }

        public void addPipePosition(BlockPos pos) { pipePositions.add(pos); }
        public void addNode(NetworkTarget node) { nodes.add(node); }

        // Optional: Add methods to get combined list of all members etc.

        @Override
        public String toString() {
            return "Component [Pipes=" + pipePositions.size() + ", Nodes=" + nodes.size() + "]";
        }

        public boolean isEmpty() {
            return pipePositions.isEmpty() && nodes.isEmpty();
        }
    }

    // ... rest of LevelNetworkManagerData
}