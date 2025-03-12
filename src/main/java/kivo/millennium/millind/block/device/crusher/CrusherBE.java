package kivo.millennium.millind.block.device.crusher;

import kivo.millennium.millind.block.device.AbstractDeviceBE;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemStackHandler;

public class CrusherBE extends AbstractDeviceBE {

    // 定义破碎机特定的槽位数量
    private static final int CRUSHER_INPUT_SLOT = 1; // 输入槽位索引 (用于 CrusherContainer 中槽位定义)
    private static final int CRUSHER_OUTPUT_SLOT = 2; // 输出槽位索引

    public CrusherBE(BlockPos pPos, BlockState pBlockState) {
        super(MillenniumBlockEntities.Crusher_BE.get(), pPos, pBlockState); // 调用 AbstractDeviceBE 的构造函数，并传入破碎机方块实体类型
        this.energyStorage = createEnergyStorage(); // 创建能量存储，可以在这里或 createEnergyStorage 方法中自定义容量和速率
        this.itemHandler = createItemHandler();   // 创建物品槽位处理器，可以在这里或 createItemHandler 方法中自定义槽位数量
        this.SLOT_COUNT = 3; // 1个电池槽位 (AbstractDeviceMT 默认) + 1个输入槽位 + 1个输出槽位
        this.MAX_TRANSFER_RATE = 64; // 设置破碎机最大能量传输速率为 64FE/tick (可以覆写父类的默认值)
    }


    @Override
    protected ItemStackHandler createItemHandler() {
        return new ItemStackHandler(SLOT_COUNT) { // 使用 SLOTS_COUNT 定义槽位数量 (3个槽位)
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();/*
                if(!level.isClientSide()) {
                    PacketListener.sendToClients(CrusherBE.this.level, worldPosition);
                }*/
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return switch (slot) {
                    case 0 -> stack.getCapability(ForgeCapabilities.ENERGY).isPresent(); // 槽位 0 (电池槽位): 允许放入可存储能量的物品 (BatterySlot 已经做了限制，这里再次验证)
                    case 1 -> true; // 槽位 1 (输入槽位): 允许放入任何物品 (可以根据实际破碎机逻辑修改)
                    case 2 -> false; // 槽位 2 (输出槽位): 不允许手动放入物品 (输出槽位，由机器自动填充)
                    default -> super.isItemValid(slot, stack); // 其他槽位 (不应该有) 使用默认验证逻辑
                };
            }
        };
    }


    @Override
    protected void tickServer() {
        if(energyStorage.getEnergyStored() > 0) {
            // TODO: 添加破碎机工作逻辑 (例如，从输入槽位取出物品，进行破碎处理，将产物放入输出槽位，消耗能量)
            //  示例: 假设破碎机每 tick 消耗 1 FE 能量
            energyStorage.extractEnergy(1, false);
            setChanged(); // 标记方块实体数据已更改 (能量值变化)
            if(!level.isClientSide()) {
                //PacketListener.sendToClients(CrusherBE.this.level, worldPosition); // 同步能量变化到客户端
            }
        }
    }

    public static int getCrusherInputSlot() {
        return CRUSHER_INPUT_SLOT; // 获取输入槽位索引 (用于 CrusherContainer 中槽位定义)
    }

    public static int getCrusherOutputSlot() {
        return CRUSHER_OUTPUT_SLOT; // 获取输出槽位索引
    }
}