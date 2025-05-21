package kivo.millennium.client.render.blockEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import kivo.millennium.milltek.block.device.MillenniumBlockProperty;
import kivo.millennium.milltek.block.laser.NetherStarLaserBE;
import kivo.millennium.milltek.util.ShapeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class NetherStarLaserBER implements BlockEntityRenderer<NetherStarLaserBE> {

    private static final ResourceLocation BEAM_TEXTURE = new ResourceLocation("minecraft",
            "textures/entity/beacon_beam.png"); // 原版信标光束纹理，你可以替换为自定义纹理

    public NetherStarLaserBER(BlockEntityRendererProvider.Context context) {

    }

    public void render(NetherStarLaserBE blockEntity, float partialTick, PoseStack pPoseStack,
            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState blockState = blockEntity.getBlockState();

        long GameTime = blockEntity.getLevel().getGameTime();

        pPoseStack.pushPose();

        pPoseStack.translate(0.5f, 0.5f, 0.5f); // 将渲染中心移动到方块中心

        Direction facing = blockState.getValue(BlockStateProperties.FACING);

        ShapeUtils.rotateByFacing(pPoseStack, facing);

        if (blockState.getValue(MillenniumBlockProperty.WORKING)) {
            renderCore(blockEntity, partialTick, GameTime, pPoseStack, bufferSource, packedLight, packedOverlay);

            float[] color = new float[] { 1, 1, 1 };
            renderBeam(pPoseStack, bufferSource, partialTick, blockEntity.getLevel().getGameTime(), 50, color);
        }

        pPoseStack.popPose();
    }

    public void renderCore(NetherStarLaserBE pBlockEntity, float partialTick, long pGameTime, PoseStack pPoseStack,
            MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();

        pPoseStack.translate(0.0f, -0.3f, 0.0f);

        float scale = 0.2f;

        pPoseStack.scale(scale, scale, scale);

        float RotationSpeed = 5F;

        float RotationYaw = Math.floorMod((int) (pGameTime * RotationSpeed + partialTick), 1000) * .1F;

        pPoseStack.mulPose(Axis.YP.rotationDegrees(RotationYaw));

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        ItemStack stack = new ItemStack(Items.NETHER_STAR);

        BakedModel bakedModel = itemRenderer.getModel(stack, pBlockEntity.getLevel(), null, 0);

        itemRenderer.render(stack, ItemDisplayContext.FIXED, true, pPoseStack, pBuffer, pPackedLight, pPackedOverlay,
                bakedModel);

        pPoseStack.popPose();
    }

    private static void renderBeam(PoseStack pPoseStack, MultiBufferSource pBufferSource, float pPartialTick,
            long pGameTime, int pHeight, float[] pColors) {
        renderBeaconBeam(pPoseStack, pBufferSource, BEAM_TEXTURE, pPartialTick, 1.0F, pGameTime, pHeight, pColors,
                0.08F, 0.15F);
    }

    public static void renderBeaconBeam(PoseStack pPoseStack, MultiBufferSource pBufferSource,
            ResourceLocation pBeamLocation, float pPartialTick, float pTextureScale, long pGameTime, int pHeight,
            float[] pColors, float pBeamRadius, float pGlowRadius) {
        int i = pHeight;
        pPoseStack.pushPose();
        pPoseStack.translate(0, 0.25D, 0);
        float f = (float) Math.floorMod(pGameTime, 40) + pPartialTick;
        float f1 = pHeight < 0 ? f : -f;
        float f2 = Mth.frac(f1 * 0.2F - (float) Mth.floor(f1 * 0.1F));
        float f3 = pColors[0];
        float f4 = pColors[1];
        float f5 = pColors[2];
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YP.rotationDegrees(f * 2.25F - 45.0F));
        float f6 = 0.0F;
        float f8 = 0.0F;
        float f9 = -pBeamRadius;
        float f10 = 0.0F;
        float f11 = 0.0F;
        float f12 = -pBeamRadius;
        float f13 = 0.0F;
        float f14 = 1.0F;
        float f15 = -1.0F + f2;
        float f16 = (float) pHeight * pTextureScale * (0.5F / pBeamRadius) + f15;
        renderPart(pPoseStack, pBufferSource.getBuffer(RenderType.beaconBeam(pBeamLocation, false)), f3, f4, f5, 1.0F,
                0, i, 0.0F, pBeamRadius, pBeamRadius, 0.0F, f9, 0.0F, 0.0F, f12, 0.0F, 1.0F, f16, f15);
        pPoseStack.popPose();
        f6 = -pGlowRadius;
        float f7 = -pGlowRadius;
        f8 = -pGlowRadius;
        f9 = -pGlowRadius;
        f13 = 0.0F;
        f14 = 1.0F;
        f15 = -1.0F + f2;
        f16 = (float) pHeight * pTextureScale + f15;
        renderPart(pPoseStack, pBufferSource.getBuffer(RenderType.beaconBeam(pBeamLocation, true)), f3, f4, f5, 0.125F,
                0, i, f6, f7, pGlowRadius, f8, f9, pGlowRadius, pGlowRadius, pGlowRadius, 0.0F, 1.0F, f16, f15);
        pPoseStack.popPose();
    }

    private static void renderPart(PoseStack pPoseStack, VertexConsumer pConsumer, float pRed, float pGreen,
            float pBlue, float pAlpha, int pMinY, int pMaxY, float pX0, float pZ0, float pX1, float pZ1, float pX2,
            float pZ2, float pX3, float pZ3, float pMinU, float pMaxU, float pMinV, float pMaxV) {
        PoseStack.Pose posestack$pose = pPoseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        renderQuad(matrix4f, matrix3f, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxY, pX0, pZ0, pX1, pZ1, pMinU,
                pMaxU, pMinV, pMaxV);
        renderQuad(matrix4f, matrix3f, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxY, pX3, pZ3, pX2, pZ2, pMinU,
                pMaxU, pMinV, pMaxV);
        renderQuad(matrix4f, matrix3f, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxY, pX1, pZ1, pX3, pZ3, pMinU,
                pMaxU, pMinV, pMaxV);
        renderQuad(matrix4f, matrix3f, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxY, pX2, pZ2, pX0, pZ0, pMinU,
                pMaxU, pMinV, pMaxV);
    }

    private static void renderQuad(Matrix4f pPose, Matrix3f pNormal, VertexConsumer pConsumer, float pRed, float pGreen,
            float pBlue, float pAlpha, int pMinY, int pMaxY, float pMinX, float pMinZ, float pMaxX, float pMaxZ,
            float pMinU, float pMaxU, float pMinV, float pMaxV) {
        addVertex(pPose, pNormal, pConsumer, pRed, pGreen, pBlue, pAlpha, pMaxY, pMinX, pMinZ, pMaxU, pMinV);
        addVertex(pPose, pNormal, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMinX, pMinZ, pMaxU, pMaxV);
        addVertex(pPose, pNormal, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxX, pMaxZ, pMinU, pMaxV);
        addVertex(pPose, pNormal, pConsumer, pRed, pGreen, pBlue, pAlpha, pMaxY, pMaxX, pMaxZ, pMinU, pMinV);
    }

    private static void addVertex(Matrix4f pPose, Matrix3f pNormal, VertexConsumer pConsumer, float pRed, float pGreen,
            float pBlue, float pAlpha, int pY, float pX, float pZ, float pU, float pV) {
        pConsumer.vertex(pPose, pX, (float) pY, pZ).color(pRed, pGreen, pBlue, pAlpha).uv(pU, pV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(pNormal, 0.0F, 1.0F, 0.0F).endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(NetherStarLaserBE pBlockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean shouldRender(NetherStarLaserBE pBlockEntity, Vec3 pCameraPos) {
        return true;
    }
}
