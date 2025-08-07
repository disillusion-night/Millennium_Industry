package kivo.millennium.milltek.pipe.network;

import net.minecraftforge.common.util.INBTSerializable;

public interface IPipeStorage<T extends IPipeStorage<?>> {
    T merge(T other);

    void clear();

    public int getCapacity();

    T setCapacity(int capacity);
}
