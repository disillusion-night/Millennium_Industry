package kivo.millennium.milltek.entity.special;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 地对地战术导弹：
 * - 垂直发射，向上升 40 格（默认）
 * - 升到指定高度后变轨，先飞往目标上空的拦截点（抛物线近似，通过高空拦截点实现），
 * - 接近拦截点后进行近似垂直俯冲攻击目标点
 *
 * 使用方式：在创建实例时通过构造器传入发射者（owner）和目标坐标（targetVec），
 * 子类或外部可设置速度/拦截高度等参数。
 */
public class GroundTacticalMissile extends AbstractMissile {

    private enum Phase {ASCEND, ARC, DIVE}

    private Phase phase = Phase.ASCEND;

    // 目标坐标（世界坐标，导弹将最终垂直攻击此点）
    private Vec3 target = null;

    // 升空起始 Y，用于计算是否升高了 40 格
    private double launchStartY = Double.NaN;

    // 参数（可调整）
    private double ascendDistance = 40.0; // 升高多少格后转轨
    private double ascendSpeed = 0.9; // 升空速度（单位：格/刻）
    private double cruiseSpeed = 1.2; // 巡航（拦截）阶段速度
    private double diveSpeed = 3.5; // 俯冲速度（竖直分量）
    private double apexHeightOffset = 40.0; // 目标上方额外的拦截高度
    private double arcArriveThreshold = 3.0; // 当接近拦截点时进入俯冲

    public GroundTacticalMissile(EntityType<? extends GroundTacticalMissile> type, Level level) {
        super(type, level);
        // 默认近炸半径与爆炸威力可以调整
        this.setProximityRadius(3.0);
        this.setExplosionPower(6.0f);
    }

    public GroundTacticalMissile(EntityType<? extends GroundTacticalMissile> type, Level level, LivingEntity owner, Vec3 target) {
        this(type, level);
        this.setOwner(owner);
        this.target = target;
        // 初始发射为垂直向上
        this.setDeltaMovement(0.0, ascendSpeed, 0.0);
    }

    @Override
    protected void defineSynchedData() {
        // 本导弹当前无需同步额外字段；实现此抽象方法以满足 Entity 要求
    }

    @Override
    protected void applyGuidance() {
        // 如果没有目标，维持当前行为或自毁
        if (target == null) {
            // 没有目标则待在 ASCEND 一段时间后自爆
            if (Double.isNaN(launchStartY)) launchStartY = this.getY();
            if (this.getY() - launchStartY >= ascendDistance) {
                // 进入巡航但没有目标 -> 直接开始俯冲并自毁在落地
                phase = Phase.DIVE;
            }
        }

        switch (phase) {
            case ASCEND:
                handleAscend();
                break;
            case ARC:
                handleArc();
                break;
            case DIVE:
                handleDive();
                break;
        }
    }

    private void handleAscend() {
        // 记录起始高度
        if (Double.isNaN(launchStartY)) launchStartY = this.getY();
        // 强制维持竖直上升速度
        this.setDeltaMovement(0.0, ascendSpeed, 0.0);

        // 当爬升到指定高度后进入拦截轨道
        if (this.getY() - launchStartY >= ascendDistance) {
            // 若有目标则转入 ARC 阶段，否则直接进入俯冲
            if (target != null) {
                phase = Phase.ARC;
            } else {
                phase = Phase.DIVE;
            }
        }
    }

    private void handleArc() {
        if (target == null) {
            phase = Phase.DIVE;
            return;
        }

        double apexY = Math.max(target.y + apexHeightOffset, this.getY() + 10.0);
        Vec3 intercept = new Vec3(target.x, apexY, target.z);

        steerTowards(intercept, Math.PI / 6.0, cruiseSpeed);

        // 只计算 xz 平面距离
        double dx = this.getX() - intercept.x;
        double dz = this.getZ() - intercept.z;
        double xzDist = Math.sqrt(dx * dx + dz * dz);

        if (xzDist <= 5.0) {
            phase = Phase.DIVE;
        }
    }


    private void handleDive() {
        // 俯冲阶段：主要向目标点下落，同时进行少量水平修正以命中目标
        if (target == null) {
            // 没有目标，直接向下快速俯冲
            this.setDeltaMovement(0.0, -diveSpeed, 0.0);
            return;
        }

        Vec3 pos = this.position();
        Vec3 toTarget = target.subtract(pos);

        // 计算水平误差和平面方向
        Vec3 horiz = new Vec3(toTarget.x, 0.0, toTarget.z);
        double horizDist = Math.max(0.001, horiz.length());

        // 水平修正分量，保持较小，使俯冲近似竖直
        double horizFactor = 0.25; // 控制水平纠偏强度
        Vec3 horizVel = horiz.normalize().scale(cruiseSpeed * horizFactor);

        // 垂直分量使用固定负速度
        double vy = -Math.abs(diveSpeed);

        Vec3 finalVel = new Vec3(horizVel.x, vy, horizVel.z);
        this.setDeltaMovement(finalVel);

        // 若已非常接近目标（接触/命中高度）则触发爆炸
        // 这里使用垂直距离阈值：当低于 2 格或者触地时触发
        if (this.getY() - target.y <= 2.0 || this.onGround()) {
            this.explode(this.getX(), this.getY(), this.getZ());
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Phase", phase.ordinal());
        pCompound.putDouble("LaunchStartY", Double.isNaN(launchStartY) ? -Double.MAX_VALUE : launchStartY);
        if (target != null) {
            pCompound.putDouble("TargetX", target.x);
            pCompound.putDouble("TargetY", target.y);
            pCompound.putDouble("TargetZ", target.z);
        }
        pCompound.putDouble("AscendDistance", ascendDistance);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Phase")) {
            int ord = pCompound.getInt("Phase");
            Phase[] vals = Phase.values();
            if (ord >= 0 && ord < vals.length) phase = vals[ord];
        }
        if (pCompound.contains("LaunchStartY")) {
            double v = pCompound.getDouble("LaunchStartY");
            launchStartY = (v == -Double.MAX_VALUE) ? Double.NaN : v;
        }
        if (pCompound.contains("TargetX") && pCompound.contains("TargetY") && pCompound.contains("TargetZ")) {
            target = new Vec3(pCompound.getDouble("TargetX"), pCompound.getDouble("TargetY"), pCompound.getDouble("TargetZ"));
        }
        if (pCompound.contains("AscendDistance")) ascendDistance = pCompound.getDouble("AscendDistance");
    }

    // 对外设置目标坐标
    public void setTarget(Vec3 t) { this.target = t; }
    public Vec3 getTarget() { return this.target; }

    // 可配置参数的 setter/getter
    public void setAscendDistance(double d) { this.ascendDistance = d; }
    public double getAscendDistance() { return this.ascendDistance; }
    public void setApexHeightOffset(double h) { this.apexHeightOffset = h; }
    public double getApexHeightOffset() { return this.apexHeightOffset; }

}
