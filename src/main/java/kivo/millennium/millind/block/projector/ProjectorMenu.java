package kivo.millennium.millind.block.projector;

import kivo.millennium.millind.init.MillenniumBlockEntities;
import kivo.millennium.millind.init.MillenniumBlocks;
import kivo.millennium.millind.init.MillenniumMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;


public class ProjectorMenu extends AbstractContainerMenu {
    public final ProjectorBE blockEntity;
    private final Level level;
    private final ContainerData data;

    public ProjectorMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2)); // 从数据包中读取 BlockPos 和 ContainerData
    }

    public ProjectorMenu(int pContainerId, Inventory playerInventory, BlockEntity entity, ContainerData data) {
        super(MillenniumMenuTypes.PROJECTOR_MENU.get(), pContainerId); // 替换为你的 MenuType 注册
        //checkBlockEntityType(entity, MillenniumBlockEntities.PROJECTOR_BE.get()); // 替换为你的 BlockEntityType 注册
        blockEntity = (ProjectorBE) entity;
        this.level = playerInventory.player.level();
        this.data = data;
        addDataSlots(data); // 添加 ContainerData 的数据槽

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        /*
        this.blockEntity.getItemHandler().(itemHandler -> {
            for (int row = 0; row < 3; ++row) {
                for (int col = 0; col < 9; ++col) {
                    ///this.addSlot(new SlotItemHandler(itemHandler, col + row * 9, 8 + col * 18, 18 + row * 18)); // 箱子物品槽位置
                }
            }
        });*/
    }


    //  ContainerData 数据槽索引，与 ProjectorBLEntity 中的 data 对应
    public int getProgressScaled() {
        int progress = this.data.get(0); // 进度值索引为 0
        int maxProgress = this.data.get(1); // 最大进度值索引为 1
        int progressArrowSize = 26; // 进度条长度 (像素)

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }


    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < ProjectorBE.SLOT_COUNT) { // 从箱子槽位移动到玩家物品栏
                if (!this.moveItemStackTo(itemstack1, ProjectorBE.SLOT_COUNT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, ProjectorBE.SLOT_COUNT, false)) { // 从玩家物品栏移动到箱子槽位
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), playerIn, MillenniumBlocks.PROJECTOR_BL.get()); // 替换为你的 Block 注册
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18)); // 玩家主物品栏槽位位置
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142)); // 玩家热键栏槽位位置
        }
    }

    public int getEnergyLevel() {
        return this.data.get(0); // 能量值索引为 0 (示例，实际能量值可能不通过 ContainerData 同步)
    }
}