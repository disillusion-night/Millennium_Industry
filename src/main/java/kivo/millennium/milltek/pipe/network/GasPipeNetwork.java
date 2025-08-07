package kivo.millennium.milltek.pipe.network;

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
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import java.util.UUID;
import java.util.List;

public class GasPipeNetwork extends AbstractLevelNetwork implements ICapabilityProvider {
    private final MillenniumGasStorage gasStorage;
    private final LazyOptional<MillenniumGasStorage> gasHandlerLazyOptional;
    private static final boolean DEBUG_TICK_LOG = true;
    private static final Logger logger = LogUtils.getLogger();

    public GasPipeNetwork(UUID uuid) {
        super(MillenniumLevelNetworkType.GAS_PIPE_NETWORK.get(), uuid);
        this.gasStorage = new MillenniumGasStorage(1, 100000);
        this.gasHandlerLazyOptional = LazyOptional.of(() -> gasStorage);
    }

    public GasPipeNetwork(CompoundTag tag) {
        super(MillenniumLevelNetworkType.GAS_PIPE_NETWORK.get(), tag);
        this.gasStorage = new MillenniumGasStorage(1, 100000);
        if (tag.contains("gas")) {
            this.gasStorage.deserializeNBT(tag.getCompound("gas"));
        }
        this.gasHandlerLazyOptional = LazyOptional.of(() -> gasStorage);
    }

    public MillenniumGasStorage getGasStorage() {
        return gasStorage;
    }

    public void setCapacity(int capacity) {
        this.gasStorage.setCapacity(0, capacity);
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
            // 合并气体存储
            MillenniumGasStorage otherStorage = gasPipeNetwork.getGasStorage();
            // 只合并第0槽
            this.gasStorage.addGasToTank(0, otherStorage.getGasInTank(0), true);
            otherStorage.setEmptyInTank(0);
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
                gasCap.ifPresent(gasHandler -> {
                    // 假设最大每tick吸入10000
                    int max_drain = 100000;
                    // 只处理第0槽
                    int received = this.gasStorage.fill(gasHandler.drain(max_drain, IGasHandler.GasAction.EXECUTE), IGasHandler.GasAction.EXECUTE);
                    if (DEBUG_TICK_LOG && received > 0) {
                        logger.info("[GasPipeNetwork] Received gas " + received + " mb from " + ctx.direction + " at " + ctx.pos);
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
                gasCap.ifPresent(gasHandler -> {
                    // 只处理第0槽
                    int sent = gasHandler.fill(this.gasStorage.drain(getGasStorage().getGasInTank(0), IGasHandler.GasAction.EXECUTE), IGasHandler.GasAction.EXECUTE);
                    if (sent > 0) {
                        this.gasStorage.getGasRefInTank(0).shrink(sent);
                        if (DEBUG_TICK_LOG) {
                            logger.info("[GasPipeNetwork] Sent gas to " + ctx.direction + " at " + ctx.pos);
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
        int mainAmount = this.gasStorage.getGasAmount(0);
        if (mainAmount <= 0) return;
        int toMove = (int) (mainAmount * ratio);
        if (toMove <= 0) return;
        // 拷贝气体类型
        var mainGas = this.gasStorage.getGasInTank(0);
        if (mainGas.isEmpty()) return;
        var moveStack = mainGas.copy();
        moveStack.setAmount(toMove);
        this.gasStorage.getGasRefInTank(0).shrink(toMove);
        gasSub.getGasStorage().addGasToTank(0, moveStack, true);
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

