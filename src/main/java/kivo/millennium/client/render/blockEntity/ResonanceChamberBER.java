package kivo.millennium.client.render.blockEntity;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kivo.millennium.millind.block.device.ResonanceChamber.ResonanceChamberBE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ResonanceChamberBER implements BlockEntityRenderer<ResonanceChamberBE> {
    private final ItemRenderer itemRenderer;

    public ResonanceChamberBER(BlockEntityRendererProvider.Context pContext) {
        this.itemRenderer = pContext.getItemRenderer();
        // 初始化或其他设置
    }

    @Override
    public void render(ResonanceChamberBE blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack itemStack = blockEntity.getItemHandler().getStackInSlot(1);
        if (!itemStack.isEmpty()) {
            Level level = blockEntity.getLevel();
            BlockPos pos = blockEntity.getBlockPos();
            BlockState state = blockEntity.getBlockState();
            int light = LevelRenderer.getLightColor(level, pos.relative(state.getValue(HorizontalDirectionalBlock.FACING)));

            poseStack.pushPose();

            poseStack.translate(0.5D, 0.5D, 0.5D);

            // 可以根据需要缩放物品
            float scale = 0.4F;
            poseStack.scale(scale, scale, scale);

            // 让物品缓慢旋转看起来更生动
            long worldTime = level != null ? level.getGameTime() : 0;
            poseStack.mulPose(Axis.YP.rotationDegrees((worldTime) * 4));

            // RenderType.entitySolid() 或其他合适的 RenderType
            itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, light, packedOverlay, poseStack, bufferSource, level, 0);

            poseStack.popPose();
        }
    }
}