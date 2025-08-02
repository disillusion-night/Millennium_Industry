package kivo.millennium.milltek.world;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import kivo.millennium.milltek.init.MillenniumLevelNetworkType;
import kivo.millennium.milltek.init.MillenniumLevelNetworkType.LevelNetworkType;
import kivo.millennium.milltek.pipe.network.AbstractLevelNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class LevelNetworkSavedData extends SavedData {
    private static final String DATA_NAME = "LevelNetworkSavedData";
    public static final Logger logger = LogUtils.getLogger();

    // 控制是否打印调试信息
    public static boolean DEBUG_LOG = true;

    // 使用实例变量而非静态变量
    private final Map<LevelNetworkType<?>, Map<UUID, AbstractLevelNetwork>> networksByType = new HashMap<>();

    public static LevelNetworkSavedData create() {
        return new LevelNetworkSavedData();
    }

    public void addNetwork(LevelNetworkType<?> type, AbstractLevelNetwork network) {
        networksByType.computeIfAbsent(type, k -> new HashMap<>()).put(network.getUUID(), network);
        setDirty();
    }

    public void removeNetwork(LevelNetworkType<?> networkType, UUID networkUUID) {
        Map<UUID, AbstractLevelNetwork> networks = networksByType.get(networkType);
        if (networks != null) {
            networks.remove(networkUUID);
            if (networks.isEmpty()) {
                networksByType.remove(networkType);
            }
        }
        setDirty();
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractLevelNetwork> T getNetwork(LevelNetworkType<T> type, UUID uuid) {
        // 只在实际修改数据时才调用setDirty()
        T network = (T) networksByType.getOrDefault(type, new HashMap<>()).get(uuid);
        if (DEBUG_LOG && network != null) {
            logger.info("[LevelNetworkSavedData] Retrieved network with UUID: " + uuid + ", isEmpty: " + network.isEmpty());
        }
        return network;
    }

    public Map<UUID, AbstractLevelNetwork> getNetworksByType(LevelNetworkType<?> type) {
        // 只在实际修改数据时才调用setDirty()
        return networksByType.getOrDefault(type, new HashMap<>());
    }
    
    // 调试方法：打印当前所有网络信息
    public void debugPrintNetworks() {
        logger.info("[LevelNetworkSavedData] === Current Networks Debug ===");
        logger.info("[LevelNetworkSavedData] Total network types: " + networksByType.size());
        
        for (Map.Entry<LevelNetworkType<?>, Map<UUID, AbstractLevelNetwork>> entry : networksByType.entrySet()) {
            String typeName = MillenniumLevelNetworkType.LevelNetworkType.getName(entry.getKey());
            Map<UUID, AbstractLevelNetwork> networks = entry.getValue();
            logger.info("[LevelNetworkSavedData] Type: " + typeName + ", Networks count: " + networks.size());
            
            for (Map.Entry<UUID, AbstractLevelNetwork> networkEntry : networks.entrySet()) {
                UUID uuid = networkEntry.getKey();
                AbstractLevelNetwork network = networkEntry.getValue();
                logger.info("[LevelNetworkSavedData]   - UUID: " + uuid + ", isEmpty: " + network.isEmpty() + ", pipes: " + network.toString());
            }
        }
        logger.info("[LevelNetworkSavedData] === End Debug ===");
    }

    @Override
    public CompoundTag save(@Nonnull CompoundTag compound) {
        CompoundTag data = new CompoundTag();

        logger.info("[LevelNetworkSavedData] Saving networks...");

        // 保存每种类型的网络，在一次循环中完成移除空网络和保存非空网络
        for (Map.Entry<LevelNetworkType<?>, Map<UUID, AbstractLevelNetwork>> entry : networksByType.entrySet()) {
            logger.info("[LevelNetworkSavedData] Saving networks of type: " + LevelNetworkType.getName(entry.getKey()));
            String typeKey = MillenniumLevelNetworkType.LevelNetworkType.getName(entry.getKey());
            Map<UUID, AbstractLevelNetwork> networks = entry.getValue();

            ListTag networkList = new ListTag();
            
            // 在一次循环中移除空网络并保存非空网络
            networks.entrySet().removeIf(networkEntry -> {
                AbstractLevelNetwork network = networkEntry.getValue();
                
                // 如果网络为空，移除它
                if (network.isEmpty()) {
                    return true; // 标记为移除
                }
                
                // 网络非空，保存它
                if (DEBUG_LOG){ 
                    logger.info("[LevelNetworkSavedData] Saving network with UUID: " + network.getUUID());
                    logger.info("[LevelNetworkSavedData] Network data: " + network.toString());
                }
                if (network.getUUID() == null) {
                    logger.warn("[LevelNetworkSavedData] Network UUID is null, skipping save for network: " + network);
                    return false; // 不移除，但也不保存
                }
                
                // 将网络数据写入CompoundTag
                CompoundTag networkTag = new CompoundTag();
                network.writeToNBT(networkTag);
                // UUID已在writeToNBT中写入
                networkList.add(networkTag);
                
                return false; // 不移除此网络
            });

            data.put(typeKey, networkList);
        }

        compound.put("networksByType", data);

        if (DEBUG_LOG) {
            System.out.println("[LevelNetworkSavedData] Saved networks: " + networksByType);
        }

        return compound;
    }

    /**
     * 从CompoundTag中解码ModLevelSaveData的数据。
     *
     * @param tag 包含数据的CompoundTag。
     * @return 解码后的ModLevelSaveData实例。
     */
    public static LevelNetworkSavedData decode(CompoundTag tag) {
        logger.info("[LevelNetworkSavedData] Decoding data...");
        LevelNetworkSavedData saveData = LevelNetworkSavedData.create();
        saveData.load(tag);
        return saveData;
    }

    public LevelNetworkSavedData load(CompoundTag compound) {
        // 清理旧数据
        this.networksByType.clear();
        
        CompoundTag data = compound.getCompound("networksByType");
        if (DEBUG_LOG)
            logger.info("[LevelNetworkSavedData] Loading networks...");

        for (String typeKey : data.getAllKeys()) {
            if (DEBUG_LOG)
                logger.info("[LevelNetworkSavedData] Loading networks of type: " + typeKey);
            LevelNetworkType<?> type = MillenniumLevelNetworkType.LEVEL_NETWORK_TYPES.getEntries().stream()
                    .filter(entry -> MillenniumLevelNetworkType.LevelNetworkType.getName(entry.get()).equals(typeKey))
                    .map(entry -> entry.get())
                    .findFirst()
                    .orElse(null);
            if (type == null)
                continue;

            ListTag networkList = data.getList(typeKey, Tag.TAG_COMPOUND);
            Map<UUID, AbstractLevelNetwork> networks = new HashMap<>();

            for (Tag tag : networkList) {
                if (DEBUG_LOG) {
                    logger.info("[LevelNetworkSavedData] Loading network with UUID: "
                            + ((CompoundTag) tag).getUUID("uuid"));
                    logger.info("[LevelNetworkSavedData] Network data: " + tag.toString());
                }
                CompoundTag networkTag = (CompoundTag) tag;
                UUID uuid = networkTag.getUUID("uuid");
                AbstractLevelNetwork network = type.create(networkTag);
                networks.put(uuid, network);
            }

            // 直接操作实例变量
            this.networksByType.put(type, networks);
        }
        
        if (DEBUG_LOG) {
            System.out.println("[LevelNetworkSavedData] Loaded networks: " + this.networksByType);
        }
        return this;
    }

    public static void tick(ServerLevel lvl) {
        LevelNetworkSavedData data = get(lvl);
        if (data != null) {
            // 在这里可以添加对每个网络的tick逻辑
            for (Map.Entry<LevelNetworkType<?>, Map<UUID, AbstractLevelNetwork>> entry : data.networksByType.entrySet()) {
                Map<UUID, AbstractLevelNetwork> networks = entry.getValue();
                for (AbstractLevelNetwork network : networks.values()) {
                    if (network.isEmpty()) continue;
                    network.tick(lvl);
                }
            }
        }
    }

    /**
     * 获取指定世界的ModLevelSaveData实例。通过这个方法获得对应的data
     *
     * @param worldIn 要获取数据的世界。
     * @return 与指定世界关联的ModLevelSaveData实例。
     **/
    public static LevelNetworkSavedData get(Level worldIn) {
        if (worldIn.isClientSide) {
            throw new IllegalStateException("getLevelNetworkSavedData can only be called on the server side");
        }
        
        var server = worldIn.getServer();
        if (server == null) {
            throw new IllegalStateException("Server is null");
        }
        
        ServerLevel world = server.getLevel(ServerLevel.OVERWORLD);
        if (world == null) {
            throw new IllegalStateException("Overworld is null");
        }
        
        DimensionDataStorage dataStorage = world.getDataStorage();
        return dataStorage.computeIfAbsent(LevelNetworkSavedData::decode, LevelNetworkSavedData::create, DATA_NAME);
    }
}
