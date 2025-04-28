package kivo.millennium.millind.pipe.client;

import kivo.millennium.millind.Main;
import kivo.millennium.millind.block.device.AbstractMachineBE;
import kivo.millennium.millind.init.MillenniumItems;
import kivo.millennium.millind.init.MillenniumLevelNetwork;
import kivo.millennium.millind.item.Wrench;
import kivo.millennium.millind.pipe.client.network.AbstractLevelNetwork;
import kivo.millennium.millind.pipe.client.network.BlockNetworkTarget;
import kivo.millennium.millind.pipe.client.network.LevelNetworkManagerData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel; // Import ServerLevel
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.ScheduledTick;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


import static kivo.millennium.millind.pipe.client.EPipeState.*;

public abstract class AbstractPipeBL<T extends AbstractLevelNetwork> extends Block implements SimpleWaterloggedBlock {

    // 连接状态属性
    public static final EnumProperty<EPipeState> NORTH = EnumProperty.create("north", EPipeState.class);
    public static final EnumProperty<EPipeState> EAST = EnumProperty.create("east", EPipeState.class);
    public static final EnumProperty<EPipeState> SOUTH = EnumProperty.create("south", EPipeState.class);
    public static final EnumProperty<EPipeState> WEST = EnumProperty.create("west", EPipeState.class);
    public static final EnumProperty<EPipeState> UP = EnumProperty.create("up", EPipeState.class);
    public static final EnumProperty<EPipeState> DOWN = EnumProperty.create("down", EPipeState.class);

    // 含水状态属性
    public static final BooleanProperty WATERLOGGED = BooleanProperty.create("waterlogged");

    public AbstractPipeBL(Properties properties) {
        super(properties.noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, NONE)
                .setValue(EAST, NONE)
                .setValue(SOUTH, NONE)
                .setValue(WEST, NONE)
                .setValue(UP, NONE)
                .setValue(DOWN, NONE)
                .setValue(WATERLOGGED, false));
    }

    public abstract double getDefaultWidth();

    /**
     * Gets the network type associated with this specific pipe block type.
     * This is needed to create the correct network instance when a new network is formed.
     */
    public abstract MillenniumLevelNetwork.LevelNetworkType<T> getAssociatedNetworkType();


    public boolean canConnectTo(BlockGetter level, BlockPos neighborPos, BlockState neighborState, Direction facing) {
        if(neighborState.isAir()) return false;
        if (neighborState.getBlock() instanceof AbstractPipeBL){
            // Check if the neighbor pipe is explicitly disconnected on this face (using wrench etc.)
            // Assuming DISCONNECTED state prevents network connection on that face.
            if(neighborState.hasProperty(getPropertyForDirection(facing.getOpposite())) && neighborState.getValue(getPropertyForDirection(facing.getOpposite())).equals(EPipeState.DISCONNECTED)) return false;
            else return isSamePipe(neighborState.getBlock()); // Pipes only connect to pipes of the same type
        } else {
            return connectionTest(level, neighborPos, neighborState, facing);
        }
    }

    protected abstract boolean connectionTest(BlockGetter level, BlockPos pos, BlockState state, Direction facing);

    protected boolean isPipe(BlockState state) { // Modified to take BlockState directly
        return state.getBlock() instanceof AbstractPipeBL;
    }

    // This method determines the visual state, but the actual network connection status
    // might be influenced by the network manager state as well.
    protected EPipeState getPipeStateForNeighbor(BlockGetter level, BlockPos pos, Direction facing) {

        BlockState currentState = level.getBlockState(pos);

        // If the current pipe is explicitly disconnected on this face, the state is DISCONNECTED
        if (currentState.hasProperty(getPropertyForDirection(facing)) && currentState.getValue(getPropertyForDirection(facing)) == DISCONNECTED) {
            return EPipeState.DISCONNECTED;
        }

        BlockPos neighborPos = pos.relative(facing);
        BlockState neighborState = level.getBlockState(neighborPos);


        if (canConnectTo(level, neighborPos, neighborState, facing)) {
            // Check if the neighbor is a pipe of the same type or a connectable node
            if (isPipe(neighborState) && isSamePipe(neighborState.getBlock())) {
                // If connecting to another pipe of the same type, show CONNECT
                return EPipeState.CONNECT;
            } else if (connectionTest(level, neighborPos, neighborState, facing)) {
                // If connecting to a non-pipe, check if it's an insert/output node type
                // This part depends on how you identify insert/output nodes.
                // For now, we'll just assume CONNECT for generic non-pipe connections
                // TODO: Implement logic to determine INSERT or OUTPUT state for nodes
                // This might involve checking neighbor capabilities or a specific node property/interface
                // Example (simplified):
                // LazyOptional<?> capability = neighborState.getCapability(ForgeCapabilities.SOME_CAP, facing.getOpposite());
                // if (capability.isPresent() && capability.resolve().get() instanceof YourEnergyInputHandler) return EPipeState.INSERT;
                // if (capability.isPresent() && capability.resolve().get() instanceof YourFluidOutputHandler) return EPipeState.OUTPUT;

                // If it's a connectable non-pipe, default to CONNECT for now
                return EPipeState.CONNECT;

            }
        }
        // If cannot connect, or the neighbor is air/disconnected/different pipe type
        return EPipeState.NONE;
    }

    /**
     * Called by BlockItem after this block has been placed.
     * This is where we trigger the network connection logic on the server side.
     */
    @Override
    public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        // onBlockAdded is generally preferred for logic that happens once when the block is placed
        // after neighbors exist. setPlacedBy is called earlier.

        // Ensure this logic only runs on the server side
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            // The state has been set by the BlockItem, but neighbors might not have fully updated yet.
            // The state calculation here relies on neighbors. It might be more robust to schedule
            // a tick or rely on onBlockAdded (which is called after setPlacedBy and neighbor updates).
            // However, following the user's request to use setPlacedBy:

            LevelNetworkManagerData manager = LevelNetworkManagerData.get(serverLevel);

            Set<UUID> neighborNetworkUUIDs = new HashSet<>();
            List<BlockPos> neighborPipePositions = new ArrayList<>();
            // TODO: Track connectable non-pipe node positions and potentially their NetworkTarget instances

            // Step 1: Discover nearby networks and neighbor pipes/nodes
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = pos.relative(direction);
                BlockState neighborState = serverLevel.getBlockState(neighborPos); // Use serverLevel

                // Check if it's a connectable pipe of the same type
                if (neighborState.getBlock() instanceof AbstractPipeBL neighborPipe && isSamePipe(neighborState.getBlock())) {
                    // Check if the neighbor pipe is not explicitly disconnected on this face
                    if (!neighborState.hasProperty(getPropertyForDirection(direction.getOpposite())) || neighborState.getValue(getPropertyForDirection(direction.getOpposite())) != EPipeState.DISCONNECTED) {
                        // Query the manager for the network UUID of the neighbor pipe's position
                        UUID neighborNetworkUuid = manager.getNetworkUuidForPipe(neighborPos);
                        if (neighborNetworkUuid != null) {
                            neighborNetworkUUIDs.add(neighborNetworkUuid);
                        }
                        neighborPipePositions.add(neighborPos);
                    }
                }
                // TODO: Check for connectable non-pipe nodes (like machines with capabilities)
                // If connectionTest passes, and the neighbor block is a potential node,
                // determine if it's already associated with a network or needs a new NetworkTarget.
                // Add their network UUIDs to neighborNetworkUUIDs if applicable,
                // and track the node positions/targets to add to the network later.
                // This part is complex and depends on your node identification strategy.
            }

            // Step 2: Decide whether to create a new network, join one, or merge
            UUID networkToJoinOrCreate = null;

            if (neighborNetworkUUIDs.isEmpty()) {
                // No connected networks found, create a new one
                MillenniumLevelNetwork.LevelNetworkType<T> networkType = getAssociatedNetworkType();
                AbstractLevelNetwork newNetwork = networkType.create();
                // UUID is generated in manager.addNetwork

                // Add the new network to the manager
                manager.addNetwork(newNetwork);
                networkToJoinOrCreate = newNetwork.getUuid();

                System.out.println("Pipe placed at " + pos.toShortString() + " creating new network: " + networkToJoinOrCreate);


            } else if (neighborNetworkUUIDs.size() == 1) {
                // Join an existing network
                networkToJoinOrCreate = neighborNetworkUUIDs.iterator().next();
                System.out.println("Pipe placed at " + pos.toShortString() + " joining existing network: " + networkToJoinOrCreate);

            } else {
                // Merge multiple networks
                // Choose one network as the primary (e.g., the first one found)
                UUID primaryNetworkUuid = neighborNetworkUUIDs.iterator().next();
                System.out.println("Pipe placed at " + pos.toShortString() + " triggering merge into network: " + primaryNetworkUuid + " from " + neighborNetworkUUIDs.size() + " networks.");

                // Merge all other networks into the primary network
                for (UUID secondaryUuid : neighborNetworkUUIDs) {
                    if (!secondaryUuid.equals(primaryNetworkUuid)) {
                        // Manager handles the merge logic and removes the secondary network
                        manager.mergeNetworks(primaryNetworkUuid, secondaryUuid);
                    }
                }
                networkToJoinOrCreate = primaryNetworkUuid;
            }

            // Step 3: Add the placed pipe and connected nodes to the chosen network
            if (networkToJoinOrCreate != null) {
                // Add the new pipe's position to the manager's mapping for the chosen network
                manager.addPipeToNetwork(pos, networkToJoinOrCreate);

                // TODO: Create NetworkTarget instances for connected non-pipe nodes (if any)
                // and add them to the network instance via the manager.
                // This will require getting the network instance using getNetworkByUuid(networkToJoinOrCreate)
                // and then calling a method like network.addNode(nodeTarget) and manager.addNodeToNetwork(networkToJoinOrCreate, nodeTarget)
            }


            // Step 4: Trigger state updates for the placed pipe and its neighbors
            // Call updateShape directly might not be ideal immediately after placement
            // as neighbor updates might still be propagating.
            // A scheduled tick might be more reliable for state recalculation.
            // However, for immediate visual feedback and following the pattern:

            // Notify neighbors that this block has been placed/changed
            serverLevel.updateNeighborsAt(pos, this);
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = pos.relative(direction);
                // Ensure the neighbor block is loaded before notifying
                if (serverLevel.isLoaded(neighborPos)) {
                    serverLevel.updateNeighborsAt(neighborPos, this); // Notify the neighbor block
                }
            }

            // Also recalculate the state for the placed block itself
            BlockState updatedState = calculateStateWhenUpdate(serverLevel, pos, serverLevel.getBlockState(pos));
            if (updatedState != serverLevel.getBlockState(pos)) {
                serverLevel.setBlock(pos, updatedState, 3); // Update the block state on the server
            }


            // setDirty is already called within manager methods that modify data
        }
    }

    // Removed onBlockAdded override


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED);
    }

    @Nonnull
    @Override
    public BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction direction, @Nonnull BlockState neighbourState, @Nonnull LevelAccessor world, @Nonnull BlockPos current, @Nonnull BlockPos offset) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(current, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        // Recalculate state based on neighbors, including potential network connection status
        // Note: calculateStateWhenUpdate relies on getPipeStateForNeighbor which currently
        // doesn't directly query the network manager. If state needs to reflect actual
        // network connectivity, this chain needs modification.
        return calculateStateWhenUpdate(world, current, state);
    }

    @Nullable
    public BlockState calculateStateWhenUpdate(LevelAccessor level, BlockPos pos, BlockState state) {
        // This method should potentially query the LevelNetworkManagerData to know
        // the *actual* network connection status on each face to determine the EPipeState.
        // For now, it relies solely on the canConnectTo / getPipeStateForNeighbor logic.
        // If EPipeState needs to reflect successful network connection vs just potential connection,
        // this method would need the manager instance and the pipe's network UUID.

        // Getting LevelNetworkManagerData here is tricky as LevelAccessor might not be ServerLevel
        // and getting SavedData from LevelAccessor is not standard.
        // If state MUST reflect actual network status, you might need to rely on
        // the network manager itself triggering state updates on pipes when networks change.


        // For now, keep the logic based only on neighbor block properties as it was.
        return state
                .setValue(NORTH, getPipeStateForNeighbor(level, pos, Direction.NORTH))
                .setValue(EAST, getPipeStateForNeighbor(level, pos, Direction.EAST))
                .setValue(SOUTH, getPipeStateForNeighbor(level, pos, Direction.SOUTH))
                .setValue(WEST, getPipeStateForNeighbor(level, pos, Direction.WEST))
                .setValue(UP, getPipeStateForNeighbor(level, pos, Direction.UP))
                .setValue(DOWN, getPipeStateForNeighbor(level, pos, Direction.DOWN));
    }


    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        // Note: onBlockAdded is preferred for initial placement logic.
        // neighborChanged is good for reacting to neighbor updates *after* placement.

        // TODO: When a neighbor changes, the network connection status might change.
        // If a connected pipe is broken, this pipe might become disconnected from its network,
        // or its network might split.
        // This method might need to check the network manager and potentially trigger network updates (splitting).

        if (!level.isClientSide) {
            // Recalculate state for the affected face(s) based on current neighbor state
            // This will update the visual connection state based on canConnectTo.
            BlockState newState = calculateStateWhenUpdate(level, pos, state);

            if (newState.getValue(WATERLOGGED)) {
                level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            }
            if (newState != state) {
                level.setBlock(pos, newState, 3); // Use flag 3 to update neighbors and render
            }

            // TODO: If a neighbor changed from a connectable pipe/node to something else (e.g., air),
            // check if this affects the network connection and potentially trigger network splitting logic.
            // This would involve getting the manager, getting the pipe's network UUID, getting the network,
            // and running split logic if connectivity is broken.
            // LevelNetworkManagerData manager = LevelNetworkManagerData.get((ServerLevel)level); // Requires level to be ServerLevel
            // UUID myNetworkUuid = manager.getNetworkUuidForPipe(pos);
            // If myNetworkUuid != null, check connectivity within the network...
        }
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
    }


    public abstract boolean isSamePipe(Block target);

    // Helper method to get the property for a given direction
    public static EnumProperty<EPipeState> getPropertyForDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }


    // public abstract T createNetwork(); // Potentially add this if network type can be created directly from pipe type?
}