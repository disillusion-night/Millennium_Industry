package kivo.millennium.millind.pipe.client.network;

import kivo.millennium.millind.init.LevelNetworkType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation; // 导入 ResourceLocation
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class NetworkManagerData extends SavedData {
    private static final String DATA_NAME = "millind_level_network_manager"; //管理单维度网络

    // 存储单维度网络实例
    private static final Map<String, ArrayList<AbstractLevelNetwork>> levelNetworks = new HashMap<>();

    public NetworkManagerData() {
    }

    @Override
    public CompoundTag save(CompoundTag pCompoundTag) {
        return null;
    }

    /**
     * 从 CompoundTag 加载 NetworkManagerData。
     */
    public static NetworkManagerData load(CompoundTag tag) {
        NetworkManagerData data = new NetworkManagerData();
        //data.nextNetworkId = tag.getInt("next_network_id");

        // 获取自定义注册表
        IForgeRegistry<LevelNetworkType<?>> networkTypeRegistry = LevelNetworkType.LEVEL_NETWORK_TYPES;
        if (networkTypeRegistry == null) {
            // TODO: 处理注册表未加载的情况，这不应该发生，或者延迟加载
            System.err.println("Network Type Registry not available during load!");
            return data; // 返回空数据，避免崩溃
        }


        CompoundTag networksTag = tag.getCompound("networks");
        for (String networkId : networksTag.getAllKeys()) {
            CompoundTag networkTag = networksTag.getCompound(networkId);
            String typeIdentifier = networkTag.getString("type_identifier");
            // CapabilityType capabilityType = CapabilityType.valueOf(networkTag.getString("capability_type")); // CapabilityType 不用于创建，工厂内部处理

            // 根据注册名从注册表中查找 LevelNetworkType
            ResourceLocation registryName = ResourceLocation.tryParse(typeIdentifier);
            if (registryName != null) {
                LevelNetworkType<?> networkType = networkTypeRegistry.getValue(registryName);

                if (networkType != null) {
                    // 使用 LevelNetworkType 创建网络实例
                    AbstractLevelNetwork network = networkType.create(); // 调用 LevelNetworkType 的 create 方法

                    if (network != null) {
                        // 加载网络数据 (AbstractLevelNetwork 的 load 方法会处理节点和子类特有数据)
                        network.load(networkTag);
                        //data.levelNetworks.put(networkId, network);
                    } else {
                        // TODO: 处理工厂创建实例失败的情况
                        System.err.println("Factory for network type '" + typeIdentifier + "' returned null during load!");
                    }
                } else {
                    // TODO: 处理未知网络类型标识符的情况，例如打印警告日志或跳过该网络
                    System.err.println("Unknown network type identifier '" + typeIdentifier + "' found during load! Skipping network.");
                }
            } else {
                // TODO: 处理无效的类型标识符
                System.err.println("Invalid network type identifier '" + typeIdentifier + "' found during load! Skipping network.");
            }
        }
        return data;
    }

    // ... (其他保存和管理网络的方法，保持与之前 AbstractLevelNetwork 对应的逻辑一致)

    /**
     * 使用注册表根据 LevelNetworkType 创建具体的网络实例。
     *
     * @param networkType 要创建的 LevelNetworkType。
     * @return 创建的 AbstractLevelNetwork 实例，如果工厂返回 null 则返回 null。
     */
    private static AbstractLevelNetwork createNetworkFromType(LevelNetworkType<?> networkType) {
        if (networkType != null) {
            // 使用 LevelNetworkType 创建网络实例
            return networkType.create();
        }
        return null; // LevelNetworkType 为 null
    }

    // TODO: 确保 updateNetwork, mergeNetworks, splitNetwork 等方法使用 createNetworkFromType 或直接调用 LevelNetworkType.create
    // 例如：
    public void updateNetwork(ServerLevel serverLevel, BlockPos changedPos, Class<? extends AbstractLevelNetwork> networkSubclass) {
        // ...
        // 在需要创建新网络的地方
        // LevelNetworkType<?> networkType = // TODO: 根据 networkSubclass 找到对应的 LevelNetworkType
        // AbstractLevelNetwork newNetwork = createNetworkFromType(networkType);
        // ...
    }
    /*
    private AbstractLevelNetwork mergeNetworks(ServerLevel serverLevel, List<String> networkIdsToMerge, Class<? extends AbstractLevelNetwork> networkSubclass) {
        // ...
        // 在需要创建合并后的网络时
        // LevelNetworkType<?> networkType = // TODO: 根据 networkSubclass 找到对应的 LevelNetworkType
        // AbstractLevelNetwork mergedNetwork = createNetworkFromType(networkType);
        // ...
        //return new AbstractLevelNetwork();
        return
    }*/

    private void splitNetwork(ServerLevel serverLevel, String oldNetworkId, List<Set<NetworkTarget>> newSegments, Class<? extends AbstractLevelNetwork> networkSubclass) {
        // ...
        // 在为新 segment 创建网络时
        // LevelNetworkType<?> networkType = // TODO: 根据 networkSubclass 找到对应的 LevelNetworkType
        // AbstractLevelNetwork newNetwork = createNetworkFromType(networkType);
        // ...
    }

    // TODO: 辅助方法来根据网络子类 Class 找到对应的 LevelNetworkType
    // 这需要在你的 Mod 中维护一个映射，或者遍历注册表查找
    // 例如：public static LevelNetworkType<?> getNetworkTypeFromClass(Class<? extends AbstractLevelNetwork> networkClass) { ... }

    public static List<AbstractLevelNetwork> getLevelNetworks(){
        List<AbstractLevelNetwork> networks = new ArrayList<>();
        //levelNetworks.forEach((String id, ArrayAbstractLevelNetwork network) -> {
            //networks.add(network);
        //});
        return networks;
    }
}