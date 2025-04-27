package kivo.millennium.millind.util;

import kivo.millennium.millind.capability.CapabilityType;
import kivo.millennium.millind.pipe.client.AbstractPipeBL;
import kivo.millennium.millind.pipe.client.network.BlockNetworkTarget;
import kivo.millennium.millind.pipe.client.network.EntityNetworkTarget;
import kivo.millennium.millind.pipe.client.network.NetworkTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity; // 导入 Entity
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class NetworkDiscoveryUtil {

    /**
     * 使用 DFS 从给定的起始位置发现一个连接的网络段。
     * DFS 主要在方块之间进行。实体目标只作为标记。
     *
     * @param level         要进行搜索的 Level (必须是 ServerLevel)。
     * @param startPos      DFS 的起始方块位置。
     * @param capabilityType 要发现网络的 Capability 类型。
     * @return 包含发现的所有 NetworkTarget (方块和实体) 的 Set。
     */
    public static Set<NetworkTarget> discoverNetworkSegment(Level level, BlockPos startPos, CapabilityType capabilityType) {
        if (level == null || level.isClientSide) {
            return new HashSet<>();
        }

        Set<NetworkTarget> discoveredTargets = new HashSet<>();
        Set<BlockPos> visitedBlocks = new HashSet<>(); // 记录访问过的方块位置

        Stack<BlockPos> stack = new Stack<>();

        // 检查起始位置是否是一个有效的网络方块，并添加到堆栈和已访问集合
        if (isValidNetworkBlock(level, startPos, capabilityType)) {
            stack.push(startPos);
            visitedBlocks.add(startPos);
            discoveredTargets.add(new BlockNetworkTarget(startPos)); // 将起始方块作为 NetworkTarget 添加
        } else {
            // 起始位置不是有效的网络方块，无法进行搜索
            return discoveredTargets;
        }

        while (!stack.isEmpty()) {
            BlockPos currentPos = stack.pop();
            BlockState currentState = level.getBlockState(currentPos);

            // 遍历当前方块的所有方向
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = currentPos.relative(direction);
                BlockState neighborState = level.getBlockState(neighborPos);

                // 检查当前方块 (currentPos) 是否能连接到邻居 (neighborPos)
                boolean canConnect = false;
                if (currentState.getBlock() instanceof AbstractPipeBL pipeBlock) {
                    canConnect = pipeBlock.canConnectTo(level, neighborPos, neighborState, direction);
                }
                // TODO: 如果当前方块不是管道，它可能是一个网络节点，需要从这里检查连接到邻居的能力

                if (canConnect) {
                    // 如果可以连接，检查邻居是否是未访问过的、有效的网络组件
                    if (isValidNetworkBlock(level, neighborPos, capabilityType)) {
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
     * 检查给定的 BlockPos 是否是一个有效的网络方块（管道或具有 Capability 的 BlockEntity 节点）。
     * 用于 DFS 遍历。
     *
     * @param level          Level。
     * @param pos            要检查的 BlockPos。
     * @param capabilityType 网络处理的 Capability 类型。
     * @return 如果是有效的网络方块，则为 true。
     */
    private static boolean isValidNetworkBlock(Level level, BlockPos pos, CapabilityType capabilityType) {
        if (level == null) return false;
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return false;

        // 检查是否是管道
        if (state.getBlock() instanceof AbstractPipeBL) {
            return true; // 假设所有 AbstractPipeBL 都可以是网络的组成部分
        }

        // 检查是否是具有与 CapabilityType 对应 Capability 的 BlockEntity
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null) {
            Capability<?> requiredCap = getCapabilityFromType(capabilityType);
            if(requiredCap != null && be.getCapability(requiredCap).isPresent()){
                return true;
            }
        }

        return false;
    }

    /**
     * 检查给定的 Entity 是否是一个有效的网络实体节点。
     * 用于标识实体目标。
     *
     * @param entity         要检查的 Entity。
     * @param capabilityType 网络处理的 Capability 类型。
     * @return 如果是有效的网络实体节点，则为 true。
     */
    private static boolean isValidNetworkEntity(Entity entity, CapabilityType capabilityType){
        if (entity == null) return false;

        // 检查实体是否具有与 CapabilityType 对应 Capability
        Capability<?> requiredCap = getCapabilityFromType(capabilityType);
        if(requiredCap != null && entity.getCapability(requiredCap).isPresent()){
            return true;
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
    // 这取决于你的管道如何连接到实体
    private static Entity getConnectedEntity(Level level, BlockPos pos, Direction direction) {
        // 示例：检查邻居位置是否有实体，并判断是否应该连接
        // 这部分逻辑可能需要根据你的具体连接机制来实现
        // 例如，你可能有一个特殊的方块实体，当它旁边有实体时才连接
        // 或者你的管道本身可以连接到范围内的实体

        // 简化的示例：检查邻居位置是否有实体（不一定准确）
        // List<Entity> entities = level.getEntities(null, new net.minecraft.world.phys.AABB(pos.relative(direction)), entity -> true);
        // if (!entities.isEmpty()) {
        //     return entities.get(0); // 返回第一个实体 (非常简陋)
        // }
        return null;
    }
}