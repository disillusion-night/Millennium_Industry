package kivo.millennium.client.render.blockEntity;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import kivo.millennium.millind.block.device.MeltingFurnace.MeltingFurnaceBE;
import kivo.millennium.millind.capability.MillenniumFluidStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.checkerframework.checker.units.qual.A;
import org.joml.Matrix4f;


public class MeltingFurnaceBER implements BlockEntityRenderer<MeltingFurnaceBE> {

    private static final int FLUID_COLOR_WHITE = 0xFFFFFFFF; // 白色填充颜色
    private static final int FLUID_RENDER_X_MIN = 2;
    private static final int FLUID_RENDER_Y_MIN = 2;
    private static final int FLUID_RENDER_X_MAX = 5; // 包含
    private static final int FLUID_RENDER_Y_MAX = 13; // 包含
    private static final int FLUID_RENDER_WIDTH = FLUID_RENDER_X_MAX - FLUID_RENDER_X_MIN + 1; // 4
    private static final int FLUID_RENDER_HEIGHT = FLUID_RENDER_Y_MAX - FLUID_RENDER_Y_MIN + 1; // 12

    public MeltingFurnaceBER(BlockEntityRendererProvider.Context context) {
        // 初始化或其他设置
    }

    @Override
    public void render(MeltingFurnaceBE blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        MillenniumFluidStorage fluidTank = blockEntity.getFluidTank();
        BlockState blockState = blockEntity.getBlockState();
        Direction facing = blockState.getValue(net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING);

        if (fluidTank != null && fluidTank.getFluidAmount(0) > 0) {
            FluidStack fluidStack = fluidTank.getFluidInTank(0);
            IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
            TextureAtlasSprite fluidSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidTypeExtensions.getStillTexture());
            int fluidColor = fluidTypeExtensions.getTintColor(fluidStack);
            float fillPercentage = (float) fluidTank.getFluidAmount(0) / fluidTank.getTankCapacity(0);

            poseStack.pushPose();

            // 不需要再进行复杂的 translateAndRotate，模型已经处理了朝向
            // translateAndRotate(poseStack, facing);

            Matrix4f matrix = poseStack.last().pose();
            VertexConsumer builder = bufferSource.getBuffer(RenderType.translucent());


            // 直接渲染到模型预留的面上
            // 我们只需要控制流体的高度

            // 获取预留面的 UV 坐标 (在模型中定义的是 2, 2, 6, 14)
            float uMin = 2 / 16.0f;
            float vMin = 2 / 16.0f;
            float uMax = 6 / 16.0f;
            float vMax = 14 / 16.0f;

            // 根据填充百分比计算流体的高度 (在 UV 坐标范围内)
            float fluidVMax = vMin + (vMax - vMin) * fillPercentage;

            // 获取流体纹理对应的 UV 坐标
            float spriteUMin = fluidSprite.getU(16 * uMin);
            float spriteVMin = fluidSprite.getV(16 * fluidVMax); // 注意这里使用了 fluidVMax
            float spriteUMax = fluidSprite.getU(16 * uMax);
            float spriteVMaxSprite = fluidSprite.getV(16 * vMin); // 注意这里使用了 vMin

            // Render the north-facing face
            builder.vertex(matrix, 2/16.0f, (14 - (12 * fillPercentage))/16.0f, 0.1f/16.0f).color(fluidColor).uv(spriteUMin, spriteVMaxSprite).uv2(packedLight, packedOverlay).normal(0, 0, -1).endVertex();
            builder.vertex(matrix, 6/16.0f, (14 - (12 * fillPercentage))/16.0f, 0.1f/16.0f).color(fluidColor).uv(spriteUMax, spriteVMaxSprite).uv2(packedLight, packedOverlay).normal(0, 0, -1).endVertex();
            builder.vertex(matrix, 6/16.0f, 14/16.0f, 0.1f/16.0f).color(fluidColor).uv(spriteUMax, spriteVMin).uv2(packedLight, packedOverlay).normal(0, 0, -1).endVertex();
            builder.vertex(matrix, 2/16.0f, 14/16.0f, 0.1f/16.0f).color(fluidColor).uv(spriteUMin, spriteVMin).uv2(packedLight, packedOverlay).normal(0, 0, -1).endVertex();

            poseStack.popPose();
        }
    }
}