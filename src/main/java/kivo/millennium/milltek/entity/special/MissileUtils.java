package kivo.millennium.milltek.entity.special;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import kivo.millennium.milltek.init.MillenniumEntities;

public class MissileUtils {

    /**
     * 在服务器世界中生成并发射一枚空对空导弹，导弹会尝试锁定并跟踪 target。
     */
    public static AirToAirMissile launchAirToAirMissile(Level level, LivingEntity shooter, LivingEntity target) {
        if (level.isClientSide) return null;
        var type = MillenniumEntities.AIR_TO_AIR_MISSILE.get();
        AirToAirMissile missile = new AirToAirMissile(type, level, shooter, target);
        // 将导弹放在发射者前方一点
        Vec3 spawnPos = shooter.position().add(shooter.getViewVector(1.0f).scale(1.5));
        missile.setPos(spawnPos.x, spawnPos.y + 0.5, spawnPos.z);
        level.addFreshEntity(missile);
        return missile;
    }

    /**
     * 在服务器世界中生成并发射一枚地对地导弹，目标为 world 坐标 targetPos。
     */
    public static GroundTacticalMissile launchGroundTacticalMissile(Level level, LivingEntity shooter, Vec3 targetPos) {
        if (level.isClientSide) return null;
        var type = MillenniumEntities.GROUND_TACTICAL_MISSILE.get();
        GroundTacticalMissile missile = new GroundTacticalMissile(type, level, shooter, targetPos);
        Vec3 spawnPos = shooter.position().add(shooter.getViewVector(1.0f).scale(1.5));
        missile.setPos(spawnPos.x, spawnPos.y + 0.5, spawnPos.z);
        level.addFreshEntity(missile);
        return missile;
    }
}

