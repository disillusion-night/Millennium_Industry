package kivo.millennium.milltek.world;

import kivo.millennium.milltek.init.MillenniumLevelNetworkType;
import kivo.millennium.milltek.pipe.client.network.AbstractLevelNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;

public class LevelNetworkSavedData extends SavedData {
    private static LevelNetworkSavedData instance;

    // 按注册名存储不同类型的网络
    private final Map<String, Map<Integer, AbstractLevelNetwork>> networksByType = new HashMap<>();
    private final Map<String, Integer> nextNetworkIdByType = new HashMap<>();

    public static LevelNetworkSavedData getInstance() {
        if (instance == null) {
            instance = new LevelNetworkSavedData();
        }
        return instance;
    }

    public int generateNewNetworkID(String type) {
        int nextId = nextNetworkIdByType.getOrDefault(type, 0);
        nextNetworkIdByType.put(type, nextId + 1);
        return nextId;
    }

    public void addNetwork(String type, AbstractLevelNetwork network) {
        networksByType.computeIfAbsent(type, k -> new HashMap<>()).put(network.getId(), network);
        setDirty();
    }

    public AbstractLevelNetwork getNetworkById(String type, int id) {
        return networksByType.getOrDefault(type, new HashMap<>()).get(id);
    }

    public Map<Integer, AbstractLevelNetwork> getNetworksByType(String type) {
        return networksByType.getOrDefault(type, new HashMap<>());
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        CompoundTag data = new CompoundTag();

        // 保存每种类型的网络
        for (Map.Entry<String, Map<Integer, AbstractLevelNetwork>> entry : networksByType.entrySet()) {
            String type = entry.getKey();
            Map<Integer, AbstractLevelNetwork> networks = entry.getValue();

            ListTag networkList = new ListTag();
            for (AbstractLevelNetwork network : networks.values()) {
                CompoundTag networkTag = new CompoundTag();
                network.writeToNBT(networkTag);
                networkTag.putInt("id", network.getId());
                networkList.add(networkTag);
            }

            data.put(type, networkList);
        }

        // 保存每种类型的下一个网络 ID
        CompoundTag nextIdTag = new CompoundTag();
        for (Map.Entry<String, Integer> entry : nextNetworkIdByType.entrySet()) {
            nextIdTag.putInt(entry.getKey(), entry.getValue());
        }
        data.put("nextNetworkIds", nextIdTag);

        compound.put("networksByType", data);
        return compound;
    }

    public void load(CompoundTag compound) {
        CompoundTag data = compound.getCompound("networksByType");

        // 加载每种类型的网络
        for (String type : data.getAllKeys()) {
            ListTag networkList = data.getList(type, Tag.TAG_COMPOUND);
            Map<Integer, AbstractLevelNetwork> networks = new HashMap<>();

            for (Tag tag : networkList) {
                CompoundTag networkTag = (CompoundTag) tag;
                int id = networkTag.getInt("id");

                // 使用注册表重新创建网络
                AbstractLevelNetwork network = MillenniumLevelNetworkType.LEVEL_NETWORK_TYPES.getEntries().stream()
                    .filter(entry -> entry.getId().getPath().equals(type))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown network type: " + type))
                    .get()
                    .create(id);

                network.readFromNBT(networkTag);
                networks.put(id, network);
            }

            networksByType.put(type, networks);
        }

        // 加载每种类型的下一个网络 ID
        CompoundTag nextIdTag = compound.getCompound("nextNetworkIds");
        for (String type : nextIdTag.getAllKeys()) {
            nextNetworkIdByType.put(type, nextIdTag.getInt(type));
        }
    }
}
