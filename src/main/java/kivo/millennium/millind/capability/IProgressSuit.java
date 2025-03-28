package kivo.millennium.millind.capability;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface IProgressSuit {

    int getProgress();

    int getProgressPersentage();

    int isWorking();

    int addProgress();
}
