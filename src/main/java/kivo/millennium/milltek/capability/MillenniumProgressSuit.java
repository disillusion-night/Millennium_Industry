package kivo.millennium.milltek.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class MillenniumProgressSuit implements IProgressSuit, INBTSerializable<CompoundTag> {
    private int progress;
    private int maxTime;
    private boolean isWorking;

    public MillenniumProgressSuit(int maxTime){
        this.maxTime = maxTime;
        this.progress = 0;
        this.isWorking = false;
    }

    public MillenniumProgressSuit(int maxTime, int progress){
        this.progress = progress;
        this.maxTime = maxTime;
        this.isWorking = true;
    }

    public void setWorking(boolean working){
        this.isWorking = working;
    }

    public void setMaxTime(int maxTime){
        this.maxTime = maxTime;
    }

    public void setProgress(int progress){
        this.progress = progress;
    }

    public void resetProgress(){
        this.progress = 0;
    }

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    public int getProgressPersentage() {
        return (int) ((double)progress / maxTime) * 100;
    }

    public int toInt(){
        if (isWorking){
            return progress << 1 + 1;
        }
        return progress;
    }

    public static int getPersentageFromCode(int code) {
        return code >> 1;
    }

    public static boolean getWorkingFromCode(int code) {
        return (code & 1) == 1;
    }

    @Override
    public int isWorking() {
        return 0;
    }

    @Override
    public int addProgress() {
        return 0;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("progress", this.progress);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.progress = nbt.contains("progress") ? nbt.getInt("progress"):0;
    }
}
