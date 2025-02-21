package kivo.millennium.millind.entity.special;

import kivo.millennium.millind.init.MillenniumEntities;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class BlackHole extends Projectile {
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(BlackHole.class, EntityDataSerializers.FLOAT);

    public BlackHole(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public BlackHole(Level pLevel, LivingEntity owner) {
        this(MillenniumEntities.BLACK_HOLE.get(), pLevel);
        setOwner(owner);
    }

    List<Entity> trackingEntities = new ArrayList<>();

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }


    private int soundTick;
    private float damage;
    private boolean under_controlled;

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDamage() {
        return damage;
    }

    public boolean isUnderControlled() {
        return under_controlled;
    }

    public void setUnderControlled(boolean is_under_controlled) {
        this.under_controlled = is_under_controlled;
    }
    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return EntityDimensions.scalable(this.getRadius() * 2.0F, this.getRadius() * 2.0F);
    }

    protected void defineSynchedData() {
        this.getEntityData().define(DATA_RADIUS, 5F);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (DATA_RADIUS.equals(pKey)) {
            this.refreshDimensions();
            if (getRadius() < .1f)
                this.discard();
        }

        super.onSyncedDataUpdated(pKey);
    }

    public void setRadius(float pRadius) {
        if (!this.level().isClientSide) {
            this.getEntityData().set(DATA_RADIUS, Math.min(pRadius, 48));
        }
    }

    public float getRadius() {
        return this.getEntityData().get(DATA_RADIUS);
    }

    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putFloat("Radius", this.getRadius());
        pCompound.putInt("Age", this.tickCount);
        pCompound.putFloat("Damage", this.getDamage());
        pCompound.putBoolean("IsUnderControlled", this.isUnderControlled());

        super.addAdditionalSaveData(pCompound);
    }
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.under_controlled = pCompound.getBoolean("IsUnderControlled");
        this.tickCount = pCompound.getInt("Age");
        this.damage = pCompound.getFloat("Damage");
        if (damage == 0)
            damage = 1;
        if (pCompound.getInt("Radius") > 0)
            this.setRadius(pCompound.getFloat("Radius"));

        super.readAdditionalSaveData(pCompound);

    }

    @Override
    public void tick() {
        super.tick();
        if(isUnderControlled())
            return;

        // 更新受黑洞影响的实体列表，根据黑洞半径决定更新频率
        int updateFrequency = Math.max((int) (getRadius() / 2), 2);
        if (tickCount % updateFrequency == 0) {
            updateTrackingEntities();
        }

        var bb = this.getBoundingBox();
        float radius = (float) (bb.getXsize());
        boolean hitTick = this.tickCount % 10 == 0;

        for (Entity entity : trackingEntities) {
            // 确保不会对自己所有者或友方单位造成影响
            if (entity != getOwner()) {
                Vec3 center = bb.getCenter();
                float distance = (float) center.distanceTo(entity.position());

                // 如果距离大于黑洞的半径，则忽略该实体
                if (distance > radius) continue;

                // 根据距离计算引力强度
                float f = 1 - distance / radius;
                float scale = f * f * .5f;

                // 计算移动向量，并应用到实体上
                Vec3 diff = center.subtract(entity.position()).scale(scale);
                entity.push(diff.x, diff.y, diff.z);

                // 每10个游戏刻检查一次是否对实体造成伤害
                if (hitTick && distance < 9 && canHitEntity(entity)) {
                    //entity.hurt(new DamageSource(new DamageTypeHolder(),this.getOwner() != null?getOwner():this), this.getDamage());
                }

                // 重置跌落距离以避免因重力掉落
                entity.fallDistance = 0;
            }
        }
    }

    private void updateTrackingEntities() {
        trackingEntities = level().getEntities(this, this.getBoundingBox().inflate(1));
    }

    private static final int loopSoundDurationInTicks = 320;

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

}
