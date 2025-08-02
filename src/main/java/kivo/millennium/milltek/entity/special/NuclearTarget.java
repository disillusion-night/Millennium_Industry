package kivo.millennium.milltek.entity.special;

import kivo.millennium.milltek.init.MillenniumEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class NuclearTarget extends Entity {
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(NuclearTarget.class, EntityDataSerializers.FLOAT);
    
    // 爆炸伤害常量
    protected static final float DAMAGE_CORE = Float.MAX_VALUE;    // 核心伤害
    protected static final float DAMAGE_1 = 32768F;               // 100格内伤害
    protected static final float DAMAGE_2 = 1000F;                // 200格内伤害
    protected static final float DAMAGE_3 = 200F;                 // 500格内伤害
    
    public static final int EXPLOSION_TIME = 280; // 14秒 = 14 * 20 tick
    
    private List<Entity> trackingEntities = new ArrayList<>();
    private LivingEntity owner;
    private int age = 0;
    
    // 椭球体爆炸范围参数
    private static final float radius_x = 64F; // X轴半径（长轴）
    private static final float radius_y = 32F; // Y轴半径（短轴，垂直方向）
    private static final float radius_z = 64F; // Z轴半径（长轴）
    
    // 水蒸发椭球体范围参数
    private static final float water_radius_x = 200F; // X轴半径
    private static final float water_radius_y = 100F; // Y轴半径（垂直方向较小）
    private static final float water_radius_z = 200F; // Z轴半径
    
    // 方块破坏队列相关
    private static final int BATCH_SIZE = 32767; // 每tick处理的方块数量
    private PriorityQueue<DestructionTask> destructionQueue = new PriorityQueue<>();
    private boolean explosionTriggered = false;
    
    // 概率控制参数 - 重构为每个任务类型独立控制
    private static class ProbabilityConfig {
        private final double coreProbability;  // 核心圈概率
        private final double edgeProbability;  // 边缘概率
        
        public ProbabilityConfig(double coreProbability, double edgeProbability) {
            this.coreProbability = Math.max(0.0, Math.min(1.0, coreProbability));
            this.edgeProbability = Math.max(0.0, Math.min(1.0, edgeProbability));
        }
        
        public double getCoreProbability() { return coreProbability; }
        public double getEdgeProbability() { return edgeProbability; }
    }
    
    // 为每个任务类型配置独立的概率范围
    private static final Map<BlockDestroyType, ProbabilityConfig> PROBABILITY_CONFIGS;
    
    static {
        Map<BlockDestroyType, ProbabilityConfig> configs = new java.util.HashMap<>();
        configs.put(BlockDestroyType.EXPLOSION, new ProbabilityConfig(1.0, 1.0));
        configs.put(BlockDestroyType.ICE_MELT, new ProbabilityConfig(1.0, 1.0));
        configs.put(BlockDestroyType.SNOW_MELT, new ProbabilityConfig(1.0, 1.0));           
        configs.put(BlockDestroyType.REMOVE_VEGETATION, new ProbabilityConfig(1.0, 1.0));   
        configs.put(BlockDestroyType.STONE_TO_GRAVEL, new ProbabilityConfig(0.8, 0.3));     // 中等概率
        configs.put(BlockDestroyType.TERRAIN_CHANGE, new ProbabilityConfig(1.0, 1.0));      // 中等概率
        configs.put(BlockDestroyType.SAND_TO_GLASS, new ProbabilityConfig(0.6, 0.2));       // 较低概率
        configs.put(BlockDestroyType.WOOD_PROCESS, new ProbabilityConfig(0.7, 0.5));        // 中等概率
        configs.put(BlockDestroyType.LEAVES_BURN, new ProbabilityConfig(1, 1));
        configs.put(BlockDestroyType.NORMAL, new ProbabilityConfig(1, 0.1));
        PROBABILITY_CONFIGS = java.util.Collections.unmodifiableMap(configs);
    }
    
    // 增强的方块破坏任务结构体
    private static class DestructionTask implements Comparable<DestructionTask> {
        public final BlockPos pos;
        public final double distanceSquared;
        public final double explosionEllipsoidDistance; // 在爆炸椭球体中的标准化距离
        public final double waterEllipsoidDistance;     // 在水蒸发椭球体中的标准化距离
        public final BlockDestroyType type;
        
        public DestructionTask(BlockPos pos, double distanceSquared, 
                              double explosionEllipsoidDistance, double waterEllipsoidDistance,
                              BlockDestroyType type) {
            this.pos = pos;
            this.distanceSquared = distanceSquared;
            this.explosionEllipsoidDistance = explosionEllipsoidDistance;
            this.waterEllipsoidDistance = waterEllipsoidDistance;
            this.type = type;
        }
        
        /**
         * 计算实际执行概率
         * @return 0.0 到 1.0 之间的概率值
         */
        public double calculateProbability() {
            // 获取该任务类型的概率配置
            ProbabilityConfig config = PROBABILITY_CONFIGS.get(type);
            if (config == null) {
                // 默认配置：中等概率
                config = new ProbabilityConfig(0.8, 0.3);
            }
            
            double coreProbability = config.getCoreProbability();
            double edgeProbability = config.getEdgeProbability();
            
            // 如果核心概率和边缘概率都是1.0，则为无条件执行
            if (coreProbability >= 1.0 && edgeProbability >= 1.0) {
                return 1.0;
            }
            
            // 计算在影响范围内的线性概率
            double probability;
            
            if (explosionEllipsoidDistance <= 1.0) {
                // 在爆炸椭球体内，使用核心概率
                probability = coreProbability;
            } else {
                // 在爆炸椭球体外，水蒸发椭球体内
                // 使用线性插值：核心概率 -> 边缘概率
                double normalizedDistance = waterEllipsoidDistance; // 0.0(中心) 到 1.0(边缘)
                probability = coreProbability + (edgeProbability - coreProbability) * normalizedDistance;
            }
            
            return Math.max(0.0, Math.min(1.0, probability));
        }
        
        @Override
        public int compareTo(DestructionTask other) {
            // 按距离排序，距离近的先执行
            return Double.compare(this.distanceSquared, other.distanceSquared);
        }
    }
    
    // 方块破坏类型
    private enum BlockDestroyType {
        EXPLOSION,          // 爆炸破坏
        TERRAIN_CHANGE,     // 地形变化（泥土->沙子/沙砾）
        STONE_TO_GRAVEL,    // 石头->沙砾
        SAND_TO_GLASS,      // 沙子->玻璃
        REMOVE_VEGETATION,  // 移除植被
        WOOD_PROCESS,       // 木制品处理（移除或点燃）
        ICE_MELT,           // 冰块融化
        SNOW_MELT,          // 雪融化
        LEAVES_BURN,        // 树叶燃烧
        NORMAL // 其他方块处理
    }

    public NuclearTarget(EntityType<? extends Entity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public NuclearTarget(Level pLevel, LivingEntity owner) {
        this(MillenniumEntities.BLACK_HOLE.get(), pLevel); // 暂时使用BLACK_HOLE的EntityType
        setOwner(owner);
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }

    public LivingEntity getOwner() {
        return this.owner;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_RADIUS, 500F); // 核爆伤害范围500格
    }

    @Override
    @Nonnull
    public EntityDimensions getDimensions(@Nonnull Pose pPose) {
        // 使用最大的椭球体半径作为实体尺寸
        float maxRadius = Math.max(Math.max(water_radius_x, water_radius_y), water_radius_z);
        return EntityDimensions.scalable(maxRadius * 2.0F, maxRadius * 2.0F);
    }

    @Override
    public void onSyncedDataUpdated(@Nonnull EntityDataAccessor<?> pKey) {
        if (DATA_RADIUS.equals(pKey)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(pKey);
    }

    @Override
    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    @Override
    public void tick() {
        super.tick();
        
        // 增加年龄
        age++;
        
        // 14秒后触发核爆
        if (age >= EXPLOSION_TIME && !explosionTriggered) {
            explosionTriggered = true;
            prepareDestruction();
            damageEntities();
        }
        
        // 处理方块破坏队列
        if (explosionTriggered) {
            processDestructionQueue();
            
            // 如果队列空了，移除实体
            if (destructionQueue.isEmpty()) {
                this.discard();
            }
        }
    }

    private void prepareDestruction() {
        BlockPos center = this.blockPosition();
        
        // 计算最大范围，使用两个椭球体中较大的范围
        int maxRadius = (int) Math.max(
            Math.max(Math.max(radius_x, radius_y), radius_z),
            Math.max(Math.max(water_radius_x, water_radius_y), water_radius_z)
        );
        
        // 一次性遍历所有方块，根据不同椭球体范围进行分类处理
        for (int x = -maxRadius; x <= maxRadius; x++) {
            for (int y = -maxRadius; y <= maxRadius; y++) {
                for (int z = -maxRadius; z <= maxRadius; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState tgt = level().getBlockState(pos);
                    if (tgt.isAir() || tgt.getDestroySpeed(level(), pos) < 0) continue; //跳过空气和不可破坏方块减少计算
                    double distanceSquared = x * x + y * y + z * z;
                    
                    // 检查是否在爆炸椭球体内
                    double explosionEllipsoidValue = (x * x) / (radius_x * radius_x) + 
                                                    (y * y) / (radius_y * radius_y) + 
                                                    (z * z) / (radius_z * radius_z);
                    
                    // 检查是否在水蒸发椭球体内
                    double waterEllipsoidValue = (x * x) / (water_radius_x * water_radius_x) + 
                                                (y * y) / (water_radius_y * water_radius_y) + 
                                                (z * z) / (water_radius_z * water_radius_z);
                    
                    // 爆炸范围内的方块破坏
                    if (explosionEllipsoidValue <= 1.0) {
                        if (tgt.getBlock() == Blocks.TALL_SEAGRASS || tgt.getBlock() == Blocks.SEAGRASS ||
                                tgt.getBlock() == Blocks.KELP_PLANT || tgt.getBlock() == Blocks.KELP ||
                                tgt.getBlock() == Blocks.WATER || tgt.getBlock() == Blocks.BUBBLE_COLUMN ||
                                tgt.is(BlockTags.CORALS)) {
                            // 立即蒸干水，避免后续处理中的潜在bug
                            level().setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                        }
                        // 含水方块脱水
                        else if (tgt.hasProperty(BlockStateProperties.WATERLOGGED)){
                            level().setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                        } else {
                            destructionQueue.offer(new DestructionTask(pos, distanceSquared,
                                    explosionEllipsoidValue, waterEllipsoidValue,
                                    BlockDestroyType.EXPLOSION));
                        }
                    }
                    // 水蒸发和地形变化范围
                    else if (waterEllipsoidValue <= 1.0) {
                        if (tgt.getBlock() == Blocks.TALL_SEAGRASS || tgt.getBlock() == Blocks.SEAGRASS ||
                                tgt.getBlock() == Blocks.KELP_PLANT || tgt.getBlock() == Blocks.KELP ||
                                tgt.getBlock() == Blocks.WATER || tgt.getBlock() == Blocks.BUBBLE_COLUMN ||
                                tgt.is(BlockTags.CORALS)
                        ) {
                            // 立即蒸干水，避免后续处理中的潜在bug
                            level().setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                        }
                        // 含水方块脱水
                        else if (tgt.hasProperty(BlockStateProperties.WATERLOGGED) &&
                        tgt.getValue(BlockStateProperties.WATERLOGGED)) {
                                BlockState newState = tgt.setValue(BlockStateProperties.WATERLOGGED, false);
                                level().setBlock(pos, newState, 2);
                        }
                        // 石头相关方块 -> 沙砾
                        else if (tgt.is(BlockTags.STONE_ORE_REPLACEABLES) || 
                                 tgt.getBlock() == Blocks.STONE || tgt.getBlock() == Blocks.COBBLESTONE ||
                                 tgt.getBlock() == Blocks.DEEPSLATE || tgt.getBlock() == Blocks.COBBLED_DEEPSLATE) {
                            destructionQueue.offer(new DestructionTask(pos, distanceSquared,
                                explosionEllipsoidValue, waterEllipsoidValue,
                                BlockDestroyType.STONE_TO_GRAVEL));
                        }
                        // 草方块等 -> 泥土/沙子/沙砾
                        else if (tgt.getBlock() == Blocks.GRASS_BLOCK || tgt.getBlock() == Blocks.MYCELIUM ||
                                 tgt.getBlock() == Blocks.PODZOL || tgt.getBlock() == Blocks.COARSE_DIRT ||
                                 tgt.getBlock() == Blocks.ROOTED_DIRT || tgt.getBlock() == Blocks.DIRT ||
                                tgt.getBlock() == Blocks.FARMLAND || tgt.getBlock() == Blocks.DIRT_PATH) {
                            destructionQueue.offer(new DestructionTask(pos, distanceSquared,
                                explosionEllipsoidValue, waterEllipsoidValue,
                                BlockDestroyType.TERRAIN_CHANGE));
                        }
                        // 沙子 -> 玻璃
                        else if (tgt.getBlock() == Blocks.SAND || tgt.getBlock() == Blocks.RED_SAND) {
                            destructionQueue.offer(new DestructionTask(pos, distanceSquared,
                                explosionEllipsoidValue, waterEllipsoidValue,
                                BlockDestroyType.SAND_TO_GLASS));
                        }
                        // 植被移除（不包括树叶，树叶单独处理）
                        else if (tgt.is(BlockTags.FLOWERS) || tgt.is(BlockTags.SAPLINGS) || 
                                 tgt.is(BlockTags.CROPS) || tgt.getBlock() == Blocks.GRASS ||
                                 tgt.getBlock() == Blocks.TALL_GRASS || tgt.getBlock() == Blocks.FERN ||
                                 tgt.getBlock() == Blocks.LARGE_FERN || tgt.getBlock() == Blocks.DEAD_BUSH ||
                                 tgt.getBlock() == Blocks.BROWN_MUSHROOM || tgt.getBlock() == Blocks.RED_MUSHROOM ||
                                 tgt.getBlock() == Blocks.SUGAR_CANE || tgt.getBlock() == Blocks.GRASS
                        ) {

                            destructionQueue.offer(new DestructionTask(pos, distanceSquared,
                                explosionEllipsoidValue, waterEllipsoidValue,
                                BlockDestroyType.REMOVE_VEGETATION));
                        }
                        // 木制方块处理
                        else if (tgt.is(BlockTags.LOGS) || tgt.is(BlockTags.PLANKS) || 
                                 tgt.is(BlockTags.WOODEN_FENCES) || tgt.is(BlockTags.WOODEN_DOORS) ||
                                 tgt.is(BlockTags.WOODEN_STAIRS) || tgt.is(BlockTags.WOODEN_SLABS)) {
                            destructionQueue.offer(new DestructionTask(pos, distanceSquared,
                                explosionEllipsoidValue, waterEllipsoidValue,
                                BlockDestroyType.WOOD_PROCESS));
                        }
                        // 冰块融化
                        else if (tgt.getBlock() == Blocks.ICE || tgt.getBlock() == Blocks.PACKED_ICE ||
                                 tgt.getBlock() == Blocks.BLUE_ICE || tgt.getBlock() == Blocks.FROSTED_ICE) {
                            destructionQueue.offer(new DestructionTask(pos, distanceSquared,
                                explosionEllipsoidValue, waterEllipsoidValue,
                                BlockDestroyType.ICE_MELT));
                        }
                        // 雪融化
                        else if (tgt.getBlock() == Blocks.SNOW || tgt.getBlock() == Blocks.SNOW_BLOCK ||
                                 tgt.getBlock() == Blocks.POWDER_SNOW) {
                            destructionQueue.offer(new DestructionTask(pos, distanceSquared,
                                explosionEllipsoidValue, waterEllipsoidValue,
                                BlockDestroyType.SNOW_MELT));
                        }
                        // 树叶燃烧
                        else if (tgt.is(BlockTags.LEAVES)) {
                            destructionQueue.offer(new DestructionTask(pos, distanceSquared,
                                explosionEllipsoidValue, waterEllipsoidValue,
                                BlockDestroyType.LEAVES_BURN));
                        }

                    }
                }
            }
        }
    }

    private void processDestructionQueue() {
        if (level().isClientSide) return;
        
        // 处理破坏队列
        int processed = 0;
        while (!destructionQueue.isEmpty() && processed < BATCH_SIZE) {
            DestructionTask task = destructionQueue.poll();
            
            // 统一进行概率判定（包括无条件任务，它们的概率为1.0）
            double probability = task.calculateProbability();
            if (level().random.nextDouble() > probability) {
                processed++;
                continue; // 概率判定失败，跳过
            }
            
            // 执行具体的方块操作
            switch (task.type) {
                case EXPLOSION:
                    level().setBlock(task.pos, Blocks.AIR.defaultBlockState(), 2);
                    break;
                    
                case TERRAIN_CHANGE:
                    // 泥土类方块替换为沙子或沙砾
                    if (level().random.nextFloat() < 0.7) {
                        level().setBlock(task.pos, Blocks.SAND.defaultBlockState(), 2);
                    } else {
                        level().setBlock(task.pos, Blocks.GRAVEL.defaultBlockState(), 2);
                    }
                    break;
                    
                case STONE_TO_GRAVEL:
                    level().setBlock(task.pos, Blocks.GRAVEL.defaultBlockState(), 2);
                    break;
                    
                case SAND_TO_GLASS:
                    // 沙子替换为玻璃
                    BlockState currentState = level().getBlockState(task.pos);
                    if (currentState.getBlock() == Blocks.SAND) {
                        level().setBlock(task.pos, Blocks.GLASS.defaultBlockState(), 2);
                    } else if (currentState.getBlock() == Blocks.RED_SAND) {
                        level().setBlock(task.pos, Blocks.ORANGE_STAINED_GLASS.defaultBlockState(), 2);
                    }
                    break;
                    
                case REMOVE_VEGETATION:
                    level().setBlock(task.pos, Blocks.AIR.defaultBlockState(), 2);
                    break;
                    
                case WOOD_PROCESS:
                    // 木制方块处理
                    if (level().random.nextFloat() < 0.3) {
                        // 30%概率变为火
                        level().setBlock(task.pos, Blocks.FIRE.defaultBlockState(), 2);
                    } else {
                        // 70%概率移除
                        level().setBlock(task.pos, Blocks.AIR.defaultBlockState(), 2);
                    }
                    break;
                    
                case ICE_MELT:
                    level().setBlock(task.pos, Blocks.AIR.defaultBlockState(), 2);
                    break;
                    
                case SNOW_MELT:
                    level().setBlock(task.pos, Blocks.AIR.defaultBlockState(), 2);
                    break;
                    
                case LEAVES_BURN:
                    level().setBlock(task.pos, Blocks.AIR.defaultBlockState(), 2);
                    break;
                    
                default:
                    break;
            }
            processed++;
        }
    }

    private void damageEntities() {
        for (Entity entity : level().getEntities(this, this.getBoundingBox().inflate(500))) {
            if (entity instanceof LivingEntity livingEntity) {
                Vec3 vec = livingEntity.position().subtract(this.position());
                double distance = vec.length();
                float damage = 0;

                // 根据距离计算伤害
                if (distance < 1.0) {
                    damage = DAMAGE_CORE;
                } else if (distance < 100.0) {
                    damage = DAMAGE_1;
                } else if (distance < 200.0) {
                    damage = DAMAGE_2;
                } else if (distance < 500.0) {
                    damage = DAMAGE_3;
                }

                if (damage > 0) {
                    DamageSource damageSource = level().damageSources().explosion(this, this);
                    livingEntity.hurt(damageSource, damage);
                }
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(@Nonnull CompoundTag pCompound) {
        pCompound.putInt("Age", this.age);
        pCompound.putBoolean("ExplosionTriggered", this.explosionTriggered);
        // 保存所有者信息（如果需要的话）
    }

    @Override
    protected void readAdditionalSaveData(@Nonnull CompoundTag pCompound) {
        this.age = pCompound.getInt("Age");
        this.explosionTriggered = pCompound.getBoolean("ExplosionTriggered");
        
        // 如果爆炸已触发但队列为空，需要重新准备队列
        if (this.explosionTriggered && this.destructionQueue.isEmpty()) {
            prepareDestruction();
        }
        // 读取所有者信息（如果需要的话）
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    // 获取倒计时剩余时间（用于客户端显示）
    public int getRemainingTime() {
        return Math.max(0, EXPLOSION_TIME - age);
    }

    // 获取倒计时进度（0.0 到 1.0）
    public float getProgress() {
        return Math.min(1.0f, (float) age / EXPLOSION_TIME);
    }

    // 获取剩余秒数，保留一位小数(保留0)
    public float getRemainingSeconds() {
        return Math.max(0, (EXPLOSION_TIME - age) / 20.0f);
    }

    // 椭球体配置方法
    public static void setExplosionEllipsoid(float radiusX, float radiusY, float radiusZ) {
        // 这些方法可以用于动态配置椭球体参数
        // 由于当前使用的是 static final，如果需要动态配置，需要将它们改为非 static
    }

    // 获取椭球体参数
    public float[] getExplosionEllipsoidRadii() {
        return new float[]{radius_x, radius_y, radius_z};
    }

    public float[] getWaterEllipsoidRadii() {
        return new float[]{water_radius_x, water_radius_y, water_radius_z};
    }

    // 检查某个位置是否在爆炸椭球体内
    public boolean isInExplosionEllipsoid(BlockPos pos) {
        BlockPos center = this.blockPosition();
        double dx = pos.getX() - center.getX();
        double dy = pos.getY() - center.getY();
        double dz = pos.getZ() - center.getZ();
        
        double ellipsoidValue = (dx * dx) / (radius_x * radius_x) + 
                               (dy * dy) / (radius_y * radius_y) + 
                               (dz * dz) / (radius_z * radius_z);
        
        return ellipsoidValue <= 1.0;
    }

    // 检查某个位置是否在水蒸发椭球体内
    public boolean isInWaterEllipsoid(BlockPos pos) {
        BlockPos center = this.blockPosition();
        double dx = pos.getX() - center.getX();
        double dy = pos.getY() - center.getY();
        double dz = pos.getZ() - center.getZ();
        
        double ellipsoidValue = (dx * dx) / (water_radius_x * water_radius_x) + 
                               (dy * dy) / (water_radius_y * water_radius_y) + 
                               (dz * dz) / (water_radius_z * water_radius_z);
        
        return ellipsoidValue <= 1.0;
    }
    
    // 获取破坏队列状态
    public int getDestructionQueueSize() {
        return destructionQueue.size();
    }
    
    public int getTotalQueueSize() {
        return destructionQueue.size();
    }
    
    public boolean isExplosionTriggered() {
        return explosionTriggered;
    }
    
    // 获取破坏进度 (0.0 到 1.0)
    public float getDestructionProgress() {
        if (!explosionTriggered) return 0.0f;
        
        int totalBlocks = getTotalQueueSize();
        if (totalBlocks == 0) return 1.0f;
        
        // 这里需要记录初始队列大小来计算进度
        return 1.0f; // 简化版本，总是返回完成
    }
    
    // 强制触发爆炸（用于调试）
    public void forceExplosion() {
        if (!explosionTriggered) {
            explosionTriggered = true;
            prepareDestruction();
            damageEntities();
        }
    }
    
    // 设置批处理大小（动态调整）
    public static int getBatchSize() {
        return BATCH_SIZE;
    }
    
    // 概率配置方法
    public static ProbabilityConfig getProbabilityConfig(BlockDestroyType type) {
        return PROBABILITY_CONFIGS.get(type);
    }
    
    public static double getCoreProbability(BlockDestroyType type) {
        ProbabilityConfig config = PROBABILITY_CONFIGS.get(type);
        return config != null ? config.getCoreProbability() : 0.8;
    }
    
    public static double getEdgeProbability(BlockDestroyType type) {
        ProbabilityConfig config = PROBABILITY_CONFIGS.get(type);
        return config != null ? config.getEdgeProbability() : 0.3;
    }
    
    // 获取所有任务类型的概率配置信息
    public static Map<BlockDestroyType, ProbabilityConfig> getAllProbabilityConfigs() {
        return Map.copyOf(PROBABILITY_CONFIGS);
    }
    
    // 如果需要动态配置概率，可以添加这些方法
    // 注意：由于使用了 static final Map，如果需要动态配置，需要将其改为可变的
    public static void updateProbabilityConfig(BlockDestroyType type, double coreProbability, double edgeProbability) {
        // 这个方法可以用于动态配置单个任务类型的概率范围
        // 需要将 PROBABILITY_CONFIGS 改为可变的 Map 才能实现
        // 例如：PROBABILITY_CONFIGS.put(type, new ProbabilityConfig(coreProbability, edgeProbability));
    }
}