package kivo.millennium.milltek.pipe.network;

import kivo.millennium.milltek.init.MillenniumLevelNetworkType;
import kivo.millennium.milltek.init.MillenniumLevelNetworkType.LevelNetworkType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractLevelNetwork {
    protected boolean isDirty = false;
    protected final UUID uuid;
    protected final LevelNetworkType<?> levelNetworkType;
    protected final Set<BlockPos> pipePositions = new HashSet<>();

    public AbstractLevelNetwork(LevelNetworkType<?> levelNetworkType) {
        this.levelNetworkType = levelNetworkType;
        this.uuid = UUID.randomUUID();
    }

    public AbstractLevelNetwork(LevelNetworkType<?> levelNetworkType, UUID uuid) {
        this.levelNetworkType = levelNetworkType;
        this.uuid = uuid;
    }

    public void writeToNBT(CompoundTag compoundTag) {
        compoundTag.putUUID("uuid", uuid);
        // 存储所有管道坐标
        var list = new net.minecraft.nbt.ListTag();
        for (BlockPos pos : pipePositions) {
            list.add(net.minecraft.nbt.NbtUtils.writeBlockPos(pos));
        }
        compoundTag.put("pipes", list);
        // 可在子类中扩展更多数据存储
    }

    public AbstractLevelNetwork(LevelNetworkType<?> levelNetworkType, CompoundTag tag) {
        this.levelNetworkType = levelNetworkType;
        this.uuid = tag.getUUID("uuid");
        // 读取所有管道坐标
        this.pipePositions.clear();
        if (tag.contains("pipes")) {
            var list = tag.getList("pipes", net.minecraft.nbt.Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                pipePositions.add(net.minecraft.nbt.NbtUtils.readBlockPos(list.getCompound(i)));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractLevelNetwork> T readFromNBT(CompoundTag compoundTag) {
        // uuid 只读一次，不覆盖已有 uuid
        // 可在子类中扩展更多数据读取
        return (T) this;
    }

    public static AbstractLevelNetwork createNetwork(String type, UUID uuid) {
        var registryObject = MillenniumLevelNetworkType.LEVEL_NETWORK_TYPES.getEntries().stream()
                .filter(entry -> entry.getId().getPath().equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown network type: " + type));

        return registryObject.get().create(uuid);
    }

    protected void setDirty() {
        isDirty = true;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Set<BlockPos> getPipePositions() {
        return pipePositions;
    }

    public void addPipe(BlockPos pos) {
        pipePositions.add(pos);
        setDirty();
    }

    public void removePipe(BlockPos pos) {
        pipePositions.remove(pos);
        setDirty();
    }

    public void clearPipes() {
        pipePositions.clear();
        setDirty();
    }
}