package kivo.millennium.millind.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class DynamicMultiblock extends AbstractMultiblock {
    protected final Vec3i minBox;
    protected final Vec3i maxBox;

    public DynamicMultiblock(Vec3i minBox, Vec3i maxBox) {
        this.minBox = minBox;
        this.maxBox = maxBox;
    }

    private Vec3i determineActualSize(Level world, BlockPos startPos) {
        // 实现逻辑以确定当前结构的实际尺寸
        // 这里仅作为示例返回一个固定值
        return new Vec3i(3, 3, 3);
    }
}