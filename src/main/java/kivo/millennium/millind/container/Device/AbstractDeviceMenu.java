package kivo.millennium.millind.container.Device;

import kivo.millennium.millind.block.device.AbstractDeviceBE;
import kivo.millennium.millind.capability.DeviceEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemStackHandler;
import org.joml.Vector2i;

public abstract class AbstractDeviceMT extends AbstractContainerMenu {

    protected int SLOT_COUNT;
    protected int power;
    protected int maxpower;
    protected Vector2i BATTERY_SLOT_POS;
    protected AbstractDeviceBE blockEntity;
    protected Player player;
    protected BlockPos pos;
    protected ItemStackHandler itemHandler;
    protected DeviceEnergyStorage energyStorage;
    protected Level level;

    /*
    protected AbstractDeviceMT(int pContainerId, Player player, BlockPos pos) {
        this(null, pContainerId, player, pos); // 用于客户端创建容器
    }*/

    protected AbstractDeviceMT(MenuType<?> pType, int pContainerId, Player player, BlockPos pos, Container pContainer) {
        super(pType, pContainerId);
        this.player = player;
        this.level = player.level();
        this.pos = pos;
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof AbstractDeviceBE deviceBE) {
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return deviceBE.getEnergyStorage().getEnergyStored() & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    AbstractDeviceMT.this.power = (AbstractDeviceMT.this.power & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (deviceBE.getEnergyStorage().getEnergyStored() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    AbstractDeviceMT.this.power = (AbstractDeviceMT.this.power & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return deviceBE.getEnergyStorage().getMaxEnergyStored() & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    AbstractDeviceMT.this.maxpower = (AbstractDeviceMT.this.maxpower & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (deviceBE.getEnergyStorage().getMaxEnergyStored() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    AbstractDeviceMT.this.maxpower = (AbstractDeviceMT.this.maxpower & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });
            setupSlot(pContainer, deviceBE); // 设置容器的物品槽位，由子类实现
            layoutPlayerInventorySlots(player.getInventory(), this.getPlayerInvPos());
        } else {
            throw new IllegalStateException("Container Provider is not valid BlockEntity"); // 如果方块实体类型不匹配，抛出异常
        }
    }

    // 设置容器的物品槽位，子类需要覆写此方法以添加自定义槽位
    protected void setupSlot(Container container, AbstractDeviceBE deviceBE) {
        this.addBatterySlot(container, deviceBE.getItemHandler());
    }

    public abstract Vector2i getBatterySlotPos();

    public abstract Vector2i getPlayerInvPos();

    // 添加电池槽位
    protected void addBatterySlot(Container container, ItemStackHandler itemHandler) {
        if (itemHandler == null || getBatterySlotPos() == null) return; // 如果物品处理器为空，则不添加电池槽位 (安全检查)
        this.addSlot(new BatterySlot(container, itemHandler, 0, getBatterySlotPos())); // 使用 BatterySlot 类添加电池槽位
    }

    // 获取电池槽位索引，默认为 0，子类可以覆写以修改电池槽位索引 (如果需要的话)
    protected int getBatterySlotIndex() {
        return 0; // 默认电池槽位索引为 0
    }

    private int addSlotRange(Container playerInventory, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new Slot(playerInventory, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(Container playerInventory, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(playerInventory, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(Container playerInventory, Vector2i pos) {
        layoutPlayerInventorySlots(playerInventory, pos.x, pos.y);
    }

    private void layoutPlayerInventorySlots(Container playerInventory, int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

    // 能量传输逻辑（从设备容器到玩家物品栏）
    protected void transferEnergyToPlayerInventory() {
        if (this.energyStorage == null || !this.energyStorage.canExtract()) {
            return; // 没有能量存储或不允许提取能量，直接返回
        }

        Inventory playerInventory = this.player.getInventory();
        int transferRate = this.energyStorage.getMaxEnergyStored(); // 假设最大传输速率等于最大存储量，可以根据需要调整

        for (int i = 0; i < playerInventory.getContainerSize(); i++) {
            ItemStack stackInSlot = playerInventory.getItem(i);
            if (!stackInSlot.isEmpty()) {
                stackInSlot.getCapability(ForgeCapabilities.ENERGY).ifPresent(playerEnergyStorage -> {
                    if (playerEnergyStorage.canReceive()) {
                        int energyToTransfer = Math.min(this.energyStorage.getEnergyStored(), transferRate); // 计算可以传输的能量
                        int energyTransfered = playerEnergyStorage.receiveEnergy(energyToTransfer, false); // 尝试向玩家物品栏物品传输能量
                        this.energyStorage.extractEnergy(energyTransfered, false); // 从设备容器能量存储中提取已传输的能量
                    }
                });
            }
        }
    }


    // 容器逻辑
    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex < getSlotCount()) { // 容器自身的槽位
                if (!this.moveItemStackTo(itemstack1, getSlotCount(), 36 + getSlotCount(), true)) { // 移动到玩家物品栏
                    return ItemStack.EMPTY;
                }
            } else if (pIndex >= getSlotCount() && pIndex < 36 + getSlotCount()) { // 玩家物品栏槽位
                if (!this.moveItemStackTo(itemstack1, 0, getSlotCount(), false)) { // 移动到容器槽位
                    return ItemStack.EMPTY;
                }
            } else if (pIndex >= 36 + getSlotCount() && pIndex < 45 + getSlotCount()) { // 玩家快捷栏槽位
                if (!this.moveItemStackTo(itemstack1, 0, getSlotCount(), false)) { // 移动到容器槽位
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(pPlayer, itemstack1);
        }

        return itemstack;
    }

    public int getPower() {
        return power;
    }

    public int getMaxPower() {
        return maxpower;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), pos), player, getBlock());
    }

    protected abstract Block getBlock(); //  强制子类提供其 Block 实例

    protected int getSlotCount() {
        return SLOT_COUNT; // 返回槽位数量
    }
}