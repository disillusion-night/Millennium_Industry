package kivo.millennium.millind.container.Device;


import kivo.millennium.millind.block.generator.GeneratorBE;
import kivo.millennium.millind.capability.TestSlot;
import kivo.millennium.millind.init.MillenniumBlocks;
import kivo.millennium.millind.init.MillenniumMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.joml.Vector2i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static kivo.millennium.millind.block.generator.GeneratorBE.SLOT;
import static kivo.millennium.millind.block.generator.GeneratorBE.SLOT_COUNT;

public class GeneratorMT extends AbstractContainerMenu {
    private static final Logger log = LoggerFactory.getLogger(GeneratorMT.class);
    public static Vector2i slotpos1 = new Vector2i(64, 24);
    private final BlockPos pos;
    private int power;
    protected Vector2i powerSourceSlot;

    public GeneratorMT(int pContainerId, Player player, BlockPos pos) {
        this(pContainerId, player, pos, new SimpleContainer(1));
    }

    public GeneratorMT(int windowId, Player player, BlockPos pos, Container pBrewingStandContainer) {
        super(MillenniumMenuTypes.GENERATOR_MENU.get(), windowId);
        this.pos = pos;
        if (player.level().getBlockEntity(pos) instanceof GeneratorBE generator) {
            addSlot(new TestSlot(pBrewingStandContainer, generator.getItems(), SLOT, slotpos1.x, slotpos1.y));
            addSlot(new TestSlot(pBrewingStandContainer,generator.getItems(), 1, slotpos1.x + 18, slotpos1.y));
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return generator.getStoredPower() & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    GeneratorMT.this.power = (GeneratorMT.this.power & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (generator.getStoredPower() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    GeneratorMT.this.power = (GeneratorMT.this.power & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });


        }
        layoutPlayerInventorySlots(player.getInventory(), 10, 70);
    }

    public int getPower() {
        return power;
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

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if (index < SLOT_COUNT) {
                if (!this.moveItemStackTo(stack, SLOT_COUNT, Inventory.INVENTORY_SIZE + SLOT_COUNT, true)) {
                    return ItemStack.EMPTY;
                }
            }
            if (!this.moveItemStackTo(stack, SLOT, SLOT+1, false)) {
                if (index < 27 + SLOT_COUNT) {
                    if (!this.moveItemStackTo(stack, 27 + SLOT_COUNT, 36 + SLOT_COUNT, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < Inventory.INVENTORY_SIZE + SLOT_COUNT && !this.moveItemStackTo(stack, SLOT_COUNT, 27 + SLOT_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), pos), player, MillenniumBlocks.GENERATOR_BL.get());
    }
}