package kivo.millennium.milltek.pipe.network;

import kivo.millennium.milltek.gas.GasStack;
import kivo.millennium.milltek.gas.IGasHandler;
import kivo.millennium.milltek.init.MillenniumCapabilities;
import kivo.millennium.milltek.init.MillenniumLevelNetworkType;
import kivo.millennium.milltek.storage.MillenniumGasStorage;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import java.util.UUID;
import java.util.List;

import static kivo.millennium.milltek.gas.IGasHandler.GasAction.EXECUTE;
import static kivo.millennium.milltek.gas.IGasHandler.GasAction.SIMULATE;

public class GasPipeNetwork extends AbstractLevelNetwork implements ICapabilityProvider {
    private final PipeGasStorage gasStorage;
    private final LazyOptional<PipeGasStorage> gasHandlerLazyOptional;
    private static final boolean DEBUG_TICK_LOG = true;
    private static final Logger logger = LogUtils.getLogger();

    public GasPipeNetwork(UUID uuid) {
        super(MillenniumLevelNetworkType.GAS_PIPE_NETWORK.get(), uuid);
        this.gasStorage = new PipeGasStorage(100000);
        this.gasHandlerLazyOptional = LazyOptional.of(() -> gasStorage);
    }

    public GasPipeNetwork(CompoundTag tag) {
        super(MillenniumLevelNetworkType.GAS_PIPE_NETWORK.get(), tag);
        this.gasStorage = new PipeGasStorage(tag.getCompound("gas"));
        this.gasHandlerLazyOptional = LazyOptional.of(() -> gasStorage);
    }

    public PipeGasStorage getGasStorage() {
        return gasStorage;
    }

    public void setCapacity(int capacity) {
        this.gasStorage.setCapacity(capacity);
        setDirty();
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.put("gas", gasStorage.serializeNBT());
    }

    @Override
    public boolean canMerge(AbstractLevelNetwork other) {
        return other instanceof GasPipeNetwork;
    }

    @Override
    protected void mergeCapabilities(AbstractLevelNetwork other) {
        if (other instanceof GasPipeNetwork gasPipeNetwork) {
            getGasStorage().merge(gasPipeNetwork.gasStorage);
            if (DEBUG_TICK_LOG) {
                logger.info("[GasPipeNetwork] Merged gas storage from " + other.getUUID());
            }
        }
    }

    @Override
    protected void handleInput(ServerLevel level, List<TargetContext> inputTargets) {
        int n = inputTargets.size();
        if (n == 0) return;
        for (TargetContext ctx : inputTargets) {
            BlockEntity blockEntity = level.getBlockEntity(ctx.pos.relative(ctx.direction));
            if (blockEntity != null) {
                LazyOptional<IGasHandler> gasCap = blockEntity.getCapability(MillenniumCapabilities.GAS, ctx.direction.getOpposite());
                gasCap.ifPresent(target -> {
                    if (getGasStorage().isGasValid(0, target.getGasInTank(0))) {
                        GasStack gasInPipe = getGasStorage().getGasStack();
                        int max_in = 10000; // 每次最多输入10000mb
                        GasStack toFill = target.drain(max_in, SIMULATE);
                        if (!toFill.isEmpty() ) {
                            int filled = getGasStorage().fill(toFill, EXECUTE);
                            target.drain(filled, EXECUTE);
                            if (DEBUG_TICK_LOG) {
                                logger.info("[GasPipeNetwork] Filled " + filled + "mb "+ getGasStorage().getGasStack().getGas().getRegistryName().toString() + "from " + ctx.direction + " at " + ctx.pos);
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void handleOutput(ServerLevel level, List<TargetContext> outputTargets) {
        int n = outputTargets.size();
        if (n == 0) return;
        for (TargetContext ctx : outputTargets) {
            BlockEntity blockEntity = level.getBlockEntity(ctx.pos.relative(ctx.direction));
            if (blockEntity != null) {
                LazyOptional<IGasHandler> gasCap = blockEntity.getCapability(MillenniumCapabilities.GAS, ctx.direction.getOpposite());
                gasCap.ifPresent(target -> {
                    for (int i = 0; i < target.getTanks(); i++) {
                        GasStack gasInPipe = getGasStorage().getGasInTank(0);
                        if (!gasInPipe.isEmpty() && getGasStorage().isGasValid(0, gasInPipe)) {
                            int max_out = 10000;
                            int toDrain = target.fill(new GasStack(gasInPipe.getGas(), max_out), SIMULATE);
                            if (toDrain > 0) {
                                GasStack drained = getGasStorage().drain(toDrain, EXECUTE);
                                target.fill(drained, EXECUTE);
                                if (DEBUG_TICK_LOG) {
                                    logger.info("[GasPipeNetwork] Drained " + drained.getAmount() + "mb" + getGasStorage().getGasStack().getGas().getRegistryName() +" to " + ctx.direction + " at " + ctx.pos);
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void distributeCapas(AbstractLevelNetwork subNetwork, float ratio) {
        if (!(subNetwork instanceof GasPipeNetwork gasSub)) return;
        if (ratio <= 0) return;
        // 只分配第0槽
        int mainAmount = this.gasStorage.getGasStack().getAmount();
        if (mainAmount <= 0) return;
        int toMove = (int) (mainAmount * ratio);
        if (toMove <= 0) return;
        // 拷贝气体类型
        var mainGas = this.gasStorage.getGasInTank(0);
        if (mainGas.isEmpty()) return;
        var moveStack = mainGas.copy();
        moveStack.setAmount(toMove);
        this.gasStorage.getGasStack().shrink(toMove);
        gasSub.getGasStorage().fill(moveStack, EXECUTE);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        // 这里假设ForgeCapabilities.GAS_HANDLER存在
        if (cap == MillenniumCapabilities.GAS) {
            return gasHandlerLazyOptional.cast();
        }
        return LazyOptional.empty();
    }
}

