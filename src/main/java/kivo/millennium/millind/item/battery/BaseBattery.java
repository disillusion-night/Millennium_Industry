package kivo.millennium.millind.item.battery;



import kivo.millennium.millind.util.NumberUtils;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BaseBattery extends Item {
    private int CAPACITY;
    private int TRANSFER_RATE;

    public BaseBattery(Properties pProperties, int Capacity, int MaxTransfer) {
        super(pProperties);
        this.CAPACITY = Capacity;
        this.TRANSFER_RATE = MaxTransfer;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable net.minecraft.nbt.CompoundTag nbt) {
        return new EnergyItemCapabilityProvider(stack, CAPACITY, TRANSFER_RATE, TRANSFER_RATE); // 创建能量 Capability Provider
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pStack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {
            pTooltipComponents.add(Component.literal("能量: " + NumberUtils.int2String(energy.getEnergyStored()) + " / " + NumberUtils.int2String(energy.getMaxEnergyStored()) + " FE")); // 显示能量信息在 Tooltip
        });
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }


    private static class EnergyItemCapabilityProvider implements ICapabilityProvider, IEnergyStorage {
        private final ItemStack stack;
        private final EnergyStorage energyStorage;

        public EnergyItemCapabilityProvider(ItemStack stack, int capacity, int receiveRate, int extractRate) {
            this.stack = stack;
            this.energyStorage = new EnergyStorage(capacity, receiveRate, extractRate) {
                @Override
                public int receiveEnergy(int maxReceive, boolean simulate) {
                    int received = super.receiveEnergy(maxReceive, simulate);
                    if (!simulate && received > 0) {
                        onEnergyChanged(); // 能量变化时更新 ItemStack NBT
                    }
                    return received;
                }

                @Override
                public int extractEnergy(int maxExtract, boolean simulate) {
                    int extracted = super.extractEnergy(maxExtract, simulate);
                    if (!simulate && extracted > 0) {
                        onEnergyChanged(); // 能量变化时更新 ItemStack NBT
                    }
                    return extracted;
                }
            };
        }

        private void onEnergyChanged() {
            if (stack.getTag() == null) {
                stack.setTag(new net.minecraft.nbt.CompoundTag());
            }
            stack.getTag().putInt("energy", this.energyStorage.getEnergyStored()); // 将能量值保存到 ItemStack NBT
        }

        @Override
        public <T> net.minecraftforge.common.util.@NotNull LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
            if (cap == ForgeCapabilities.ENERGY) {
                return net.minecraftforge.common.util.LazyOptional.of(() -> this.energyStorage).cast(); // 提供 Energy Capability
            }
            return net.minecraftforge.common.util.LazyOptional.empty();
        }

        @Override
        public int getEnergyStored() {
            if (stack.getTag() == null) {
                return 0; // 默认能量为 0
            }
            return stack.getTag().getInt("energy"); // 从 ItemStack NBT 读取能量值
        }

        @Override
        public int getMaxEnergyStored() {
            return this.energyStorage.getMaxEnergyStored(); // 返回最大能量容量
        }

        @Override
        public boolean canExtract() {
            return this.energyStorage.canExtract(); // 允许能量被提取
        }

        @Override
        public boolean canReceive() {
            return this.energyStorage.canReceive(); // 允许能量被接收
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return this.energyStorage.receiveEnergy(maxReceive, simulate); // 接收能量
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return this.energyStorage.extractEnergy(maxExtract, simulate); // 提取能量
        }
    }
}