package kivo.millennium.milltek.pipe.network;

import net.minecraftforge.common.util.INBTSerializable;

public interface IMergeableStorage<T extends INBTSerializable<?>> {
    public T merge(T other);

    public void clear();
}
