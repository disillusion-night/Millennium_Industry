package kivo.millennium.millind.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public abstract class AbstractMultiblock implements IMultiblock {

    @Override
    public BlockPos calculateCorePosition(BlockPos startPos) {
        // 默认实现，具体实现由子类提供
        return startPos;
    }
    /**
     * 默认的check方法实现，可以被子类重写。
     */
    @Override
    public boolean check(Level world, BlockPos startPos) {
        // 默认实现：假设所有的方块状态都有效
        return true;
    }
}