package kivo.millennium.client.render.blockEntity;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import kivo.millennium.millind.block.hypercube.HDECBE;
import kivo.millennium.millind.block.hypercube.HDECBL;
import kivo.millennium.millind.util.HyperCubeRenderUtils;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class HDECBIRenderer extends BlockEntityWithoutLevelRenderer {

    private final BlockEntityRenderer<HDECBE> blockEntityRenderer;

    public HDECBIRenderer(BlockEntityRendererProvider.Context context) {
        super(context.getBlockEntityRenderDispatcher(), context.getModelSet());
        this.blockEntityRenderer = new HDECBERenderer(context);
    }

    @Override
    public void renderByItem(ItemStack pStack, ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        BlockState blockState = ((BlockItem) pStack.getItem()).getBlock().defaultBlockState();
        if (!(blockState.getBlock() instanceof HDECBL)) { // 确保是你的超立方体方块
            return; // 如果不是，则不渲染，或者你可以提供默认的物品渲染
        }

        //  以下代码很大程度上复用自你的 HypercubeBlockRenderer.render() 方法，但需要进行调整以适应物品渲染的上下文

        pPoseStack.pushPose();
        pPoseStack.translate(0.5f, 0.5f, 0.5f); //  物品渲染通常中心点在 (0.5, 0.5, 0.5)

        float time = 0.0f; //  使用物品的悬浮动画计数器作为时间，你也可以使用其他时间源，例如 Minecraft.getInstance().level.getGameTime()
        float rotationSpeed = 0.5f; // 旋转速度，可以根据需要调整
        float rotationAngle = time * rotationSpeed;

        float rotation4DAngle = time * rotationSpeed * 0.5f;
        float cosTheta = net.minecraft.util.Mth.cos(rotation4DAngle * net.minecraft.util.Mth.DEG_TO_RAD);
        float sinTheta = net.minecraft.util.Mth.sin(rotation4DAngle * net.minecraft.util.Mth.DEG_TO_RAD);


        pPoseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotationAngle));
        pPoseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(rotationAngle * 0.5f));

        float scale = 0.7f;
        pPoseStack.scale(scale, scale, scale);

        VertexConsumer builder = pBuffer.getBuffer(RenderType.lines());

        float[][] vertices4D = { // 4D 超立方体顶点数据 (保持不变) ... };
                {-1, -1, -1, -1}, {-1, -1, -1, 1}, {-1, -1, 1, -1}, {-1, -1, 1, 1},
                {-1, 1, -1, -1}, {-1, 1, -1, 1}, {-1, 1, 1, -1}, {-1, 1, 1, 1},
                {1, -1, -1, -1}, {1, -1, -1, 1}, {1, -1, 1, -1}, {1, -1, 1, 1},
                {1, 1, -1, -1}, {1, 1, -1, 1}, {1, 1, 1, -1}, {1, 1, 1, 1}
        };

        float[][] vertices3D = new float[16][3];
        float projectionDistance = 3.0f;

        for (int i = 0; i < 16; i++) {
            float[] rotatedVertex4D = HyperCubeRenderUtils.rotateWZ(vertices4D[i], cosTheta, sinTheta);
            vertices3D[i] = HyperCubeRenderUtils.project4Dto3D(rotatedVertex4D, projectionDistance);
        }

        //  注意这里 packedLight 和 packedOverlay 使用的是 renderByItem 方法传入的参数
        HyperCubeRenderUtils.drawEdges(pPoseStack, builder, vertices3D, getEdgeIndices(), getRenderOffset(), pPackedLight);


        pPoseStack.popPose();
    }
    private int[][] getEdgeIndices() { // 边索引数据 (保持不变) ... };
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


    protected BlockPos getRenderOffset() { // 渲染偏移量 (保持不变) ... };
        return BlockPos.ZERO;
    }
}