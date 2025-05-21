package kivo.millennium.milltek.world;

import kivo.millennium.milltek.init.MillenniumLevelNetworkType;
import kivo.millennium.milltek.init.MillenniumLevelNetworkType.LevelNetworkType;
import kivo.millennium.milltek.pipe.client.network.AbstractLevelNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

public class LevelNetworkSavedData extends SavedData {
    private static final String DATA_NAME = "LevelNetworkSavedData";
    public static final Logger logger = LogUtils.getLogger();

    // 控制是否打印调试信息
    public static boolean DEBUG_LOG = false;

    // 以LevelNetworkType为键，UUID为子键
    private static final Map<LevelNetworkType<?>, Map<UUID, AbstractLevelNetwork>> networksByType = new HashMap<>();

    public static LevelNetworkSavedData create(){
        return new LevelNetworkSavedData();
    }

    public void addNetwork(LevelNetworkType<?> type, AbstractLevelNetwork network) {
        networksByType.computeIfAbsent(type, k -> new HashMap<>()).put(network.getUuid(), network);
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

    public <T extends AbstractLevelNetwork>T getNetworkByUuid(LevelNetworkType<T> type, UUID uuid) {
        setDirty();
        return (T) networksByType.getOrDefault(type, new HashMap<>()).get(uuid);
    }

    public Map<UUID, AbstractLevelNetwork> getNetworksByType(LevelNetworkType<?> type) {
        setDirty();
        return networksByType.getOrDefault(type, new HashMap<>());
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        CompoundTag data = new CompoundTag();

        logger.info("[LevelNetworkSavedData] Saving networks...");

        // 保存每种类型的网络
        for (Map.Entry<LevelNetworkType<?>, Map<UUID, AbstractLevelNetwork>> entry : networksByType.entrySet()) {
            logger.info("[LevelNetworkSavedData] Saving networks of type: " + LevelNetworkType.getName(entry.getKey()));
            String typeKey = MillenniumLevelNetworkType.LevelNetworkType.getName(entry.getKey());
            Map<UUID, AbstractLevelNetwork> networks = entry.getValue();

            ListTag networkList = new ListTag();
            for (AbstractLevelNetwork network : networks.values()) {
                logger.info("[LevelNetworkSavedData] Saving network with UUID: " + network.getUuid());
                
                logger.info("[LevelNetworkSavedData] Network data: " + network.toString());

                CompoundTag networkTag = new CompoundTag();
                network.writeToNBT(networkTag);
                // UUID已在writeToNBT中写入
                networkList.add(networkTag);
            }

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
    public static LevelNetworkSavedData decode(CompoundTag tag){
        logger.info("[LevelNetworkSavedData] Decoding data...");
        LevelNetworkSavedData saveData = LevelNetworkSavedData.create();
        saveData.load(tag);
        return saveData;
    }


    public LevelNetworkSavedData load(CompoundTag compound) {
        LevelNetworkSavedData levelNetworkSavedData = this.create();
        CompoundTag data = compound.getCompound("networksByType");
        if(DEBUG_LOG) logger.info("[LevelNetworkSavedData] Loading networks...");

        for (String typeKey : data.getAllKeys()) {
            if (DEBUG_LOG) logger.info("[LevelNetworkSavedData] Loading networks of type: " + typeKey);
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
                if (DEBUG_LOG){
                    logger.info("[LevelNetworkSavedData] Loading network with UUID: " + ((CompoundTag) tag).getUUID("uuid"));
                    logger.info("[LevelNetworkSavedData] Network data: " + tag.toString());
                }
                CompoundTag networkTag = (CompoundTag) tag;
                UUID uuid = networkTag.getUUID("uuid");
                AbstractLevelNetwork network = type.create(networkTag);
                network.readFromNBT(networkTag);
                networks.put(uuid, network);
            }

            networksByType.put(type, networks);
        }

        if (DEBUG_LOG) {
            System.out.println("[LevelNetworkSavedData] Loaded networks: " + networksByType);
        }
        return levelNetworkSavedData;
    }

    
    /**
     * 获取指定世界的ModLevelSaveData实例。通过这个方法获得对应的data
     *
     * @param worldIn 要获取数据的世界。
     * @return 与指定世界关联的ModLevelSaveData实例。
     * **/
    public static LevelNetworkSavedData get(Level worldIn) {
        if (worldIn.isClientSide) {
            throw new IllegalStateException("getLevelNetworkSavedData can only be called on the server side");
        }
        ServerLevel world = worldIn.getServer().getLevel(ServerLevel.OVERWORLD);
        DimensionDataStorage dataStorage = world.getDataStorage();
        return dataStorage.computeIfAbsent(LevelNetworkSavedData::decode, LevelNetworkSavedData::create, DATA_NAME);
    }
}
