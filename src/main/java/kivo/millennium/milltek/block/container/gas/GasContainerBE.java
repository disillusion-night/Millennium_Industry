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
        if (cap == MillenniumCapabilities.GAS && getFaceMode(side) != EFaceMode.DISCONNECT) {
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
            neighbor.getCapability(MillenniumCapabilities.GAS, dir.getOpposite()).ifPresent(handler -> {
                int amount = getMaxInputRate();
                for (int tank = 0; tank < handler.getTanks(); tank++) {
                    var drain = handler.drain(amount, IGasHandler.GasAction.SIMULATE);
                    if (!drain.isEmpty() && drain.getAmount() > 0) {
                        int filled = self.fill(drain, IGasHandler.GasAction.EXECUTE);
                        if (filled > 0) {
                            handler.drain(filled, IGasHandler.GasAction.EXECUTE);
                        }
                    }
                }
            });
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
            neighbor.getCapability(MillenniumCapabilities.GAS, dir.getOpposite()).ifPresent(handler -> {
                int amount = getMaxOutputRate();
                for (int tank = 0; tank < self.getTanks(); tank++) {
                    var stack = self.getGasInTank(tank);
                    if (!stack.isEmpty() && stack.getAmount() > 0) {
                        var toSend = stack.copy();
                        toSend.setAmount(Math.min(amount, stack.getAmount()));
                        int filled = handler.fill(toSend, IGasHandler.GasAction.EXECUTE);
                        if (filled > 0) {
                            self.drain(filled, IGasHandler.GasAction.EXECUTE);
                        }
                    }
                }
            });
        }
    }
}
