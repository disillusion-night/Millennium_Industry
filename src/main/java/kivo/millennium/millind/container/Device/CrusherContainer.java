package kivo.millennium.millind.container.Device;

import kivo.millennium.millind.block.device.crusher.CrusherBE;
import kivo.millennium.millind.init.MillenniumBlocks;
import kivo.millennium.millind.init.MillenniumMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.SlotItemHandler;
import org.joml.Vector2i;

public class CrusherContainer extends AbstractDeviceMT {


    public CrusherContainer(int pContainerId, Player player, BlockPos pos) {
        super(MillenniumMenuTypes.CRUSHER_CONTAINER.get(), pContainerId, player, pos);
    }

    @Override
    protected void setupSlot() {
        if (itemHandler == null) return; // 安全检查

        // 添加电池槽位 (槽位索引 0, 由 AbstractDeviceMT 默认添加)
        // 电池槽位位置在 AbstractDeviceMT 中已定义 (BATTERY_SLOT_POS)

        // 添加输入槽位 (槽位索引 1,  CrusherBlockBE.CRUSHER_INPUT_SLOT)
        this.addSlot(new SlotItemHandler(itemHandler, CrusherBE.getCrusherInputSlot(), 56, 17)); //  GUI 位置 (56, 17) 可以调整

        // 添加输出槽位 (槽位索引 2, CrusherBlockBE.CRUSHER_OUTPUT_SLOT)
        this.addSlot(new SlotItemHandler(itemHandler, CrusherBE.getCrusherOutputSlot(), 116, 35)); // GUI 位置 (116, 35) 可以调整

        // 添加玩家物品栏槽位
        //addPlayerInventorySlots(playerInventory);

        // 添加玩家快捷栏槽位
        //addPlayerHotbarSlots(playerInventory);
    }

    @Override
    protected Vector2i getPlayerInvPos() {
        return new Vector2i(10, 70);
    }

    @Override
    protected Block getBlock() {
        return MillenniumBlocks.CRUSHER_BL.get(); // 返回破碎机方块实例  (请确保你已注册 ModBlocks.CRUSHER_BLOCK)
    }
}