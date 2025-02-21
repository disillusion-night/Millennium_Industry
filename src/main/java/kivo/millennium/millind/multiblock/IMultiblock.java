package kivo.millennium.millind.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IMultiblock {
    /**
     * 检查当前多方块结构是否合法。
     * @param world 世界对象
     * @param startPos 起始位置
     * @return 如果结构合法返回true，否则返回false
     */
    boolean check(Level world, BlockPos startPos);

    /**
     * 计算多方块结构的核心位置。
     * @param startPos 多方块结构起始位置
     * @return 核心位置
     */
    BlockPos calculateCorePosition(BlockPos startPos);

}