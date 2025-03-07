package kivo.millennium.millind.container;

import kivo.millennium.millind.block.AbstractDeviceBE;
import kivo.millennium.millind.init.MillenniumMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public abstract class AbstractDeviceMT extends AbstractContainerMenu {
    private final BlockPos pos;
    private int power;
    private int maxPower;

    public AbstractDeviceMT(int windowId, Player player, BlockPos pos, MenuType<?> menuType) {
        super(menuType, windowId);
        this.pos = pos;
        if (player.level().getBlockEntity(pos) instanceof AbstractDeviceBE deviceBE) {
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return deviceBE.getStoredPower() & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    AbstractDeviceMT.this.power = (AbstractDeviceMT.this.power & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (deviceBE.getStoredPower() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    AbstractDeviceMT.this.power = (AbstractDeviceMT.this.power & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return deviceBE.getMaxPower() & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    AbstractDeviceMT.this.maxPower = (AbstractDeviceMT.this.maxPower & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (deviceBE.getMaxPower() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    AbstractDeviceMT.this.maxPower = (AbstractDeviceMT.this.maxPower & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });
        }
        layoutPlayerInventorySlots(player.getInventory(), 10, 70);
    }


    public int getPower() {
        return power;
    }


    public int getMaxPower() {
        return maxPower;
    }

    private int addSlotRange(Container playerInventory, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new Slot(playerInventory, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(Container playerInventory, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(playerInventory, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(Container playerInventory, int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }
}
