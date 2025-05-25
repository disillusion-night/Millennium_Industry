package kivo.millennium.milltek.block.container.energy;

import kivo.millennium.milltek.block.container.base.AbstractContainerBE;
import kivo.millennium.milltek.block.property.EFaceMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public abstract class EnergyContainerBE extends AbstractContainerBE {
    protected LazyOptional<IEnergyStorage> energyHandler = LazyOptional.empty();

    public EnergyContainerBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY && energyHandler.isPresent()) {
            EFaceMode mode = getFaceMode(side);
            if (mode == EFaceMode.DISCONNECT) {
                return LazyOptional.empty();
            }
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    /**
     * 获取本方块实体的最大能量存储量（可被子类覆写）
     */
    public static int getMaxEnergyStorage() {
        return 100000; // 默认值，子类可覆写
    }

    /**
     * 获取本方块实体的最大能量输入速率（可被子类覆写）
     */
    public static int getMaxInputRate() {
        return 1000; // 默认值，子类可覆写
    }

    /**
     * 获取本方块实体的最大能量输出速率（可被子类覆写）
     */
    public static int getMaxOutputRate() {
        return 1000; // 默认值，子类可覆写
    }

    /**
     * 可供子类调用的能量存储构造方法
     */
    protected IEnergyStorage createEnergyStorage() {
        return new IEnergyStorage() {
            private int energy = 0;

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                int received = Math.min(getMaxInputRate(), Math.min(getMaxEnergyStorage() - energy, maxReceive));
                if (!simulate)
                    energy += received;
                return received;
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                int extracted = Math.min(getMaxOutputRate(), Math.min(energy, maxExtract));
                if (!simulate)
                    energy -= extracted;
                return extracted;
            }

            @Override
            public int getEnergyStored() {
                return energy;
            }

            @Override
            public int getMaxEnergyStored() {
                return getMaxEnergyStorage();
            }

            @Override
            public boolean canExtract() {
                return getMaxOutputRate() > 0;
            }

            @Override
            public boolean canReceive() {
                return getMaxInputRate() > 0;
            }
        };
    }

    @Override
    protected void handlePull(Direction dir) {
        Level lvl = this.level;
        if (lvl == null || lvl.isClientSide())
            return;
        IEnergyStorage self = energyHandler.orElseGet(() -> null);
        if (self == null)
            return;
        BlockEntity neighbor = lvl.getBlockEntity(worldPosition.relative(dir));
        if (neighbor != null) {
            neighbor.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).ifPresent(handler -> {
                int canExtract = handler.extractEnergy(getMaxInputRate(), true);
                if (canExtract > 0) {
                    int received = self.receiveEnergy(canExtract, false);
                    handler.extractEnergy(received, false);
                }
            });
        }
    }

    @Override
    protected void handlePush(Direction dir) {
        Level lvl = this.level;
        if (lvl == null || lvl.isClientSide())
            return;
        IEnergyStorage self = energyHandler.orElseGet(() -> null);
        if (self == null)
            return;
        BlockEntity neighbor = lvl.getBlockEntity(worldPosition.relative(dir));
        if (neighbor != null) {
            neighbor.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).ifPresent(handler -> {
                int canReceive = handler.receiveEnergy(getMaxOutputRate(), true);
                if (canReceive > 0) {
                    int extracted = self.extractEnergy(canReceive, false);
                    handler.receiveEnergy(extracted, false);
                }
            });
        }
    }
}
