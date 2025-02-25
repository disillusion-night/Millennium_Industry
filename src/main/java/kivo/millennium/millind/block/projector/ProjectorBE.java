package kivo.millennium.millind.block.projector;


import kivo.millennium.millind.init.MillenniumBlockEntities;
import kivo.millennium.millind.init.MillenniumMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProjectorBE extends BlockEntity implements MenuProvider {
    public static final int SLOT_COUNT = 27;

    /**
     * todo:提供配置文件支持
     */
    public static final int ENERGY_CAPACITY = 2000000; // 2M FE
    public static final int ENERGY_MAX_RECEIVE = 2048;
    public static final int ENERGY_USAGE_PER_TICK = 20; // 假设每 Tick 消耗 20FE

    private final ItemStackHandler itemHandler = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);

    private final EnergyStorage energyStorage = new EnergyStorage(ENERGY_CAPACITY, ENERGY_MAX_RECEIVE, 0, 0) {

    };
    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.of(() -> energyStorage);

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;

    public ProjectorBE(BlockPos pPos, BlockState pBlockState) {
        super(MillenniumBlockEntities.PROJECTOR_BE.get(), pPos, pBlockState);
        this.data = new SimpleContainerData(2);
    }

    public InteractionResult handleRightClickEvent(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit){
        player.openMenu(state.getMenuProvider(level, pos));

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyStorage.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.put("energy", energyStorage.serializeNBT());
        pTag.putInt("projector.progress", this.progress);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        energyStorage.deserializeNBT(pTag.getCompound("energy"));
        this.progress = pTag.getInt("projector.progress");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
        lazyItemHandler.invalidate();
        lazyEnergyStorage.invalidate();
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, ProjectorBE pBlockEntity) {
        if(pLevel.isClientSide()) {
            return;
        }

        // 能量接收逻辑
        if (pBlockEntity.energyStorage.getEnergyStored() < pBlockEntity.energyStorage.getMaxEnergyStored()) {
            for (Direction direction : Direction.values()) {
                if (direction == Direction.UP || direction == Direction.DOWN) continue;
                BlockEntity neighborEntity = pLevel.getBlockEntity(pPos.relative(direction));
                if (neighborEntity != null) {
                    LazyOptional<IEnergyStorage> neighborEnergyStorage = neighborEntity.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite());
                    neighborEnergyStorage.ifPresent(storage -> {
                        if (storage.canExtract()) {
                            int energyRequest = Math.min(pBlockEntity.energyStorage.getMaxEnergyStored() - pBlockEntity.energyStorage.getEnergyStored(), ENERGY_MAX_RECEIVE);
                            int receivedEnergy = storage.extractEnergy(energyRequest, false);
                            pBlockEntity.energyStorage.receiveEnergy(receivedEnergy, false);
                        }
                    });
                }
            }
        }


        // 方块实体 Tick 逻辑 (消耗能量，执行功能)
        if(hasEnoughPowerToRun(pBlockEntity)) {
            pBlockEntity.progress++;
            pBlockEntity.energyStorage.extractEnergy(ENERGY_USAGE_PER_TICK, false);
            setChanged(pLevel, pPos, pState);

            if(pBlockEntity.progress >= pBlockEntity.maxProgress) {
                pBlockEntity.progress = 0;
                //  TODO: 在这里执行方块实体的主要功能，例如物品处理，投影等等
                //  示例：  假设投影功能每次消耗 1000 FE
                if (pBlockEntity.consumeEnergy(1000)) { // 尝试消耗 1000 FE 执行投影功能
                    //  TODO:  执行投影功能代码
                    System.out.println("Projector Function Executed! (Consumed 1000 FE)");
                } else {
                    System.out.println("Not enough energy to execute Projector Function.");
                }

                setChanged(pLevel, pPos, pState);
            }
        } else {
            pBlockEntity.progress = 0;
            setChanged(pLevel, pPos, pState);
        }
    }

    private static boolean hasEnoughPowerToRun(ProjectorBE entity) {
        return entity.energyStorage.getEnergyStored() >= ENERGY_USAGE_PER_TICK;
    }

    //  新增： 消耗能量函数
    public boolean consumeEnergy(int energyToConsume) {
        if (this.energyStorage.getEnergyStored() >= energyToConsume) {
            this.energyStorage.extractEnergy(energyToConsume, false);
            setChanged(); // 标记方块实体数据已更改
            return true; // 能量消耗成功
        } else {
            return false; // 能量不足，消耗失败
        }
    }


    //@Override
    public Component getDisplayName() {
        return Component.translatable("block.millind.projector");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ProjectorMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (level.isClientSide()) {
            CompoundTag tag = pkt.getTag();
            if (tag != null) {
                load(tag);
            }
        }
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public ContainerData getData() {
        return data;
    }
}