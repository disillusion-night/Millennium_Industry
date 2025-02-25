package kivo.millennium.millind.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Block3DMap {
    private final BlockState[][][] blockStates;
    private final int width;
    private final int height;
    private final int depth;

    /**
     * 构造方法，创建一个指定尺寸的 Block3DMap。
     *
     * @param width  宽度 (X 轴尺寸).
     * @param height 高度 (Y 轴尺寸).
     * @param depth  深度 (Z 轴尺寸).
     * @throws IllegalArgumentException 如果宽度、高度或深度小于等于 0。
     */
    public Block3DMap(int width, int height, int depth) {
        if (width <= 0 || height <= 0 || depth <= 0) {
            throw new IllegalArgumentException("宽度、高度和深度必须都大于 0");
        }
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.blockStates = new BlockState[width][height][depth];
    }

    /**
     * 获取 Block3DMap 的宽度。
     *
     * @return 宽度。
     */
    public int getWidth() {
        return width;
    }

    /**
     * 获取 Block3DMap 的高度。
     *
     * @return 高度。
     */
    public int getHeight() {
        return height;
    }

    /**
     * 获取 Block3DMap 的深度。
     *
     * @return 深度。
     */
    public int getDepth() {
        return depth;
    }

    /**
     * 设置指定坐标的方块状态。
     *
     * @param x     X 轴坐标 (0-indexed，范围 0 到 width-1).
     * @param y     Y 轴坐标 (0-indexed，范围 0 到 height-1).
     * @param z     Z 轴坐标 (0-indexed，范围 0 到 depth-1).
     * @param state 要设置的方块状态。
     * @throws ArrayIndexOutOfBoundsException 如果坐标超出 Block3DMap 的边界。
     * @throws NullPointerException           如果 state 为 null。
     */
    public void setBlockState(int x, int y, int z, BlockState state) {
        checkBounds(x, y, z);
        if (state == null) {
            throw new NullPointerException("方块状态不能为空");
        }
        blockStates[x][y][z] = state;
    }

    /**
     * 获取指定坐标的方块状态。
     *
     * @param x X 轴坐标 (0-indexed).
     * @param y Y 轴坐标 (0-indexed).
     * @param z Z 轴坐标 (0-indexed).
     * @return 指定坐标的方块状态。
     * @throws ArrayIndexOutOfBoundsException 如果坐标超出 Block3DMap 的边界。
     */
    public BlockState getBlockState(int x, int y, int z) {
        checkBounds(x, y, z);
        return blockStates[x][y][z];
    }

    /**
     * 从 Minecraft 世界创建一个 Block3DMap 对象。
     *
     * @param level    Minecraft 世界的 Level 对象。
     * @param startPos 长方体区域的起始坐标。
     * @param width    区域宽度。
     * @param height   区域高度。
     * @param depth    区域深度。
     * @return 从世界数据创建的 Block3DMap 对象。
     * @throws IllegalArgumentException 如果宽度、高度或深度小于等于 0。
     * @throws NullPointerException       如果 level 或 startPos 为 null。
     */
    public static Block3DMap createFromWorld(Level level, BlockPos startPos, int width, int height, int depth) {
        if (level == null) {
            throw new NullPointerException("Level 不能为空");
        }
        if (startPos == null) {
            throw new NullPointerException("起始坐标不能为空");
        }
        Block3DMap block3DMap = new Block3DMap(width, height, depth);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    BlockPos currentPos = startPos.offset(x, y, z);
                    block3DMap.setBlockState(x, y, z, level.getBlockState(currentPos));
                }
            }
        }
        return block3DMap;
    }

    private static final String MAX_THREADS_CONFIG_KEY = "maxBlock3DMapThreads";

    /**
     * 多线程版本：从 Minecraft 世界创建一个 Block3DMap 对象。
     * 使用多线程并行处理不同区域，提高创建速度。
     * 线程数和区域切分方向已配置化。
     *
     * @param level    Minecraft 世界的 Level 对象。
     * @param startPos 长方体区域的起始坐标。
     * @param width    区域宽度。
     * @param height   区域高度。
     * @param depth    区域深度。
     * @return 从世界数据创建的 Block3DMap 对象。
     * @throws IllegalArgumentException 如果宽度、高度或深度小于等于 0。
     * @throws NullPointerException       如果 level 或 startPos 为 null。
     * @throws RuntimeException         如果多线程处理过程中发生异常。
     */
    public static Block3DMap createFromWorldMultithreaded(Level level, BlockPos startPos, int width, int height, int depth) {
        if (level == null) {
            throw new NullPointerException("Level 不能为空");
        }
        if (startPos == null) {
            throw new NullPointerException("起始坐标不能为空");
        }
        if (width <= 0 || height <= 0 || depth <= 0) {
            throw new IllegalArgumentException("宽度、高度和深度必须都大于 0");
        }

        Block3DMap block3DMap = new Block3DMap(width, height, depth);

        int maxConfiguredThreads = getConfiguredMaxThreads();
        int numberOfThreads = Math.max(1, maxConfiguredThreads > 0 ? maxConfiguredThreads : Runtime.getRuntime().availableProcessors() / 2);

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<ChunkData>> futures = new ArrayList<>();

        int chunkSizeY = Math.max(1, height / numberOfThreads); // 每个线程处理的 Y 轴高度
        int chunkCountY = (int) Math.ceil((double) height / chunkSizeY); // 需要处理的 Y 轴区块数量

        for (int chunkIndexY = 0; chunkIndexY < chunkCountY; chunkIndexY++) {
            int startY = chunkIndexY * chunkSizeY;
            int endY = Math.min(startY + chunkSizeY, height);

            Callable<ChunkData> callableTask = () -> {
                BlockState[][][] chunkBlockStates = new BlockState[width][endY - startY][depth]; // 注意 Y 轴尺寸的变化
                for (int x = 0; x < width; x++) {
                    for (int y = startY; y < endY; y++) { // Y 轴循环范围调整
                        for (int z = 0; z < depth; z++) {
                            BlockPos currentPos = startPos.offset(x, y, z);
                            chunkBlockStates[x][y - startY][z] = level.getBlockState(currentPos); // 注意 Y 轴索引的调整
                        }
                    }
                }
                return new ChunkData(startY, endY, chunkBlockStates); // 返回区块数据和起始/结束 Y 坐标
            };
            futures.add(executorService.submit(callableTask));
        }

        try {
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            // 合并所有区块的结果
            for (Future<ChunkData> future : futures) {
                ChunkData chunkData = future.get();
                int startY = chunkData.startY;
                BlockState[][][] chunkBlockStates = chunkData.blockStates;
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < chunkData.blockStates[0].length; y++) { // 使用区块的高度
                        for (int z = 0; z < depth; z++) {
                            block3DMap.setBlockState(x, startY + y, z, chunkBlockStates[x][y][z]); // Y 轴偏移调整
                        }
                    }
                }
            }

        } catch (InterruptedException | ExecutionException e) {
            executorService.shutdownNow();
            throw new RuntimeException("多线程创建 Block3DMap 失败", e);
        }

        return block3DMap;
    }

    /**
     * 辅助方法，用于模拟从配置文件中读取最大线程数。
     * 在实际项目中，你需要使用你的 Mod 的配置系统来加载配置值。
     * 例如，使用 Forge 的 @ModConfig.Config 机制。
     *
     * @return 配置的最大线程数，如果配置未设置或无效，则返回 0 (表示使用默认策略)。
     */
    static int getConfiguredMaxThreads() {
        //  ***  重要:  ***
        //  以下代码仅为示例，你需要替换为你的实际配置加载逻辑。
        //  你需要从你的 Mod 的配置文件中读取 MAX_THREADS_CONFIG_KEY 对应的配置值。

        //  示例：假设你有一个 ConfigManager 类来处理配置
        //  return ConfigManager.getIntConfig(MAX_THREADS_CONFIG_KEY);

        //  更简化的示例，直接返回一个固定的配置值或从系统属性/环境变量读取
        //  例如，从系统属性 "maxBlock3DMapThreads" 读取：
        //  String configValue = System.getProperty(MAX_THREADS_CONFIG_KEY);
        //  if (configValue != null) {
        //      try {
        //          return Integer.parseInt(configValue);
        //      } catch (NumberFormatException e) {
        //          // 配置值无效，忽略并使用默认值
        //      }
        //  }
        //  return 0; // 默认值：表示不限制最大线程数，使用默认策略

        //  ***  请根据你的实际配置系统实现配置加载  ***
        return 4; //  这里为了演示，直接返回一个固定的值 4，你需要替换成真正的配置读取逻辑
    }


    /**
     * 辅助类，用于封装区块数据及其起始和结束 Y 坐标 (现在是 Y 轴切分).
     */
    private static class ChunkData {
        public final int startY; // 现在是 Y 轴的起始坐标
        public final int endY;   // 现在是 Y 轴的结束坐标
        public final BlockState[][][] blockStates;

        public ChunkData(int startY, int endY, BlockState[][][] blockStates) {
            this.startY = startY;
            this.endY = endY;
            this.blockStates = blockStates;
        }
    }

    /**
     * 检查给定的坐标是否在 Block3DMap 的边界内。
     * 如果坐标超出边界，抛出 ArrayIndexOutOfBoundsException 异常。
     *
     * @param x X 轴坐标。
     * @param y Y 轴坐标。
     * @param z Z 轴坐标。
     * @throws ArrayIndexOutOfBoundsException 如果坐标超出边界。
     */
    private void checkBounds(int x, int y, int z) {
        if (x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= depth) {
            throw new ArrayIndexOutOfBoundsException(String.format("坐标 (%d, %d, %d) 超出 Block3DMap 边界 [宽度:%d, 高度:%d, 深度:%d]", x, y, z, width, height, depth));
        }
    }
}
