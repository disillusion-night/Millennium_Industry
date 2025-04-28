package kivo.millennium.millind.util;

import kivo.millennium.millind.capability.CapabilityType;
import kivo.millennium.millind.pipe.client.AbstractPipeBL;
import kivo.millennium.millind.pipe.client.network.Network;
import kivo.millennium.millind.pipe.client.network.BlockNetworkTarget;
import kivo.millennium.millind.pipe.client.network.NetworkTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class NetworkDiscoveryUtil {

    /**
     * 使用 DFS 从给定的起始位置发现一个连接的网络段，只沿着属于指定网络子类的组件进行。
     *
     * @param level           要进行搜索的 Level (必须是 ServerLevel)。
     * @param startPos        DFS 的起始方块位置。
     * @param networkSubclass 要发现的网络子类的 Class 对象。
     * @return 包含发现的所有 NetworkTarget (方块和实体) 的 Set，这些目标属于指定的网络子类。
     */
    public static Set<NetworkTarget> discoverNetworkSegment(Level level, BlockPos startPos, Class<? extends Network> networkSubclass) {
        if (level == null || level.isClientSide) {
            return new HashSet<>();
        }

        Set<NetworkTarget> discoveredTargets = new HashSet<>();
        Set<BlockPos> visitedBlocks = new HashSet<>();

        Stack<BlockPos> stack = new Stack<>();

        // 检查起始位置是否是一个有效的网络方块，并且属于指定的网络子类
        if (isValidNetworkComponent(level, startPos, networkSubclass)) {
            stack.push(startPos);
            visitedBlocks.add(startPos);
            discoveredTargets.add(new BlockNetworkTarget(startPos)); // 将起始方块作为 NetworkTarget 添加
        } else {
            return discoveredTargets;
        }

        while (!stack.isEmpty()) {
            BlockPos currentPos = stack.pop();
            BlockState currentState = level.getBlockState(currentPos);

            // 遍历当前方块的所有方向
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = currentPos.relative(direction);
                BlockState neighborState = level.getBlockState(neighborPos);

                // 检查当前方块能否连接到邻居，并且邻居是否属于指定的网络子类
                boolean canConnect = false;
                if (currentState.getBlock() instanceof AbstractPipeBL currentPipe) {
                    // 检查管道是否能连接到邻居 (基于同种网络子类和物理兼容性)
                    canConnect = currentPipe.canConnectTo(level, neighborPos, neighborState, direction);
                    // canConnectTo 应该已经检查了是否属于同种网络子类
                }
                // TODO: 如果当前方块是非管道网络组件，也需要检查它能否连接到邻居 (并且属于同种网络子类)

                if (canConnect) {
                    // 如果可以连接，检查邻居是否是未访问过的、有效的网络组件，且属于指定网络子类
                    if (isValidNetworkComponent(level, neighborPos, networkSubclass)) {
                        // 如果邻居是另一个网络方块 (管道或 BlockEntity 节点)
                        if (!visitedBlocks.contains(neighborPos)) {
                            stack.push(neighborPos); // 将邻居方块添加到堆栈进行 DFS
                            visitedBlocks.add(neighborPos);
                            discoveredTargets.add(new BlockNetworkTarget(neighborPos)); // 将邻居方块作为 NetworkTarget 添加
                        }
                    }
                }
            }
        }

        return discoveredTargets;
    }

    /**
     * 检查给定的 BlockPos 或 Entity 是否是一个有效的网络组件，并且属于指定的网络子类。
     * 用于 DFS 遍历和连接判断。
     *
     * @param level           Level。
     * @param networkSubclass 要检查的网络子类的 Class 对象。
     * @return 如果是有效的网络组件且属于指定子类，则为 true。
     */
    public static boolean isValidNetworkComponent(Level level, BlockPos pos, Class<? extends Network> networkSubclass) {
        if (level == null || pos == null) return false;

        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return false;

        // 检查是否是指定网络子类的管道
        if (state.getBlock() instanceof AbstractPipeBL pipeBlock) {
            return true;
        }

        // 检查是否是具有 Capability 且属于指定网络子类的 BlockEntity
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null) {
            // TODO: 检查 BlockEntity 是否属于指定的网络子类
            // 这需要在 BlockEntity 中提供一个方法来获取其所属的网络子类
            // 或者通过检查其持有的 Capability 来判断，并与 networkSubclass 关联
            // Capability<?> requiredCap = getCapabilityFromNetworkSubclass(networkSubclass); // TODO: 实现这个方法
            // if(requiredCap != null && be.getCapability(requiredCap).isPresent()){
            //      // 还需要确认这个 Capability 确实属于指定的网络子类
            //      // 这可能需要一个注册表来关联 Capability 和网络子类
            //      return true;
            // }
        }
        return false;

    }

    // 辅助方法：根据 CapabilityType 获取对应的 Forge Capability
    private static Capability<?> getCapabilityFromType(CapabilityType type) {
        return switch (type) {
            case ENERGY -> ForgeCapabilities.ENERGY;
            case FLUID -> ForgeCapabilities.FLUID_HANDLER;
            case ITEM -> ForgeCapabilities.ITEM_HANDLER;
            case GAS -> ForgeCapabilities.FLUID_HANDLER; // 气体通常使用流体 Capability
            // TODO: 添加其他 CapabilityType 的映射
            default -> null;
        };
    }

    // TODO: 实现一个方法来获取连接到给定 BlockPos 和方向的 Entity
    private static Entity getConnectedEntity(BlockGetter level, BlockPos pos, Direction direction) {
        // 这取决于你的管道如何连接到实体，需要根据你的具体连接机制来实现
        return null;
    }

    // TODO: 实现一个方法来获取与指定网络子类相关的 Capability 类型
    // 例如：public static CapabilityType getCapabilityTypeFromNetworkSubclass(Class<? extends Network> networkSubclass) {}
    // 这个方法可能在 isValidNetworkComponent 中用于判断非管道组件
}