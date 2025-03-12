package kivo.millennium.millind.block.device.inductionFurnace;

import kivo.millennium.millind.block.device.AbstractDeviceBE;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

public class InductionFurnaceBE extends AbstractDeviceBE {
    public static int SLOT_COUNT = 7;
    public static int INPUT1_SLOT = 1;
    public static int INPUT2_SLOT = 2;
    public static int OUTPUT1_SLOT = 3;
    public static int OUTPUT2_SLOT = 4;
    public static int OUTPUT3_SLOT = 5;
    public static int OUTPUT4_SLOT = 6;

    public InductionFurnaceBE(BlockPos pPos, BlockState pBlockState) {
        super(MillenniumBlockEntities.INDUCTION_FURNACE_BE.get(), pPos, pBlockState, SLOT_COUNT);
    }

    /*
    @Override
    protected ItemStackHandler createItemHandler() {
        return super.createItemHandler();
    }*/
}
