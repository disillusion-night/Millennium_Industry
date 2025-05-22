package kivo.millennium.milltek.block.container.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import kivo.millennium.milltek.block.property.EFaceMode;
import javax.annotation.Nonnull;

public abstract class AbstractContainerBE extends BlockEntity {
    public AbstractContainerBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // 通过 BlockState 获取面模式
    public EFaceMode getFaceMode(Direction dir) {
        if (level == null)
            return EFaceMode.NONE;
        BlockState state = getBlockState();
        if (state.getBlock() instanceof kivo.millennium.milltek.block.container.base.AbstractContainerBL block) {
            return block.getFaceMode(state, dir);
        }
        return EFaceMode.NONE;
    }

    // 通过 BlockState 设置面模式（会触发方块状态更新）
    public void setFaceMode(Direction dir, EFaceMode mode) {
        if (level == null)
            return;
        BlockState state = getBlockState();
        if (state.getBlock() instanceof kivo.millennium.milltek.block.container.base.AbstractContainerBL block) {
            BlockState newState = block.setFaceMode(state, dir, mode);
            if (newState != state && level != null) {
                level.setBlock(worldPosition, newState, 3);
            }
        }
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AbstractContainerBE blockEntity) {
        if (level == null || level.isClientSide()) {
            return;
        }
        blockEntity.tickServer();
    }

    public void tickServer() {
        // 默认遍历所有面，自动处理pull/push逻辑（能量/流体/气体等）
        for (Direction dir : Direction.values()) {
            EFaceMode mode = getFaceMode(dir);
            if (mode == EFaceMode.PULL) {
                handlePull(dir);
            } else if (mode == EFaceMode.PUSH) {
                handlePush(dir);
            }
            // NONE: 仅被动访问，DISCONNECT: 不做任何操作
        }
    }

    /**
     * 钩子：每tick每面调用，子类可覆写实现pull/push等自动化逻辑
     * 若子类需要自定义更细致的行为，可覆写本方法
     */
    protected void onTickFace(Direction dir) {
        // 默认无操作
    }

    /**
     * 自动pull逻辑，子类可覆写实现能量/流体/气体等的拉取
     */
    protected void handlePull(Direction dir) {
        // 默认无操作，子类如EnergyContainerBE可实现能量拉取
    }

    /**
     * 自动push逻辑，子类可覆写实现能量/流体/气体等的推送
     */
    protected void handlePush(Direction dir) {
        // 默认无操作，子类如EnergyContainerBE可实现能量推送
    }
}
