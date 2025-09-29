package kivo.millennium.milltek.entity.special;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

/**
 * 空对空导弹：锁定一个实体并使用比例导引法接近目标（PN, N=K=2）。
 * 简化实现：计算相对速度的横向分量并按 K*closingSpeed 比例给出侧向加速度，
 * 将速度方向调整为新的速度方向并维持一定巡航速度。
 */
public class AirToAirMissile extends AbstractMissile {

    private static final double NAVIGATION_CONSTANT = 2.0; // K = 2
    private static final double DEFAULT_SPEED = 2.5; // 导弹巡航速度
    private static final double MAX_ACCELERATION = 1.2; // 每 tick 最大速度改变量的幅度
    private static final boolean DEBUG_LOG = true; // 控制是否打印调试日志

    private UUID targetUUID = null;
    private LivingEntity targetEntity = null;

    public AirToAirMissile(EntityType<? extends AirToAirMissile> type, Level level) {
        super(type, level);
        // 设置默认参数
        this.setProximityRadius(2.0);
        this.setExplosionPower(4.0f);
    }

    public AirToAirMissile(EntityType<? extends AirToAirMissile> type, Level level, LivingEntity owner, LivingEntity target) {
        this(type, level);
        this.setOwner(owner);
        lockTarget(target);
        // 初始速度沿发射者视线方向（如果有），否则沿 +X
        Vec3 dir = owner != null ? owner.getViewVector(1.0f) : new Vec3(1, 0, 0);
        this.setDeltaMovement(dir.normalize().scale(DEFAULT_SPEED));
        if (DEBUG_LOG) {
            System.out.println("[AirToAirMissile] 构造: 初始速度=" + this.getDeltaMovement() + ", 位置=" + this.position());
        }
    }

    @Override
    protected void defineSynchedData() {
        // 无额外同步字段
    }

    /**
     * 锁定目标实体（在 NBT 中保存 UUID）
     */
    public void lockTarget(LivingEntity target) {
        if (target == null) return;
        this.targetEntity = target;
        this.targetUUID = target.getUUID();
    }

    private void tryResolveTarget() {
        if (this.targetEntity == null && this.targetUUID != null && !level().isClientSide) {
            var candidates = level().getEntities((net.minecraft.world.entity.Entity) null, this.getBoundingBox().inflate(256.0D), e -> e.getUUID().equals(this.targetUUID));
            if (!candidates.isEmpty()) {
                var ent = candidates.get(0);
                if (ent instanceof LivingEntity) this.targetEntity = (LivingEntity) ent;
            }
        }
    }

    @Override
    protected void applyGuidance() {
        // 只在服务器端进行制导
        if (level().isClientSide) return;

        // 尝试解析目标实体
        if (targetEntity == null && targetUUID != null) tryResolveTarget();

        if (targetEntity == null || !targetEntity.isAlive()) {
            // 无目标或目标失效：保持当前速度并在若干时间后自毁（继承的 lifetime 管理）
            if (DEBUG_LOG) {
                System.out.println("[AirToAirMissile] 无目标或目标失效, 当前速度=" + this.getDeltaMovement());
            }
            return;
        }

        Vec3 missilePos = this.position();
        Vec3 targetPos = targetEntity.position();
        Vec3 LOS = targetPos.subtract(missilePos);

        double distance = LOS.length();
        if (distance < 0.001) return;
        Vec3 LOSu = LOS.scale(1.0 / distance);

        Vec3 vMissile = this.getDeltaMovement();
        Vec3 vTarget = targetEntity.getDeltaMovement();
        Vec3 vRel = vTarget.subtract(vMissile);

        // 收缩速度（正值表示正在接近）
        double closingSpeed = -vRel.dot(LOSu);

        // 横向相对速度分量（相对于 LOS 的分量）
        Vec3 lateralRel = vRel.subtract(LOSu.scale(vRel.dot(LOSu)));

        // 估算 LOS 角速率项（简化）：使用 lateralRel / distance
        Vec3 losRate = lateralRel.scale(1.0 / Math.max(distance, 1.0));

        // PN 指令加速度 a = N * Vc * losRate
        Vec3 commandedAccel = losRate.scale(NAVIGATION_CONSTANT * Math.max(closingSpeed, 0.0));

        // 限幅
        double accelMag = commandedAccel.length();
        if (accelMag > MAX_ACCELERATION) {
            commandedAccel = commandedAccel.scale(MAX_ACCELERATION / accelMag);
        }

        // 更新速度：v_new = v + a (这里以每 tick 单位直接叠加)
        Vec3 newVel = vMissile.add(commandedAccel);

        // 维持近似设计速度（避免速度变得太小或太大）
        double newSpeed = newVel.length();
        if (newSpeed > 0.001) {
            newVel = newVel.normalize().scale(DEFAULT_SPEED);
        } else {
            newVel = vMissile;
        }

        this.setDeltaMovement(newVel);

        // 更新朝向以匹配速度
        double dx = newVel.x;
        double dy = newVel.y;
        double dz = newVel.z;
        double horiz = Math.sqrt(dx * dx + dz * dz);
        this.setYRot((float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F);
        this.setXRot((float) (Math.atan2(dy, horiz) * 180.0D / Math.PI));
        if (DEBUG_LOG) {
            System.out.println("[AirToAirMissile] 制导: 目标位置=" + targetPos + ", 导弹位置=" + missilePos + ", 当前速度=" + this.getDeltaMovement());
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (this.targetUUID != null) pCompound.putUUID("AAM_TargetUUID", this.targetUUID);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.hasUUID("AAM_TargetUUID")) {
            this.targetUUID = pCompound.getUUID("AAM_TargetUUID");
        }
    }

}
