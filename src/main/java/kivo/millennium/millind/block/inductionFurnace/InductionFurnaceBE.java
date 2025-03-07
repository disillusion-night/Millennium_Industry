package kivo.millennium.millind.block.inductionFurnace;

import kivo.millennium.millind.block.AbstractDeviceBE;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class InductionFurnaceBE extends AbstractDeviceBE {
    private static final String ITEMS_TAG = "Inventory";

    public static int SLOT_COUNT = 6;
    public static int INPUT1_SLOT = 0;
    public static int INPUT2_SLOT = 1;
    public static int OUTPUT1_SLOT = 2;
    public static int OUTPUT2_SLOT = 3;
    public static int OUTPUT3_SLOT = 4;
    public static int OUTPUT4_SLOT = 5;

    private final ItemStackHandler items = createItemHandler();
    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> items);

    public InductionFurnaceBE(BlockPos pPos, BlockState pBlockState) {
        super(MillenniumBlockEntities.INDUCTION_FURNACE_BE.get(), pPos, pBlockState);
    }


    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(ITEMS_TAG, items.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(ITEMS_TAG)) {
            items.deserializeNBT(tag.getCompound(ITEMS_TAG));
        }
    }

    @Nonnull
    private ItemStackHandler createItemHandler() {
        return new ItemStackHandler(SLOT_COUNT) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }
}
