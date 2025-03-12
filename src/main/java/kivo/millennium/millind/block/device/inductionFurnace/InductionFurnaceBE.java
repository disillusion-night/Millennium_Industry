package kivo.millennium.millind.block.device.inductionFurnace;

import kivo.millennium.millind.block.device.AbstractDeviceBE;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class InductionFurnaceBE extends AbstractDeviceBE {
    private static final String ITEMS_TAG = "Inventory";

    public static int SLOT_COUNT = 3;
    public static int INPUT1_SLOT = 1;
    public static int INPUT2_SLOT = 2;
    public static int OUTPUT1_SLOT = 2;
    public static int OUTPUT2_SLOT = 3;
    public static int OUTPUT3_SLOT = 4;
    public static int OUTPUT4_SLOT = 5;

    public InductionFurnaceBE(BlockPos pPos, BlockState pBlockState) {
        super(MillenniumBlockEntities.INDUCTION_FURNACE_BE.get(), pPos, pBlockState);
        //this.SLOT
    }
}
