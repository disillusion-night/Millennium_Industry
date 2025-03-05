package kivo.millennium.millind.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.BlockPos;
import org.joml.Matrix4f;

public class HyperCubeRenderUtils {

    // 4D WZ 平面旋转
    public static float[] rotateWZ(float[] vertex4D, float cosTheta, float sinTheta) {
        float z = vertex4D[2];
        float w = vertex4D[3];
        float rotatedZ = cosTheta * z - sinTheta * w;
        float rotatedW = sinTheta * z + cosTheta * w;
        return new float[]{vertex4D[0], vertex4D[1], rotatedZ, rotatedW};
    }

    // 4D XY 平面旋转
    public static float[] rotateXY(float[] vertex4D, float cosTheta, float sinTheta) {
        float x = vertex4D[0];
        float y = vertex4D[1];
        float rotatedX = cosTheta * x - sinTheta * y;
        float rotatedY = sinTheta * x + cosTheta * y;
        return new float[]{rotatedX, rotatedY, vertex4D[2], vertex4D[3]}; // x, y 坐标旋转，z, w 坐标不变
    }

    // 4D to 3D (w = 0)
    public static float[] project4Dto3D(float[] vertex4D, float projectionDistance) {
        float w = vertex4D[3];
        float scale = projectionDistance / (projectionDistance + w + 4);
        float x = vertex4D[0] * scale;
        float y = vertex4D[1] * scale;
        float z = vertex4D[2] * scale;
        return new float[]{x, y, z};
    }

    // 顶点绘制辅助函数 (静态工具方法)
    public static void vertex(VertexConsumer builder, Matrix4f matrix4f, float[] vertex, float r, float g, float b, float alpha, float lineWidth, BlockPos offset, float packedOverlay) {
        builder.vertex(matrix4f, vertex[0] + offset.getX(), vertex[1] + offset.getY(), vertex[2] + offset.getZ())
                .color(r, g, b, alpha)
                .normal(0, 0, 0)
                .uv(0, 0)
                .uv2(0)
                .uv2((int)packedOverlay) // 注意类型转换，packedOverlay 是 int
                .endVertex();
    }

    public static void drawEdges(PoseStack poseStack, VertexConsumer builder, float[][] vertices, int[][] edgeIndices, BlockPos offset, float packedOverlay) { // vertices 参数类型改为 double[][]
        Matrix4f matrix4f = poseStack.last().pose();
        float r = 1.0F, g = 1.0F, b = 1.0F, alpha = 1.0F;
        float lineWidth = 2.0F;

        for (int[] edge : edgeIndices) {
            float[] vertex1f = {vertices[edge[0]][0], vertices[edge[0]][1], vertices[edge[0]][2]};
            float[] vertex2f = {vertices[edge[1]][0], vertices[edge[1]][1], vertices[edge[1]][2]};
            HyperCubeRenderUtils.vertex(builder, matrix4f, vertex1f, r, g, b, alpha, lineWidth, offset, packedOverlay);
            HyperCubeRenderUtils.vertex(builder, matrix4f, vertex2f, r, g, b, alpha, lineWidth, offset, packedOverlay);
        }
    }
}