/*
package kivo.millennium.millind.block.arcfurnace;

import kivo.millennium.millind.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class ArcFurnaceBE extends BlockEntity implements MenuProvider {
    public static final int ENERGY_CAPACITY = 10000; // 电量容量
    public static final int ENERGY_USAGE_PER_TICK = 10; // 每刻消耗电量
    public static final int MAX_PROGRESS = 100; //  烧炼进度条最大值 (用于GUI显示)
    private final EnergyStorage energyStorage = createEnergyStorage();
    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
    protected final ContainerData data;
    private int progress = 0; // 烧炼进度
    private int energyLevel = 0; // 电量等级 (用于GUI显示)
    private int maxEnergyLevel = 10; // 最大电量等级 (用于GUI显示)
    LazyOptional<SimpleContainer> itemHandler = LazyOptional.of(() -> new SimpleContainer(2) { // 输入槽和输出槽
        @Override
        public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
            return true;
        }

        @Override
        public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
            return true;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    });


    public ArcFurnaceBE(BlockPos pPos, BlockState pBlockState) {
        super(MillenniumBlockEntities.ARC_FURNACE_BE.get(), pPos, pBlockState);
        this.data = new SimpleContainerData(4); //  使用 ContainerData 传递能量等级和进度信息 (调整大小为4)
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.millind.arc_furnace"); //  本地化键
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ArcFurnaceMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    public @Nonnull <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandler.cast();
        }
        if(cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyEnergyStorage.invalidate();
        itemHandler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
        itemHandler = LazyOptional.of(() -> new SimpleContainer(2) {
            @Override
            public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
                return true;
            }

            @Override
            public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
                return true;
            }

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        });
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt("arc_furnace.progress", progress);
        pTag.putInt("arc_furnace.energy", energyStorage.getEnergyStored()); // 保存电量
        CompoundTag inventoryTag = itemHandler.orElseThrow(RuntimeException::new).save(new CompoundTag());
        pTag.put("inventory", inventoryTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        progress = pTag.getInt("arc_furnace.progress");
        energyStorage.setEnergy(pTag.getInt("arc_furnace.energy")); // 加载电量
        CompoundTag inventoryTag = pTag.getCompound("inventory");
        itemHandler.orElseThrow(RuntimeException::new).load(inventoryTag);
    }

    public void drops() {
        SimpleContainer inventory = itemHandler.orElse(new SimpleContainer(2)); //  使用orElse提供默认值
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, ArcFurnaceBE pBlockEntity) {
        if(hasEnergy(pBlockEntity)) {
            if(hasRecipe(pBlockEntity)) {
                pBlockEntity.progress++;
                setChanged(pLevel, pPos, pState);
                if(pBlockEntity.progress >= MAX_PROGRESS) { //  烧炼速度是两倍，所以 MAX_PROGRESS 可以设置为熔炉的一半
                    craftItem(pBlockEntity);
                    pBlockEntity.progress = 0;
                }
            } else {
                pBlockEntity.resetProgress();
                setChanged(pLevel, pPos, pState);
            }
            pBlockEntity.energyStorage.extractEnergy(ENERGY_USAGE_PER_TICK, false); // 每刻消耗电量
            setEnergyLevelData(pBlockEntity); //  更新能量等级数据
        } else {
            pBlockEntity.resetProgress();
            setChanged(pLevel, pPos, pState);
        }
    }

    private static void setEnergyLevelData(ArcFurnaceBE pBlockEntity) {
        double ratio = (double) pBlockEntity.energyStorage.getEnergyStored() / ArcFurnaceBE.ENERGY_CAPACITY;
        pBlockEntity.energyLevel = (int) (ratio * pBlockEntity.maxEnergyLevel); //  计算能量等级
        pBlockEntity.data.set(2, pBlockEntity.energyLevel); //  设置能量等级数据槽 (slot 2)
        pBlockEntity.data.set(3, pBlockEntity.maxEnergyLevel); //  设置最大能量等级数据槽 (slot 3)

    }

    private void resetProgress() {
        this.progress = 0;
    }

    private static void craftItem(ArcFurnaceBE entity) {
        Level level = entity.level;
        SimpleContainer inventory = entity.itemHandler.orElse(new SimpleContainer(2)); //  使用orElse提供默认值
        Optional<ArcFurnaceRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(ModRecipes.ARC_FURNACE_RECIPE.get(), inventory, level);

        if(recipe.isPresent()) {
            ItemStack outputStack = recipe.get().getResultItem(level.registryAccess());
            entity.itemHandler.ifPresent(handler -> {
                handler.extractItem(0, 1, false); // 取出输入物品
                handler.setChanged();
                if(handler.getStackInSlot(1).isEmpty()) { // 如果输出槽为空，直接放入
                    handler.setStackInSlot(1, outputStack);
                } else { // 否则，叠加到已有的物品上
                    handler.getStackInSlot(1).grow(outputStack.getCount());
                }
            });
        }
    }

    private static boolean hasRecipe(ArcFurnaceBE entity) {
        Level level = entity.level;
        SimpleContainer inventory = entity.itemHandler.orElse(new SimpleContainer(2)); //  使用orElse提供默认值
        Optional<ArcFurnaceRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(ModRecipes.ARC_FURNACE_RECIPE.get(), inventory, level);

        if(recipe.isPresent()) {
            ItemStack outputStack = recipe.get().getResultItem(level.registryAccess());
            return canInsertItemIntoOutputSlot(inventory, outputStack); // 检查是否可以放入输出槽
        }

        return false;
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack outputStack) {
        ItemStack currentOutputStack = inventory.getItem(1); // 输出槽物品
        if(currentOutputStack.isEmpty()) {
            return true; // 输出槽为空，可以直接放入
        }
        if(!currentOutputStack.is(outputStack.getItem())) {
            return false; //  物品类型不同，不能放入
        }
        return currentOutputStack.getCount() + outputStack.getCount() <= inventory.getMaxStackSize(); //  检查叠加后是否超过最大堆叠大小
    }

    private static boolean hasEnergy(ArcFurnaceBE entity) {
        return entity.energyStorage.getEnergyStored() >= ENERGY_USAGE_PER_TICK; // 检查电量是否足够
    }


    private EnergyStorage createEnergyStorage() {
        return new EnergyStorage(ENERGY_CAPACITY, 256, 256, 0) { // 容量，输入速率，输出速率，初始电量
            @Override
            protected void onEnergyChanged() {
                setChanged(); // 电量变化时标记方块实体需要保存
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL); //  通知客户端更新
            }
        };
    }

    public int getProgress() {
        return progress;
    }

    public int getEnergyLevel() {
        return energyLevel;
    }

    public int getMaxEnergyLevel() {
        return maxEnergyLevel;
    }

    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    public int getEnergyCapacity() {
        return ENERGY_CAPACITY;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (level.isClientSide) {
            load(pkt.getTag());
        }
    }
}*/