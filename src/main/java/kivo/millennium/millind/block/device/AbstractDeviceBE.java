package kivo.millennium.millind.block.device;

import kivo.millennium.millind.Main;
import kivo.millennium.millind.capability.DeviceEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;

public abstract class AbstractDeviceBE extends BlockEntity {

    protected int MAX_TRANSFER_RATE = 1024; // FE/tick

    // 物品槽位处理器
    public ItemStackHandler itemHandler;
    private LazyOptional<ItemStackHandler> lazyItemHandler = LazyOptional.empty();

    // 能量存储处理器
    protected DeviceEnergyStorage energyStorage;
    private LazyOptional<IEnergyStorage> lazyEnergyStorage;

    public AbstractDeviceBE(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState, int slotCount) {
        super(pType, pWorldPosition, pBlockState);
        //this
        this.itemHandler = createItemHandler(slotCount);
        this.energyStorage = createEnergyStorage(); // 创建能量存储处理器
        this.lazyItemHandler = LazyOptional.of(() -> itemHandler); // 初始化物品槽位 Capability 的 LazyOptional
        this.lazyEnergyStorage = LazyOptional.of(() -> energyStorage); // 初始化能量存储 Capability 的 LazyOptional
    }


    // 创建物品槽位处理器，子类可以覆写以自定义槽位数量
    protected ItemStackHandler createItemHandler(int slot_count) {
        return new ItemStackHandler(slot_count) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
            /*
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return AbstractDeviceBE.this.isItemValid(slot, stack); // 调用 isItemValid 方法进行物品验证
            }*/
        };
    }

    // 创建能量存储处理器，子类可以覆写以自定义容量和传输速率
    protected DeviceEnergyStorage createEnergyStorage() {
        return new DeviceEnergyStorage(100000, MAX_TRANSFER_RATE);
    }

    // 子类可以覆写此方法以定义特定槽位的物品验证逻辑
    protected boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true; // 默认所有物品都有效
    }

    // 每 tick 执行的逻辑，由 AbstractDeviceBL 的 Ticker 调用
    public static <T extends BlockEntity> void tick(net.minecraft.world.level.Level pLevel, BlockPos pPos, BlockState pState, AbstractDeviceBE pBlockEntity) {
        if (pLevel.isClientSide()) {
            return; // 客户端不做逻辑处理
        }

        pBlockEntity.tickServer(); // 调用服务端的 tick 逻辑
    }

    // 服务端每 tick 执行的逻辑，子类可以覆写以实现自己的tick逻辑
    protected void tickServer() {
        // 默认的 tick 逻辑，子类可以覆写
    }

    /*
    // 从物品槽位中掉落物品
    public void drops() {
        super.drops();
        if (this.level != null) {
            Containers.dropContents(this.level, this.worldPosition, this.itemHandler); // 掉落物品槽位中的物品
        }
    }*/


    // NBT 数据读写
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("inventory", itemHandler.serializeNBT()); // 保存物品槽位数据
        pTag.putInt("energy", energyStorage.getEnergyStored()); // 保存能量数据
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("inventory")){
            itemHandler.deserializeNBT(pTag.getCompound("inventory")); // 加载物品槽位数据
        }
        if (pTag.contains("energy")){
            energyStorage.setEnergy(pTag.getInt("energy")); // 加载能量数据
        }
    }

    // 数据包同步（用于GUI更新等）
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this); // 创建更新数据包
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag); // 将数据保存到 NBT
        return tag; // 返回 NBT 数据
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (pkt.getTag() != null) {
            load(pkt.getTag()); // 从数据包中加载数据
        }
    }


    // Capability 处理
    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast(); // 提供物品槽位 Capability
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyStorage.cast(); // 提供能量存储 Capability
        }
        return super.getCapability(cap, side); // 其他 Capability 交给父类处理
    }

    // 在方块实体失效时，使 LazyOptional 失效，防止内存泄漏
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyStorage.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
    }

    // 获取物品槽位处理器
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    // 获取能量存储处理器
    public DeviceEnergyStorage getEnergyStorage() {
        return energyStorage;
    }
        /*
    public int getSlotCount(){
        return slot_count;
    }*/
}