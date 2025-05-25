package kivo.millennium.milltek.pipe.network;

import java.util.List;

import kivo.millennium.milltek.init.MillenniumBlockEntities;
import kivo.millennium.milltek.init.MillenniumBlocks;
import kivo.millennium.milltek.init.MillenniumLevelNetworkType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraft.world.level.Level;

public class EnergyPipeBE extends PipeBE<EnergyPipeNetwork> {
    public EnergyPipeBE(BlockPos pos, BlockState state) {
        super(MillenniumBlockEntities.ENERGY_PIPE_BE.get(), MillenniumLevelNetworkType.ENERGY_PIPE_NETWORK.get(), pos,
                state);
    }

    @Override
    protected void handleInputToNetwork(EnergyPipeNetwork network, java.util.List<IOEntry> inputTargets) {
        Level level = getLevel();
        if (level == null || level.isClientSide)
            return;
        var energyStorage = network.getEnergyStorage();
        for (IOEntry entry : inputTargets) {
            if (entry.be != null) {
                entry.be.getCapability(ForgeCapabilities.ENERGY, entry.direction.getOpposite())
                        .ifPresent(handler -> {
                            int canExtract = handler.extractEnergy(this.maxInputPerTick, true);
                            if (canExtract > 0) {
                                int accepted = energyStorage.receiveEnergy(canExtract, true);
                                if (accepted > 0) {
                                    int actuallyExtracted = handler.extractEnergy(accepted, false);
                                    energyStorage.receiveEnergy(actuallyExtracted, false);
                                }
                            }
                        });
            }
        }
    }

    @Override
    protected void handleOutputFromNetwork(EnergyPipeNetwork network, java.util.List<IOEntry> outputTargets) {
        Level level = getLevel();
        if (level == null || level.isClientSide)
            return;
        var energyStorage = network.getEnergyStorage();
        for (IOEntry entry : outputTargets) {
            if (entry.be != null) {
                entry.be.getCapability(ForgeCapabilities.ENERGY, entry.direction.getOpposite())
                        .ifPresent(handler -> {
                            int canInsert = handler.receiveEnergy(this.maxOutputPerTick, true);
                            if (canInsert > 0) {
                                int extracted = energyStorage.extractEnergy(canInsert, true);
                                if (extracted > 0) {
                                    int actuallyInserted = handler.receiveEnergy(extracted, false);
                                    energyStorage.extractEnergy(actuallyInserted, false);
                                }
                            }
                        });
            }
        }
    }

    @Override
    protected void redistributeNetworkContent(List<EnergyPipeNetwork> newNetworks,
            List<List<PipeBE<EnergyPipeNetwork>>> groups) {
        // 简单策略：平均分配原网络能量到新网络
        int totalEnergy = 0;
        for (EnergyPipeNetwork net : newNetworks) {
            totalEnergy += net.getEnergyStorage().getEnergyStored();
        }
        int avg = newNetworks.isEmpty() ? 0 : totalEnergy / newNetworks.size();
        for (EnergyPipeNetwork net : newNetworks) {
            net.getEnergyStorage().setCapacity(net.getEnergyStorage().getMaxEnergyStored());
            // 直接设置能量（如需更复杂策略可自定义）
            try {
                java.lang.reflect.Field f = net.minecraftforge.energy.EnergyStorage.class.getDeclaredField("energy");
                f.setAccessible(true);
                f.setInt(net.getEnergyStorage(), avg);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    protected void mergeNetworkContent(EnergyPipeNetwork mainNetwork, EnergyPipeNetwork otherNetwork) {
        // 简单策略：把otherNetwork的能量全部加到mainNetwork
        var mainStorage = mainNetwork.getEnergyStorage();
        var otherStorage = otherNetwork.getEnergyStorage();
        int total = mainStorage.getEnergyStored() + otherStorage.getEnergyStored();
        int max = mainStorage.getMaxEnergyStored();
        // 尽量不丢失能量，超出部分丢弃
        mainStorage.setCapacity(max);
        try {
            java.lang.reflect.Field f = net.minecraftforge.energy.EnergyStorage.class.getDeclaredField("energy");
            f.setAccessible(true);
            f.setInt(mainStorage, Math.min(total, max));
            f.setInt(otherStorage, 0);
        } catch (Exception ignored) {
        }
    }

    @Override
    protected Block getBlock() {
        return MillenniumBlocks.ENERGY_PIPE.get();
    }
}
