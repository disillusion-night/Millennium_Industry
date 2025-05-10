package kivo.millennium.milltek.pipe.client.network;

import kivo.millennium.milltek.init.MillenniumLevelNetworkType;
import kivo.millennium.milltek.init.MillenniumLevelNetworkType.LevelNetworkType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;

import org.stringtemplate.v4.compiler.CodeGenerator.primary_return;

public abstract class AbstractLevelNetwork {
    protected boolean isDirty = false;
    protected int id;
    protected final LevelNetworkType<?> levelNetworkType;
    protected final ArrayList<AbstractNetworkTarget> inputs;
    protected final ArrayList<AbstractNetworkTarget> outputs;

    public AbstractLevelNetwork(LevelNetworkType<?> levelNetworkType, int id) {
        this.levelNetworkType = levelNetworkType;
        this.id = id;
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
    }

    public AbstractLevelNetwork(LevelNetworkType<?> levelNetworkType, int id, ArrayList<AbstractNetworkTarget> inputs, ArrayList<AbstractNetworkTarget> outputs) {
        this.levelNetworkType = levelNetworkType;
        this.id = id;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    // 添加输入目标
    public void addInput(AbstractNetworkTarget target) {
        if (!inputs.contains(target)) {
            inputs.add(target);
            setDirty();
        }
    }
    // 添加输入目标
    public void addInput(BlockEntity blockEntity) {
        BlockEntityNetworkTarget target = new BlockEntityNetworkTarget(blockEntity.getBlockPos());
        if (!inputs.contains(target)) {
            inputs.add(target);
            setDirty();
        }
    }

    // 移除输入目标
    public void removeInput(AbstractNetworkTarget target) {
        if (inputs.remove(target)) {
            setDirty();
        }
    }
    
    // 移除输入目标
    public void removeInput(BlockEntity blockEntity) {
        BlockEntityNetworkTarget target = new BlockEntityNetworkTarget(blockEntity.getBlockPos());
        if (inputs.remove(target)) {
            setDirty();
        }
    }

    // 添加输出目标
    public void addOutput(AbstractNetworkTarget target) {
        if (!outputs.contains(target)) {
            outputs.add(target);
            setDirty();
        }
    }

    public void addOutput(BlockEntity blockEntity) {
        BlockEntityNetworkTarget target = new BlockEntityNetworkTarget(blockEntity.getBlockPos());
        if (!outputs.contains(target)) {
            outputs.add(target);
            setDirty();
        }
    }

    // 移除输出目标
    public void removeOutput(AbstractNetworkTarget target) {
        if (outputs.remove(target)) {
            setDirty();
        }
    }

    public void writeToNBT(CompoundTag compoundTag) {
        compoundTag.putInt("id", id);

        ListTag inputList = new ListTag();
        inputs.forEach(target -> inputList.add(target.writeToNBT()));
        compoundTag.put("inputs", inputList);

        ListTag outputList = new ListTag();
        outputs.forEach(target -> outputList.add(target.writeToNBT()));
        
        compoundTag.put("outputs", outputList);
    }

    public <T extends AbstractLevelNetwork> T readFromNBT(CompoundTag compoundTag) {
        this.id = compoundTag.getInt("id");

        ListTag inputList = compoundTag.getList("inputs", 10);
        for (int i = 0; i < inputList.size(); i++) {
            CompoundTag targetTag = inputList.getCompound(i);
            BlockEntityNetworkTarget target = new BlockEntityNetworkTarget(targetTag);
            inputs.add(target);
        }

        ListTag outputList = compoundTag.getList("outputs", 10);
        for (int i = 0; i < outputList.size(); i++) {
            CompoundTag targetTag = outputList.getCompound(i);
            BlockEntityNetworkTarget target = new BlockEntityNetworkTarget(targetTag);
            outputs.add(target);
        }
        return (T) this;
    }

    public static AbstractLevelNetwork createNetwork(String type, int id) {
        var registryObject = MillenniumLevelNetworkType.LEVEL_NETWORK_TYPES.getEntries().stream()
            .filter(entry -> entry.getId().getPath().equals(type))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown network type: " + type));

        return registryObject.get().create(id);
    }

    public static void tick(Level level, AbstractLevelNetwork network) {
        if (!level.isClientSide()) {
            network.tickServer(level);
        }
    }

    protected abstract void tickServer(Level level);

    protected void setDirty() {
        isDirty = true;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public int getId() {
        return id;
    }
}
