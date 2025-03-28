package kivo.millennium.millind.block;

public interface IWorkingMachine {
    boolean isWorking();

    int getProgressPercent();

    default int getProgressAndLit() {
        if (isWorking()) {
            return getProgressPercent() << 1 | 1;
        } else {
            return getProgressPercent() << 1;
        }
    }

}
