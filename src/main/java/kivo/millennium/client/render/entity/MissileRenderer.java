package kivo.millennium.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.RandomSource;

/**
 * 导弹实体渲染器：使用 ItemRenderer 绘制导弹模型（与箭类似），并在尾部生成烟雾粒子。
 */
public class MissileRenderer<T extends Projectile> extends EntityRenderer<T> {
    private final ItemRenderer itemRenderer;
    private final ItemStack renderStack;

    public MissileRenderer(EntityRendererProvider.Context ctx, ItemStack renderStack) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
        this.renderStack = renderStack;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // 朝向速度方向
        Vec3 motion = entity.getDeltaMovement();
        double dx = motion.x;
        double dy = motion.y;
        double dz = motion.z;
        double horiz = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (Math.atan2(dy, horiz) * 180.0D / Math.PI);

        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch));

        // 缩放以匹配箭/导弹大小
        float scale = 0.6f;
        poseStack.scale(scale, scale, scale);

        // 渲染模型（使用 ItemRenderer）
        itemRenderer.renderStatic(renderStack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), 0);

        poseStack.popPose();

        // 生成尾部烟雾（客户端）
        if (!entity.level().isClientSide()) return;
        RandomSource rnd = entity.level().getRandom();
        if (rnd.nextDouble() < 0.5) {
            // 方向单位向量
            double len = Math.max(0.0001, Math.sqrt(dx * dx + dy * dy + dz * dz));
            Vec3 dir = new Vec3(dx / len, dy / len, dz / len);
            double tailOffset = 0.35;
            double tx = entity.getX() - dir.x * tailOffset + (rnd.nextDouble() - 0.5) * 0.08;
            double ty = entity.getY() + 0.1 + (rnd.nextDouble() - 0.5) * 0.04;
            double tz = entity.getZ() - dir.z * tailOffset + (rnd.nextDouble() - 0.5) * 0.08;
            double pvx = -dir.x * 0.02 + (rnd.nextDouble() - 0.5) * 0.01;
            double pvy = -Math.abs(dir.y) * 0.01 + (rnd.nextDouble() - 0.5) * 0.01;
            double pvz = -dir.z * 0.02 + (rnd.nextDouble() - 0.5) * 0.01;
            try {
                entity.level().addParticle(ParticleTypes.SMOKE, tx, ty, tz, pvx, pvy, pvz);
            } catch (Throwable ignored) {}
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public net.minecraft.resources.ResourceLocation getTextureLocation(T entity) {
        return net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS;
    }
}
