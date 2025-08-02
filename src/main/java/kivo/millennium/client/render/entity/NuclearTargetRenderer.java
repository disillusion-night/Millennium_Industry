package kivo.millennium.client.render.entity;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import kivo.millennium.milltek.Main;
import kivo.millennium.milltek.entity.special.NuclearTarget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class NuclearTargetRenderer extends EntityRenderer<NuclearTarget> {

    private static final ResourceLocation CENTER_TEXTURE = Main.getRL("textures/entity/nuclear_target/nuclear_target.png");
    public NuclearTargetRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(NuclearTarget entity, float pEntityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int pPackedLight) {
        poseStack.pushPose();

        // 对实体进行居中处理
        poseStack.translate(0, 0, 0);

        //渲染始终向上的中心材质
        poseStack.mulPose(Axis.YP.rotationDegrees(90f));
        poseStack.scale(0.01f, 0.01f, 0.01f);
        // 获取顶点消费者，用于绘制透明纹理
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(CENTER_TEXTURE));
        // 定义矩形面
        Matrix4f poseMatrix = poseStack.last().pose();
        Matrix3f normalMatrix = poseStack.last().normal();
        consumer.vertex(poseMatrix, -8, 0, -8).color(255, 255, 255, 255).uv(0f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, 8, 0, -8).color(255, 255, 255, 255).uv(1f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, 8, 0, 8).color(255, 255, 255, 255).uv(1f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        consumer.vertex(poseMatrix, -8, 0, 8).color(255, 255, 255, 255).uv(0f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 1f, 0f).endVertex();
        poseStack.popPose();

        // 渲染一个具有广告牌效果的倒计时
        poseStack.pushPose();
        poseStack.translate(0, 0, 0); // 调整位置到实体上方
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        //poseStack.mulPose(Axis.YP.rotationDegrees(90f));
        poseStack.scale(0.1f, 0.1f, 0.1f); // 缩小到合适大小
        String countdownText = String.format(String.valueOf(entity.getRemainingSeconds()), "%.1f"); // 转换为秒，保留一位小数（保留0）
        this.getFont().drawInBatch(countdownText, this.getFont().width(countdownText) / 2f, 0F, 0xFFFFFFFF, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0xffff00, LightTexture.FULL_BRIGHT);
        poseStack.popPose();


        super.render(entity, pEntityYaw, partialTicks, poseStack, bufferSource, pPackedLight);
    }
    @Override
    public ResourceLocation getTextureLocation(NuclearTarget pEntity) {
        return CENTER_TEXTURE;
    }
}
