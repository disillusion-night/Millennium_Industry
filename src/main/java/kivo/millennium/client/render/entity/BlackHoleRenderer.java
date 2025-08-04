package kivo.millennium.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import kivo.millennium.milltek.Main;
import kivo.millennium.milltek.entity.special.BlackHole;
import kivo.millennium.client.render.RendererSetup;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BlackHoleRenderer extends EntityRenderer<BlackHole> {

    private static final ResourceLocation CENTER_TEXTURE = Main.getRL("textures/entity/black_hole/black_hole.png");
    private static final ResourceLocation BEAM_TEXTURE = Main.getRL("textures/entity/black_hole/beam.png");

    public BlackHoleRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(BlackHole entity, float pEntityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int pPackedLight) {
        poseStack.pushPose();
        // 使用自定义的黑洞RenderType进行渲染
        VertexConsumer consumer = bufferSource.getBuffer(RendererSetup.blackHoleRenderType);
        // 这里可以添加具体的渲染逻辑，例如绘制一个平面或球体，示例仅保留结构
        // ...渲染黑洞中心贴图等...
        poseStack.popPose();
    }



    @Override
    public ResourceLocation getTextureLocation(BlackHole pEntity) {
        return CENTER_TEXTURE;
    }
}
