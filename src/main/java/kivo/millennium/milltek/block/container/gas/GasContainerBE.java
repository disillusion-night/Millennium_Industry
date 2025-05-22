package kivo.millennium.milltek.block.container.gas;

import kivo.millennium.milltek.block.container.base.AbstractContainerBE;
import kivo.millennium.milltek.block.property.EFaceMode;
import kivo.millennium.milltek.gas.IGasHandler;
import kivo.millennium.milltek.init.MillenniumCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

public abstract class GasContainerBE extends AbstractContainerBE {
    protected LazyOptional<IGasHandler> gasHandler = LazyOptional.empty();

    public GasContainerBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.gasHandler = LazyOptional
                .of(() -> new ContainerGasTank(getMaxGasAmount(), getMaxInputRate(), getMaxOutputRate()));
    }

    public static int getMaxGasAmount() {
        return 10000;
    }

    public static int getMaxInputRate() {
        return 1000;
    }

    public static int getMaxOutputRate() {
        return 1000;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == MillenniumCapabilities.GAS) {
            return gasHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void handlePull(Direction dir) {
        Level lvl = this.level;
        if (lvl == null || lvl.isClientSide())
            return;
        IGasHandler self = gasHandler.orElseGet(() -> null);
        if (self == null)
            return;
        BlockEntity neighbor = lvl.getBlockEntity(worldPosition.relative(dir));
        if (neighbor != null) {
            // neighbor.getCapability(ForgeCapabilities.GAS_HANDLER, dir.getOpposite()) ...
            // 伪代码：实现与fluid类似的pull逻辑
        }
    }

    @Override
    protected void handlePush(Direction dir) {
        Level lvl = this.level;
        if (lvl == null || lvl.isClientSide())
            return;
        IGasHandler self = gasHandler.orElseGet(() -> null);
        if (self == null)
            return;
        BlockEntity neighbor = lvl.getBlockEntity(worldPosition.relative(dir));
        if (neighbor != null) {
            // neighbor.getCapability(ForgeCapabilities.GAS_HANDLER, dir.getOpposite()) ...
            // 伪代码：实现与fluid类似的push逻辑
        }
    }
}
