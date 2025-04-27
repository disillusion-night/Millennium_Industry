package kivo.millennium.millind.pipe.client.network;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Network {

    private final boolean isGlobal;
    private final List<NetworkTarget> inputNodes = new ArrayList<>();
    private final List<NetworkTarget> outputNodes = new ArrayList<>();

    /**
     * 网络系统的基类。
     *
     * @param isGlobal 如果网络可以在维度间传输（全局可见），则为 true；否则为 false（单维度传输）。
     */
    public Network(boolean isGlobal) {
        this.isGlobal = isGlobal;
    }

    protected abstract String getTypeIdentifier();

    /**
     * 获取网络是否为全局可见（跨维度传输）。
     *
     * @return 如果是全局可见网络，则为 true；否则为 false。
     */
    public final boolean isGlobal() {
        return isGlobal;
    }

    /**
     * 添加一个输入节点。
     *
     * @param target 输入节点的网络目标。
     */
    public void addInputNode(NetworkTarget target) {
        if (!this.inputNodes.contains(target)) {
            this.inputNodes.add(target);
            // TODO: 通知 WorldSavedData 数据已更改
        }
    }

    /**
     * 移除一个输入节点。
     *
     * @param target 要移除的输入节点的网络目标。
     */
    public void removeInputNode(NetworkTarget target) {
        if (this.inputNodes.remove(target)) {
            // TODO: 通知 WorldSavedData 数据已更改
        }
    }

    /**
     * 获取所有输入节点的网络目标列表。
     *
     * @return 输入节点的 NetworkTarget 列表。
     */
    public List<NetworkTarget> getInputNodes() {
        return inputNodes;
    }

    /**
     * 添加一个输出节点。
     *
     * @param target 输出节点的网络目标。
     */
    public void addOutputNode(NetworkTarget target) {
        if (!this.outputNodes.contains(target)) {
            this.outputNodes.add(target);
            // TODO: 通知 WorldSavedData 数据已更改
        }
    }

    /**
     * 移除一个输出节点。
     *
     * @param target 要移除的输出节点的网络目标。
     */
    public void removeOutputNode(NetworkTarget target) {
        if (this.outputNodes.remove(target)) {
            // TODO: 通知 WorldSavedData 数据已更改
        }
    }

    /**
     * 获取所有输出节点的网络目标列表。
     *
     * @return 输出节点的 NetworkTarget 列表。
     */
    public List<NetworkTarget> getOutputNodes() {
        return outputNodes;
    }


    /**
     * 获取网络提供的 Capability。
     * 具体的 Capability 逻辑在子类中实现。
     *
     * @param cap  要获取的 Capability。
     * @param side 可能相关的方向（对于网络来说，通常为 null，除非你有基于方向的 Capability）。
     * @return 一个包含 Capability 实例的 LazyOptional，如果不支持该 Capability 则为空 LazyOptional。
     */
    @NotNull
    public abstract <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side);


    /**
     * 将网络的特定数据保存到 CompoundTag。
     *
     * @param tag 要保存到的 CompoundTag。
     * @return 保存后的 CompoundTag。
     */
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("is_global", this.isGlobal);
        tag.putString("type", getTypeIdentifier()); // 保存网络类型标识符

        // 保存输入节点
        CompoundTag inputNodesTag = new CompoundTag();
        for (int i = 0; i < inputNodes.size(); i++) {
            inputNodesTag.put(String.valueOf(i), inputNodes.get(i).save(new CompoundTag()));
        }
        tag.put("input_nodes", inputNodesTag);

        // 保存输出节点
        CompoundTag outputNodesTag = new CompoundTag();
        for (int i = 0; i < outputNodes.size(); i++) {
            outputNodesTag.put(String.valueOf(i), outputNodes.get(i).save(new CompoundTag()));
        }
        tag.put("output_nodes", outputNodesTag);

        // 保存子类特有的数据
        saveNetworkData(tag);

        return tag;
    }

    /**
     * 从 CompoundTag 加载网络的特定数据。
     *
     * @param tag 要加载的 CompoundTag。
     */
    public void load(CompoundTag tag) {
        // isGlobal 在构造方法中设置，不需要从 tag 加载
        // type 在构造方法中设置，不需要从 tag 加载

        // 加载输入节点
        CompoundTag inputNodesTag = tag.getCompound("input_nodes");
        for (String key : inputNodesTag.getAllKeys()) {
            CompoundTag nodeTag = inputNodesTag.getCompound(key);
            NetworkTarget target = NetworkTarget.fromNbt(nodeTag);
            if (target != null) {
                inputNodes.add(target);
            }
        }

        // 加载输出节点
        CompoundTag outputNodesTag = tag.getCompound("output_nodes");
        for (String key : outputNodesTag.getAllKeys()) {
            CompoundTag nodeTag = outputNodesTag.getCompound(key);
            NetworkTarget target = NetworkTarget.fromNbt(nodeTag);
            if (target != null) {
                outputNodes.add(target);
            }
        }

        // 加载子类特有的数据
        loadNetworkData(tag);
    }

    /**
     * 将子类特有的网络数据保存到 CompoundTag。
     * 由子类实现。
     *
     * @param tag 要保存到的 CompoundTag。
     */
    protected abstract void saveNetworkData(CompoundTag tag);

    /**
     * 从 CompoundTag 加载子类特有的网络数据。
     * 由子类实现。
     *
     * @param tag 要加载的 CompoundTag。
     */
    protected abstract void loadNetworkData(CompoundTag tag);


    /**
     * 处理网络的每个 Tick 逻辑。
     * 由子类实现。
     *
     * @param level 网络所在的 Level。
     */
    public abstract void handleTick(ServerLevel level); // 使用 ServerLevel 以便访问服务端特性

}