package kivo.millennium.milltek.world;

import kivo.millennium.milltek.init.MillenniumLevelNetworkType;
import kivo.millennium.milltek.init.MillenniumLevelNetworkType.LevelNetworkType;
import kivo.millennium.milltek.pipe.client.network.AbstractLevelNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LevelNetworkSavedData extends SavedData {
    private static LevelNetworkSavedData instance;

    // 以LevelNetworkType为键，UUID为子键
    private final Map<LevelNetworkType<?>, Map<UUID, AbstractLevelNetwork>> networksByType = new HashMap<>();

    public static LevelNetworkSavedData getInstance() {
        if (instance == null) {
            instance = new LevelNetworkSavedData();
        }
        return instance;
    }

    public void addNetwork(LevelNetworkType<?> type, AbstractLevelNetwork network) {
        networksByType.computeIfAbsent(type, k -> new HashMap<>()).put(network.getUuid(), network);
        setDirty();
    }

    public AbstractLevelNetwork getNetworkByUuid(LevelNetworkType<?> type, UUID uuid) {
        return networksByType.getOrDefault(type, new HashMap<>()).get(uuid);
    }

    public Map<UUID, AbstractLevelNetwork> getNetworksByType(LevelNetworkType<?> type) {
        return networksByType.getOrDefault(type, new HashMap<>());
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        CompoundTag data = new CompoundTag();

        // 保存每种类型的网络
        for (Map.Entry<LevelNetworkType<?>, Map<UUID, AbstractLevelNetwork>> entry : networksByType.entrySet()) {
            String typeKey = MillenniumLevelNetworkType.LevelNetworkType.getName(entry.getKey());
            Map<UUID, AbstractLevelNetwork> networks = entry.getValue();

            ListTag networkList = new ListTag();
            for (AbstractLevelNetwork network : networks.values()) {
                CompoundTag networkTag = new CompoundTag();
                network.writeToNBT(networkTag);
                // UUID已在writeToNBT中写入
                networkList.add(networkTag);
            }

            data.put(typeKey, networkList);
        }

        compound.put("networksByType", data);
        return compound;
    }

    public void load(CompoundTag compound) {
        CompoundTag data = compound.getCompound("networksByType");

        for (String typeKey : data.getAllKeys()) {
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
                CompoundTag networkTag = (CompoundTag) tag;
                UUID uuid = networkTag.getUUID("uuid");
                AbstractLevelNetwork network = type.create(networkTag);
                network.readFromNBT(networkTag);
                networks.put(uuid, network);
            }

            networksByType.put(type, networks);
        }
    }
}
