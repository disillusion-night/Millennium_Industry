package kivo.millennium.millind.block.device;

import kivo.millennium.millind.block.IWorkingMachine;
import kivo.millennium.millind.capability.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;

public abstract class AbstractMachineBE extends BlockEntity implements IWorkingMachine {
    public CapabilityCache cache;

    public<T extends AbstractMachineBE> AbstractMachineBE(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState, CapabilityCache.Builder builder) {
        super(pType, pWorldPosition, pBlockState);
        this.cache = builder.build(this::setCapabilityChanged);
    }

    // 每 tick 执行的逻辑，由 AbstractDeviceBL 的 Ticker 调用
    public static <T extends BlockEntity> void tick(Level pLevel, BlockPos pPos, BlockState pState, AbstractMachineBE pBlockEntity) {
        if (pLevel.isClientSide()) {
            return; // 客户端不做逻辑处理
        }

        pBlockEntity.tickServer(); // 调用服务端的 tick 逻辑
    }

    protected void tickServer() {
    }


    // 从物品槽位中掉落物品
    public void drops() {
        if (this.level != null && getItemHandler() != null) {
            getItemHandler().drops(level, worldPosition);
        }
    }


    // NBT 数据读写
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        this.cache.writeToNBT(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.cache.readFromNBT(pTag);
    }

    public abstract boolean isWorking();

    protected void setCapabilityChanged(CapabilityType type){
        setChanged();
    }

    // 数据包同步
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this); // 创建更新数据包
    }

    @Override
    public CompoundTag getUpdateTag(){
        CompoundTag tag = super.getUpdateTag();
        cache.writeToNBT(tag);
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
        LazyOptional a = this.cache.getCapability(cap, side);
        if(a != LazyOptional.empty()){
            return a;
        }
        return super.getCapability(cap, side);
    }


    // 在方块实体失效时，使 LazyOptional 失效，防止内存泄漏
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        cache.inValidCaps();
    }

    @Override
    public void handleUpdateTag(CompoundTag compoundTag){
        super.handleUpdateTag(compoundTag);
        cache.readFromNBT(compoundTag);
    }


    @Override
    public void reviveCaps() {
        super.reviveCaps();
    }

    public MillenniumFluidStorage getFluidHandler() {
        return cache.getFluidCapability();
    }

    public MillenniumItemStorage getItemHandler() {
        return cache.getItemCapability();
    }

    public MillenniumEnergyStorage getEnergyStorage() {
        return cache.getEnergyCapability();
    }
}