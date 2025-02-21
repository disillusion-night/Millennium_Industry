package kivo.millennium.millind.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class StaticMultiblock extends AbstractMultiblock{
    protected final Vec3i box;
    protected final BlockTypeValidator[][][] template;
    
    public StaticMultiblock(Vec3i box, BlockTypeValidator[][][] Block3dmap) {
        this.box = box;
        this.template = Block3dmap;
    }

    /**
     * 获取从startPos开始的所有方块的状态，按照X, Y, Z轴组织。
     */
    public BlockState[][][] getAllBlockStatesAs3DArray(Level world, BlockPos startPos) {
        BlockState[][][] BlockStates = new BlockState[box.getX()][box.getY()][box.getZ()];

        for (int y = 0; y < box.getY(); y++) {
            for (int x = 0; x < box.getX(); x++) {
                for (int z = 0; z < box.getZ(); z++) {
                    BlockPos currentPos = startPos.offset(x, y, z);
                    BlockStates[x][y][z] = world.getBlockState(currentPos);
                }
            }
        }

        return BlockStates;
    }

    /**
     * 根据原始坐标和起始点坐标获取在三维数组中的索引。
     */
    public static int[] getIndicesFromPosition(BlockPos startPos, BlockPos pos) {
        return new int[]{pos.getX() - startPos.getX(), pos.getY() - startPos.getY(), pos.getZ() - startPos.getZ()};
    }

    /**
     * 根据三维数组中的索引和起始点坐标获取原始坐标。
     */
    public static BlockPos getPositionFromIndices(BlockPos startPos, int x, int y, int z) {
        return startPos.offset(x, y, z);
    }

    /**
     * 对三维数组进行90度顺时针旋转（绕Y轴）。
     */
    public static BlockTypeValidator[][][] rotate90Degrees(BlockTypeValidator[][][] array) {
        int xLen = array.length;
        int zLen = array[0][0].length;
        int yLen = array[0].length;

        BlockTypeValidator[][][] rotated = new BlockTypeValidator[zLen][yLen][xLen];
        for (int y = 0; y < yLen; y++) {
            for (int x = 0; x < xLen; x++) {
                for (int z = 0; z < zLen; z++) {
                    rotated[z][y][xLen - 1 - x] = array[x][y][z];
                }
            }
        }

        return rotated;
    }

    @Override
    public BlockPos calculateCorePosition(BlockPos startPos) {
        int centerX = box.getX() / 2;
        int centerZ = box.getZ() / 2;
        return startPos.offset(centerX, 0, centerZ); // 假设startPos是底面左下角的坐标
    }
}