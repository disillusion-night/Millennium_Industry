package kivo.millennium.millind.block.device;

import kivo.millennium.millind.Main;
import kivo.millennium.millind.capability.DeviceEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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

    public DeviceItemStorage itemHandler;
    private LazyOptional<ItemStackHandler> lazyItemHandler = LazyOptional.empty();

    protected DeviceEnergyStorage energyStorage;
    private LazyOptional<IEnergyStorage> lazyEnergyStorage;

    public AbstractDeviceBE(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState, int slotCount) {
        super(pType, pWorldPosition, pBlockState);
        this.itemHandler = slotCount > 0?createItemHandler(slotCount):null;
        this.energyStorage = createEnergyStorage();
        this.lazyItemHandler = slotCount > 0?LazyOptional.of(() -> itemHandler):LazyOptional.empty();
        this.lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
    }

    protected DeviceItemStorage createItemHandler(int slot_count) {
        return new DeviceItemStorage(slot_count) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                onContentChange(slot);
            }
        };
    }

    protected void onContentChange(int slot){

    }

    // 创建能量存储处理器，子类可以覆写以自定义容量和传输速率
    protected DeviceEnergyStorage createEnergyStorage() {
        return new DeviceEnergyStorage(100000, MAX_TRANSFER_RATE);
    }

    // 每 tick 执行的逻辑，由 AbstractDeviceBL 的 Ticker 调用
    public static <T extends BlockEntity> void tick(Level pLevel, BlockPos pPos, BlockState pState, AbstractDeviceBE pBlockEntity) {
        if (pLevel.isClientSide()) {
            return; // 客户端不做逻辑处理
        }

        pBlockEntity.tickServer(); // 调用服务端的 tick 逻辑
    }

    // 服务端每 tick 执行的逻辑，子类可以覆写以实现自己的tick逻辑
    protected void tickServer() {
        // 默认的 tick 逻辑，子类可以覆写
    }


    // 从物品槽位中掉落物品
    public void drops() {
        if (this.level != null) {
            this.itemHandler.drops(level, worldPosition);
        }
    }


    // NBT 数据读写
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        saveData(pTag);
    }

    protected void saveData(CompoundTag pTag){
        pTag.put("inventory", itemHandler.serializeNBT()); // 保存物品槽位数据
        pTag.putInt("energy", energyStorage.getEnergyStored()); // 保存能量数据
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        loadData(pTag);
    }


    public void loadData(CompoundTag pTag) {
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
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (pkt.getTag() != null) {
            load(pkt.getTag());
        }
    }


    // Capability 处理
    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }
        return super.getCapability(cap, side);
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

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public DeviceEnergyStorage getEnergyStorage() {
        return energyStorage;
    }
}