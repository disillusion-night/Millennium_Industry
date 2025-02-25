package kivo.millennium.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import kivo.millennium.millind.entity.special.BlackHole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Random;

import java.util.ArrayList;
import java.util.List;

import static kivo.millennium.millind.Main.LOGGER;
import static kivo.millennium.millind.Main.getRL;

public class BlackHoleRenderer extends EntityRenderer<BlackHole> {

    private static final ResourceLocation CENTER_TEXTURE = getRL("textures/entity/black_hole/black_hole.png");
    private static final ResourceLocation BEAM_TEXTURE = getRL("textures/entity/black_hole/beam.png");

    public BlackHoleRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(BlackHole entity, float pEntityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int pPackedLight) {
        poseStack.pushPose();

        //bufferSource.getBuffer().;
        // 对实体进行居中处理
        poseStack.translate(0, entity.getBoundingBox().getYsize() / 2, 0);

        // 计算缩放比例
        float entityScale = entity.getBbWidth() * .025f;
        poseStack.scale(.5f * entityScale, .5f * entityScale, .5f * entityScale);
        // 应用相机方向和旋转调整
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(90f));
        poseStack.translate(5, 0, 0);

        // 获取顶点消费者，用于绘制透明纹理
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(CENTER_TEXTURE));

        // 定义黑洞中心的矩形面
        Matrix4f poseMatrix = poseStack.last().pose();
        Matrix3f normalMatrix = poseStack.last().normal();

        consumer.vertex(poseMatrix, 0, -8, -8).color(255, 255, 255, 255).uv(0f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, 0, 8, -8).color(255, 255, 255, 255).uv(0f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, 0, 8, 8).color(255, 255, 255, 255).uv(1f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, 0, -8, 8).color(255, 255, 255, 255).uv(1f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();

        poseStack.popPose();
    
        poseStack.pushPose();

        poseStack.translate(0, entity.getBoundingBox().getYsize() / 2, 0);
        float animationProgress = (entity.tickCount + partialTicks) / 200.0F;
        //float fadeProgress = Math.min(animationProgress > 0.8F ? (animationProgress - 0.8F) / 0.2F : 0.0F, 1.0F);
        float fadeProgress = .5f;
        RandomSource randomSource = RandomSource.create(432L);
//        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lightning());
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.energySwirl(BEAM_TEXTURE, 0, 0));
        //poseStack.translate(0.0D, -1.0D, -2.0D);

        for (int i = 0; i < 30; ++i) {
            poseStack.mulPose(Axis.XP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(randomSource.nextFloat() * 360.0F + animationProgress * 90.0F));
            float size1 = (randomSource.nextFloat() * 10.0F + 5.0F + fadeProgress * 5.0F) * entityScale * .4f;
            Matrix4f matrix = poseStack.last().pose();
            Matrix3f normalMatrix2 = poseStack.last().normal();

            drawTriangle(vertexConsumer, matrix, normalMatrix2, size1);
        }

        poseStack.popPose();

        poseStack.pushPose();

        poseStack.translate(0, entity.getBoundingBox().getYsize() / 2, 0);

        poseStack.mulPose(Axis.YP.rotationDegrees(entity.yRotO)); // 绕Y轴旋转
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.xRotO)); // 绕X轴旋转


        for (int i = 0; i < 8; ++i) {
            poseStack.mulPose(Axis.XP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(randomSource.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(randomSource.nextFloat() * 360.0F * animationProgress));

            float size1 = (randomSource.nextFloat() * 10.0F + 5.0F + fadeProgress * 5.0F) * entityScale;
            Matrix4f matrix = poseStack.last().pose();
            Matrix3f normalMatrix3 = poseStack.last().normal();

            drawTriangle2(vertexConsumer, matrix, normalMatrix3, size1);
        }

        poseStack.popPose();

        super.render(entity, pEntityYaw, partialTicks, poseStack, bufferSource, pPackedLight);
    }



    private static void drawTriangle(VertexConsumer consumer, Matrix4f poseMatrix, Matrix3f normalMatrix, float size) {
        consumer.vertex(poseMatrix, 0, 0, 0).color(255, 0, 255, 255).uv(0f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, 0, 3 * size, -1 * size).color(0, 0, 0, 0).uv(0f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, 0, 3 * size, 1 * size).color(0, 0, 0, 0).uv(1f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, 0, 0, 0).color(255, 0, 255, 255).uv(1f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
    }


    private static void drawTriangle2(VertexConsumer consumer, Matrix4f poseMatrix, Matrix3f normalMatrix, float size) {
        consumer.vertex(poseMatrix, 0, 0, 0).color(255, 0, 255, 255).uv(0f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, 0, 5 * size, -1 * size).color(0, 0, 0, 0).uv(0f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, 0, 5 * size, 1 * size).color(0, 0, 0, 0).uv(1f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, 0, 0, 0).color(255, 0, 255, 255).uv(1f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();

        consumer.vertex(poseMatrix, 0, 0, 0).color(255, 0, 255, 255).uv(0f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, 0, -5 * size, -.5f * size).color(0, 0, 0, 0).uv(0f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, 0, -5 * size, .5f * size).color(0, 0, 0, 0).uv(1f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, 0, 0, 0).color(255, 0, 255, 255).uv(1f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();

    }


    @Override
    public ResourceLocation getTextureLocation(BlackHole pEntity) {
        return CENTER_TEXTURE;
    }
}