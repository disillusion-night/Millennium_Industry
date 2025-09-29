package kivo.millennium.milltek.item;

import kivo.millennium.milltek.entity.special.MissileUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.List;

public class MissileLauncherItem extends Item {
    public MissileLauncherItem(Properties properties) {
        super(properties);
    }

    @Override
    @Nonnull
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        if (!level.isClientSide) {
            // 获取玩家视线
            Vec3 eyePos = player.getEyePosition(1.0f);
            Vec3 lookVec = player.getLookAngle();
            double maxDistance = 64.0D;
            Vec3 reachVec = eyePos.add(lookVec.scale(maxDistance));
            // 扩大包围盒，查找视线方向上的实体
            AABB aabb = player.getBoundingBox().expandTowards(lookVec.scale(maxDistance)).inflate(1.0D);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb, e -> e != player && e.isAlive());
            LivingEntity closest = null;
            double closestDist = maxDistance;
            for (LivingEntity entity : entities) {
                AABB entityBox = entity.getBoundingBox().inflate(0.3);
                java.util.Optional<Vec3> hit = entityBox.clip(eyePos, reachVec);
                if (hit.isPresent()) {
                    double dist = eyePos.distanceTo(hit.get());
                    if (dist < closestDist) {
                        closestDist = dist;
                        closest = entity;
                    }
                }
            }
            if (closest != null) {
                MissileUtils.launchAirToAirMissile(level, player, closest);
                // 可选：消耗物品
                // player.getItemInHand(hand).shrink(1);
                player.displayClientMessage(net.minecraft.network.chat.Component.literal("已发射导弹！"), true);
            } else {
                player.displayClientMessage(net.minecraft.network.chat.Component.literal("未发现目标！"), true);
            }
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}
