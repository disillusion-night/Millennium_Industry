package kivo.millennium.milltek.entity.special;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractMissile extends Projectile {
    // 可配置属性
    protected float damage = 10.0f; // 伤害值（仅作信息用途）
    protected float explosionPower = 4.0f; // 爆炸威力/半径
    protected boolean proximityFuseEnabled = true; // 是否启用近炸引信
    protected double proximityRadius = 3.0D; // 近炸引信触发半径（单位：格）
    protected int lifetimeTicks = 20 * 60; // 最大存活时间（刻），默认60秒后自爆

    // 状态
    protected int ageTicks = 0; // 已存在刻数

    public AbstractMissile(EntityType<? extends AbstractMissile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = false;
    }

    /**
     * 便捷构造器：指定拥有者
     */
    public AbstractMissile(EntityType<? extends AbstractMissile> type, Level level, LivingEntity owner) {
        this(type, level);
        this.setOwner(owner);
    }

    @Override
    public void tick() {
        super.tick();

        // 仅在服务器端执行实体行为逻辑
        if (!level().isClientSide) {
            ageTicks++;

            // 子类重写该方法以实现制导行为（每刻调用）
            applyGuidance();

            // 碰撞检测（与实体）：使用略微扩大的包围盒检测接触实体
            var bbox = this.getBoundingBox().inflate(0.5D);
            for (Entity e : level().getEntities(this, bbox)) {
                if (e == this || e == this.getOwner()) continue;
                // 过滤不可被击中的实体（例如物品、某些投射物、拥有者等）
                if (!canHitEntity(e)) continue;
                // 接触直接引爆
                this.explode(this.getX(), this.getY(), this.getZ());
                return; // 已引爆，实体将在 explode() 中移除
            }

            // 碰撞检测（与方块）：如果处于非空气方块中，视为撞墙，引爆
            var pos = this.blockPosition();
            if (!level().isEmptyBlock(pos)) {
                this.explode(this.getX(), this.getY(), this.getZ());
                return;
            }

            // 近炸引信：检测指定半径内的实体并触发
            if (proximityFuseEnabled) {
                var close = level().getEntities(this, this.getBoundingBox().inflate(proximityRadius), e -> e != this && e != getOwner());
                for (Entity e : close) {
                    if (!canHitEntity(e)) continue;
                    this.explode(this.getX(), this.getY(), this.getZ());
                    return;
                }
            }

            // 存活时间到则自爆
            if (ageTicks >= lifetimeTicks) {
                this.explode(this.getX(), this.getY(), this.getZ());
                return;
            }
        }
        // 强制每 tick 移动实体，防止父类未自动 move
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    /**
     * 导弹在指定坐标的爆炸逻辑；子类可以重写以改变方块交互或特效，但应调用 super 以确保实体被移除。
     */
    protected void explode(double x, double y, double z) {
        if (level().isClientSide) return;

        // 以拥有者作为爆炸发起者（若存在），默认使用保守的爆炸调用以避免随意破坏方块
        Entity owner = this.getOwner();
        // 注意：不同的 mappings 中 explode 签名可能不同，子类可覆盖此方法以使用特定的爆炸行为
        // 使用 Level.ExplosionInteraction 枚举来指定方块交互策略，选择 NONE 表示不破坏方块
        level().explode(owner instanceof LivingEntity ? (LivingEntity) owner : null, x, y, z, explosionPower, Level.ExplosionInteraction.NONE);

        // 移除实体
        this.discard();
    }

    /**
     * 制导钩子：子类应实现此方法来更新导弹速度和朝向（每 tick 在服务器端被调用）。
     */
    protected abstract void applyGuidance();

    /**
     * 简单的朝向目标点制导辅助方法。
     * 使用比例混合（平滑）将当前速度向目标方向逼近。
     * maxTurnAngleRadians 参数保留以便子类使用更复杂的转向逻辑。
     */
    protected void steerTowards(Vec3 target, double maxTurnAngleRadians, double speed) {
        Vec3 toTarget = target.subtract(this.position());
        Vec3 desired = toTarget.normalize().scale(speed);

        // 在当前速度和期望速度间做平滑混合
        Vec3 current = this.getDeltaMovement();
        double blend = 0.15; // 平滑系数
        Vec3 newVel = current.scale(1.0 - blend).add(desired.scale(blend));

        this.setDeltaMovement(newVel);
        // 根据速度更新实体朝向（用于渲染/朝向一致）
        double dx = newVel.x;
        double dy = newVel.y;
        double dz = newVel.z;
        double horiz = Math.sqrt(dx * dx + dz * dz);
        this.setYRot((float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F);
        this.setXRot((float) (Math.atan2(dy, horiz) * 180.0D / Math.PI));
    }

    /**
     * 判定导弹是否能攻击给定实体的默认实现：
     * - 不攻击自身或拥有者
     * - 仅攻击存活实体
     * - 忽略地上物品与其他投射物
     * 子类可覆盖以实现队伍/友好判断等。
     */
    protected boolean canHitEntity(Entity e) {
        if (e == null) return false;
        if (e == this || e == this.getOwner()) return false;
        if (!e.isAlive()) return false;
        // 忽略地上的物品和其他投射物
        if (e instanceof net.minecraft.world.entity.item.ItemEntity) return false;
        if (e instanceof Projectile) return false;
        return true;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putFloat("Damage", this.damage);
        pCompound.putFloat("ExplosionPower", this.explosionPower);
        pCompound.putBoolean("ProximityFuse", this.proximityFuseEnabled);
        pCompound.putDouble("ProximityRadius", this.proximityRadius);
        pCompound.putInt("LifetimeTicks", this.lifetimeTicks);
        pCompound.putInt("AgeTicks", this.ageTicks);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Damage")) this.damage = pCompound.getFloat("Damage");
        if (pCompound.contains("ExplosionPower")) this.explosionPower = pCompound.getFloat("ExplosionPower");
        if (pCompound.contains("ProximityFuse")) this.proximityFuseEnabled = pCompound.getBoolean("ProximityFuse");
        if (pCompound.contains("ProximityRadius")) this.proximityRadius = pCompound.getDouble("ProximityRadius");
        if (pCompound.contains("LifetimeTicks")) this.lifetimeTicks = pCompound.getInt("LifetimeTicks");
        if (pCompound.contains("AgeTicks")) this.ageTicks = pCompound.getInt("AgeTicks");
    }

    // 对外可配置的 setter/getter
    public void setDamage(float damage) { this.damage = damage; }
    public float getDamage() { return this.damage; }
    public void setExplosionPower(float power) { this.explosionPower = power; }
    public float getExplosionPower() { return this.explosionPower; }
    public void setProximityFuseEnabled(boolean enabled) { this.proximityFuseEnabled = enabled; }
    public boolean isProximityFuseEnabled() { return this.proximityFuseEnabled; }
    public void setProximityRadius(double r) { this.proximityRadius = r; }
    public double getProximityRadius() { return this.proximityRadius; }
    public void setLifetimeTicks(int ticks) { this.lifetimeTicks = ticks; }
    public int getLifetimeTicks() { return this.lifetimeTicks; }

}
