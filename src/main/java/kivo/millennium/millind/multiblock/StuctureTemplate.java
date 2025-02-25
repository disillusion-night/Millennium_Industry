package kivo.millennium.millind.multiblock;

import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static kivo.millennium.millind.multiblock.Block3DMap.getConfiguredMaxThreads;

public class StuctureTemplate {

    private final BlockValidator[][][] validatorGrid;
    private final int width;
    private final int height;
    private final int depth;

    /**
     * 构造方法，用于创建 StructureTemplate 对象。
     *
     * @param width  结构模板的宽度（X 轴尺寸）。
     * @param height 结构模板的高度（Y 轴尺寸）。
     * @param depth  结构模板的深度（Z 轴尺寸）。
     * @throws IllegalArgumentException 如果宽度、高度或深度小于等于 0。
     */
    public StuctureTemplate(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.validatorGrid = new BlockValidator[width][height][depth];
    }

    /**
     * 获取指定坐标的 BlockValidator 对象。
     *
     * @param x X 轴坐标 (0-indexed)。
     * @param y Y 轴坐标 (0-indexed)。
     * @param z Z 轴坐标 (0-indexed)。
     * @return 指定坐标的 BlockValidator 对象。
     * @throws ArrayIndexOutOfBoundsException 如果坐标超出结构模板的边界。
     */
    public BlockValidator getValidator(int x, int y, int z) {
        checkBounds(x, y, z);
        return validatorGrid[x][y][z];
    }

    /**
     * 设置指定坐标的 BlockValidator 对象。
     *
     * @param x         X 轴坐标 (0-indexed)。
     * @param y         Y 轴坐标 (0-indexed)。
     * @param z         Z 轴坐标 (0-indexed)。
     * @param validator 要设置的 BlockValidator 对象。
     * @throws ArrayIndexOutOfBoundsException 如果坐标超出结构模板的边界。
     * @throws NullPointerException           如果 validator 为 null。
     */
    public void setValidator(int x, int y, int z, BlockValidator validator) {
        checkBounds(x, y, z);
        if (validator == null) {
            throw new NullPointerException("BlockValidator 不能为空");
        }
        validatorGrid[x][y][z] = validator;
    }

    /**
     * 获取结构模板的宽度。
     *
     * @return 结构模板的宽度。
     */
    public int getWidth() {
        return width;
    }

    /**
     * 获取结构模板的高度。
     *
     * @return 结构模板的高度。
     */
    public int getHeight() {
        return height;
    }

    /**
     * 获取结构模板的深度。
     *
     * @return 结构模板的深度。
     */
    public int getDepth() {
        return depth;
    }

    /**
     * 检查给定的坐标是否在结构模板的边界内。
     * 如果坐标超出边界，抛出 ArrayIndexOutOfBoundsException 异常。
     *
     * @param x X 轴坐标。
     * @param y Y 轴坐标。
     * @param z Z 轴坐标。
     * @throws ArrayIndexOutOfBoundsException 如果坐标超出边界。
     */
    private void checkBounds(int x, int y, int z) {
        if (x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= depth) {
            throw new ArrayIndexOutOfBoundsException(String.format("坐标 (%d, %d, %d) 超出结构模板边界 [宽度:%d, 高度:%d, 深度:%d]", x, y, z, width, height, depth));
        }
    }


    /**
     * 多线程方法，对比一个 Block3DMap 是否与当前 StructureTemplate 匹配。
     * 使用多线程并行处理不同区域，提高对比速度。
     * 线程数和区域切分方向配置与 Block3DMap 创建方法保持一致。
     *
     * @param block3DMap 需要对比的 Block3DMap 对象。
     * @return 如果 Block3DMap 与 StructureTemplate 匹配，返回 true；否则返回 false。
     * @throws NullPointerException 如果 block3DMap 为 null。
     * @throws RuntimeException       如果多线程处理过程中发生异常。
     */
    public boolean isTemplateMatchMultithreaded(Block3DMap block3DMap) {
        if (block3DMap == null) {
            throw new NullPointerException("Block3DMap 不能为空");
        }
        if (block3DMap.getWidth() != width || block3DMap.getHeight() != height || block3DMap.getDepth() != depth) {
            return false; // 尺寸不一致，直接返回 false
        }

        int maxConfiguredThreads = getConfiguredMaxThreads(); // 复用 Block3DMap 的配置读取方法
        int numberOfThreads = Math.max(1, maxConfiguredThreads > 0 ? maxConfiguredThreads : Runtime.getRuntime().availableProcessors() / 2);
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<ChunkComparisonResult>> futures = new ArrayList<>();

        // 沿 Y 轴切分区域，与 Block3DMap 创建方法保持一致
        int chunkSizeY = Math.max(1, height / numberOfThreads);
        int chunkCountY = (int) Math.ceil((double) height / chunkSizeY);

        for (int chunkIndexY = 0; chunkIndexY < chunkCountY; chunkIndexY++) {
            int startY = chunkIndexY * chunkSizeY;
            int endY = Math.min(startY + chunkSizeY, height);

            Callable<ChunkComparisonResult> callableTask = () -> {
                boolean chunkMatch = true; // 初始假设当前区块匹配
                for (int x = 0; x < width; x++) {
                    for (int y = startY; y < endY; y++) {
                        for (int z = 0; z < depth; z++) {
                            BlockValidator validator = getValidator(x, y, z);
                            BlockState blockState = block3DMap.getBlockState(x, y, z);
                            if (validator != null && blockState != null) { // 只有当模板定义了验证器且 Block3DMap 中有方块时才进行验证
                                if (!validator.isValid(blockState)) {
                                    chunkMatch = false; // 发现不匹配的方块，当前区块不匹配
                                    break; // 提前结束内层循环，无需继续检查当前区块
                                }
                            } else if (validator != null && blockState == null) {
                                chunkMatch = false; // 模板定义了验证器，但 Block3DMap 对应位置为空，不匹配
                                break;
                            } else if (validator == null && blockState != null) {
                                // 模板未定义验证器，但 Block3DMap 中有方块,  在这种设计下，我们认为模板未指定的位置可以是任意方块(或空气)，所以不算不匹配
                                continue; // 继续检查下一个方块
                            } // validator == null && blockState == null: 模板未定义验证器，Block3DMap 对应位置也为空，也算匹配，继续检查

                        }
                        if (!chunkMatch) break; // 如果当前行已不匹配，提前结束当前层循环
                    }
                    if (!chunkMatch) break; // 如果当前层已不匹配，提前结束外层循环
                }
                return new ChunkComparisonResult(chunkMatch); // 返回区块对比结果
            };
            futures.add(executorService.submit(callableTask));
        }

        boolean overallMatch = true; // 初始假设整体匹配
        try {
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            // 合并所有区块的对比结果
            for (Future<ChunkComparisonResult> future : futures) {
                ChunkComparisonResult chunkResult = future.get();
                if (!chunkResult.isChunkMatch()) {
                    overallMatch = false; // 只要有一个区块不匹配，整体就不匹配
                    break; // 提前结束合并循环，无需继续检查
                }
            }

        } catch (InterruptedException | ExecutionException e) {
            executorService.shutdownNow();
            throw new RuntimeException("多线程对比失败", e);
        }

        return overallMatch;
    }
    /**
     * 辅助类，用于封装区块对比结果 (现在是 Y 轴切分).
     */
    private static class ChunkComparisonResult {
        private final boolean chunkMatch; // 当前区块是否匹配模板

        public ChunkComparisonResult(boolean chunkMatch) {
            this.chunkMatch = chunkMatch;
        }

        public boolean isChunkMatch() {
            return chunkMatch;
        }
    }

}