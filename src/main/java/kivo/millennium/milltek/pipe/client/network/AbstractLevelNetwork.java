package kivo.millennium.milltek.pipe.client.network;

import com.mojang.datafixers.types.templates.CompoundList;
import com.mojang.datafixers.types.templates.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public abstract class AbstractLevelNetwork {
    protected final int id;
    protected final ArrayList<AbstractNetworkTarget> inputs;
    protected final ArrayList<AbstractNetworkTarget> outputs;

    public AbstractLevelNetwork(int id, ArrayList<AbstractNetworkTarget> inputs,
            ArrayList<AbstractNetworkTarget> outputs) {
        this.id = id;
        this.inputs = inputs;
        this.outputs = outputs;

    }

    public void writeToNBT(CompoundTag compoundTag) {
        compoundTag.putInt("id", id);
        ListTag inputList = new ListTag();
        inputs.forEach((AbstractNetworkTarget targets) -> {
            inputList.add(targets.writeToNBT());
        });
        compoundTag.put("inputs", inputList);

        ListTag outputList = new ListTag();
        inputs.forEach((AbstractNetworkTarget targets) -> {
            outputList.add(targets.writeToNBT());
        });
        compoundTag.put("outputs", outputList);

    }

    protected void writeAdditionalData() {

    }

    public AbstractLevelNetwork readFromNBT(CompoundTag compoundTag) {
        return this;
    }

    public static void tick(Level level, AbstractLevelNetwork network) {
        if (!level.isClientSide())
            network.tickServer(level);
    }

    protected abstract void tickServer(Level level);
}
