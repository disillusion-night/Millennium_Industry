package kivo.millennium.millind.multiblock;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MultiBlock {
    protected Vec3i size;
    protected BlockEntity core;
    protected Vec3i offset;

    public Vec3i getSize() {
        return size;
    }

}
