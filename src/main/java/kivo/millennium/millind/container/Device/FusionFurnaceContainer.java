package kivo.millennium.millind.container.Device;

import kivo.millennium.millind.block.device.AbstractMachineBE;
import kivo.millennium.millind.block.device.FusionFurnace.FusionFurnaceBE;
import kivo.millennium.millind.block.device.MeltingFurnace.MeltingFurnaceBE;
import kivo.millennium.millind.capability.ExtendedSlot;
import kivo.millennium.millind.init.MillenniumBlocks;
import kivo.millennium.millind.init.MillenniumMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Vector2i;

public class FusionFurnaceContainer extends AbstractDeviceMenu {
    private static final Vector2i inputpos = new Vector2i(53, 37);
    private static final Vector2i outputpos = new Vector2i(107, 35);
    private int fluidCapacityIn;
    private int fluidAmountIn;
    private int fluidCapacityOut;
    private int fluidAmountOut;
    private int progressAndLit;
    private int fluidIdIn;
    private int fluidIdOut;
    public FusionFurnaceContainer(int pContainerId, Player player, BlockPos pos) {
        super(MillenniumMenuTypes.FUSION_FURNACE_MENU.get(), pContainerId, player, pos, new SimpleContainer(FusionFurnaceBE.SLOT_COUNT));
    }

    @Override
    protected void setupSlot(Container container, AbstractMachineBE deviceBE) {
        super.setupSlot(container, deviceBE);
        addSlot(new ExtendedSlot(container, deviceBE.getItemHandler(), FusionFurnaceBE.INPUT_SLOT, inputpos));
    }


    @Override
    protected void setupDataSlot(Container container, AbstractMachineBE deviceBE) {
        super.setupDataSlot(container, deviceBE);
        FusionFurnaceBE be = (FusionFurnaceBE) deviceBE;
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return be.getProgressAndLit() & 0xffff;
            }

            @Override
            public void set(int pValue) {
                FusionFurnaceContainer.this.progressAndLit = (FusionFurnaceContainer.this.progressAndLit & 0xffff0000) | (pValue & 0xffff);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return be.getFluidTank().getFluidAmountIn() & 0xffff;
            }

            @Override
            public void set(int pValue) {
                FusionFurnaceContainer.this.fluidAmountIn = (FusionFurnaceContainer.this.fluidAmountIn & 0xffff0000) | (pValue & 0xffff);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (be.getFluidTank().getFluidAmountIn() >> 16) & 0xffff;
            }

            @Override
            public void set(int pValue) {
                FusionFurnaceContainer.this.fluidAmountIn = (FusionFurnaceContainer.this.fluidAmountIn & 0xffff) | ((pValue & 0xffff) << 16);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return be.getFluidTank().getCapacityIn() & 0xffff;
            }

            @Override
            public void set(int pValue) {
                FusionFurnaceContainer.this.fluidCapacityIn = (FusionFurnaceContainer.this.fluidCapacityIn & 0xffff0000) | (pValue & 0xffff);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (be.getFluidTank().getCapacityIn() >> 16) & 0xffff;
            }

            @Override
            public void set(int pValue) {
                FusionFurnaceContainer.this.fluidCapacityIn = (FusionFurnaceContainer.this.fluidCapacityIn & 0xffff) | ((pValue & 0xffff) << 16);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return be.getFluidTank().getFluidAmountOut() & 0xffff;
            }

            @Override
            public void set(int pValue) {
                FusionFurnaceContainer.this.fluidAmountOut = (FusionFurnaceContainer.this.fluidAmountOut & 0xffff0000) | (pValue & 0xffff);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (be.getFluidTank().getFluidAmountOut() >> 16) & 0xffff;
            }

            @Override
            public void set(int pValue) {
                FusionFurnaceContainer.this.fluidAmountOut = (FusionFurnaceContainer.this.fluidAmountOut & 0xffff) | ((pValue & 0xffff) << 16);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return be.getFluidTank().getCapacityOut() & 0xffff;
            }

            @Override
            public void set(int pValue) {
                FusionFurnaceContainer.this.fluidCapacityOut = (FusionFurnaceContainer.this.fluidCapacityOut & 0xffff0000) | (pValue & 0xffff);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (be.getFluidTank().getCapacityOut() >> 16) & 0xffff;
            }

            @Override
            public void set(int pValue) {
                FusionFurnaceContainer.this.fluidCapacityOut = (FusionFurnaceContainer.this.fluidCapacityOut & 0xffff) | ((pValue & 0xffff) << 16);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return BuiltInRegistries.FLUID.getId(be.getFluidTank().getFluidIn().getFluid());
            }

            @Override
            public void set(int pValue) {
                FusionFurnaceContainer.this.fluidIdIn = pValue;
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return BuiltInRegistries.FLUID.getId(be.getFluidTank().getFluidOut().getFluid());
            }

            @Override
            public void set(int pValue) {
                FusionFurnaceContainer.this.fluidIdOut = pValue;
            }
        });
    }

    public int getProgress(){
        return progressAndLit >> 1;
    }

    public boolean getLit(){
        return (progressAndLit & 1) > 0;
    }

    public int getFluidAmountIn(){
        return fluidAmountIn;
    }

    public int getFluidCapacityIn(){
        return fluidCapacityIn;
    }

    public int getFluidAmountOut(){
        return fluidAmountOut;
    }

    public int getFluidCapacityOut(){
        return fluidCapacityOut;
    }

    public int getFluidIdIn(){
        return fluidIdIn;
    }

    public int getFluidIdOut(){
        return fluidIdOut;
    }

    public FluidStack getFluidIn() {
        int amount = this.getFluidAmountIn();
        int fluidId = this.getFluidIdIn();
        if (fluidId == -1 || amount <= 0) {
            return FluidStack.EMPTY;
        }
        return new FluidStack(BuiltInRegistries.FLUID.byId(fluidId), amount);
    }

    public FluidStack getFluidOut() {
        int amount = this.getFluidAmountOut();
        int fluidId = this.getFluidIdOut();
        if (fluidId == -1 || amount <= 0) {
            return FluidStack.EMPTY;
        }
        return new FluidStack(BuiltInRegistries.FLUID.byId(fluidId), amount);
    }

    @Override
    protected Block getBlock() {
        return MillenniumBlocks.FUSION_FURNACE_BL.get();
    }
}