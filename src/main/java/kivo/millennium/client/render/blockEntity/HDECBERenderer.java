package kivo.millennium.client.render.blockEntity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import kivo.millennium.milltek.block.hypercube.HDECBE;
import kivo.millennium.milltek.util.HyperCubeRenderUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;


public class HDECBERenderer implements BlockEntityRenderer<HDECBE> {

    public HDECBERenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(HDECBE blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        RenderSystem.disableDepthTest();
        poseStack.translate(0.5f, 0.5f, 0.5f);

        float time = blockEntity.getLevel().getGameTime() + partialTick;
        float rotationSpeed = 0.5f; // 3D 旋转速度
        float rotationAngle = time * rotationSpeed;

        float rotation4DAngle = time * rotationSpeed * 0.5f; // 4D 旋转速度，保持每 tick 0.5 度
        float cosTheta = Mth.cos(rotation4DAngle * Mth.DEG_TO_RAD);
        float sinTheta = Mth.sin(rotation4DAngle * Mth.DEG_TO_RAD);

        poseStack.mulPose(Axis.YP.rotationDegrees(rotationAngle));
        poseStack.mulPose(Axis.XP.rotationDegrees(rotationAngle * 0.5f));

        float scale = 0.9f; // 缩放比例
        poseStack.scale(scale, scale, scale);

        VertexConsumer builder = bufferSource.getBuffer(RenderType.lines());

        // 超立方体顶点数据
        float[][] vertices4D = {
                {-1, -1, -1, -1}, {-1, -1, -1, 1}, {-1, -1, 1, -1}, {-1, -1, 1, 1},
                {-1, 1, -1, -1}, {-1, 1, -1, 1}, {-1, 1, 1, -1}, {-1, 1, 1, 1},
                {1, -1, -1, -1}, {1, -1, -1, 1}, {1, -1, 1, -1}, {1, -1, 1, 1},
                {1, 1, -1, -1}, {1, 1, -1, 1}, {1, 1, 1, -1}, {1, 1, 1, 1}
        };

        float[][] vertices3D = new float[vertices4D.length][3];
        float projectionDistance = 3.0f; // 投影距离

        for (int i = 0; i < vertices4D.length; i++) {
            float[] rotatedVertex4D_WZ = HyperCubeRenderUtils.rotateWZ(vertices4D[i], cosTheta, sinTheta);
            float[] rotatedVertex4D = HyperCubeRenderUtils.rotateXY(rotatedVertex4D_WZ, cosTheta, sinTheta);
            vertices3D[i] = HyperCubeRenderUtils.project4Dto3D(rotatedVertex4D, projectionDistance);
        }


        HyperCubeRenderUtils.drawEdges(poseStack, builder, vertices3D, getEdgeIndices(), getRenderOffset(), packedOverlay);


        poseStack.popPose();
    }


    private int[][] getEdgeIndices() {
        return new int[][]{
                {0, 1}, {0, 2}, {0, 4}, {0, 8},
                {1, 3}, {1, 5}, {1, 9}, {2, 3},
                {2, 6}, {2, 10}, {3, 7}, {3, 11},
                {4, 5}, {4, 6}, {4, 12}, {5, 7},
                {5, 13}, {6, 7}, {6, 14}, {7, 15},
                {8, 9}, {8, 10}, {8, 12}, {9, 11},
                {9, 13}, {10, 11}, {10, 14}, {11, 15},
                {12, 13}, {12, 14}, {13, 15}, {14, 15}
        };
    }

    protected BlockPos getRenderOffset() {
        return BlockPos.ZERO;
    }
}