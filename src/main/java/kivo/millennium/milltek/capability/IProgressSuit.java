package kivo.millennium.milltek.capability;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface IProgressSuit {

    int getProgress();

    int getProgressPersentage();

    int isWorking();

    int addProgress();
}
