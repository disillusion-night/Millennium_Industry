package kivo.millennium.milltek.machine.FusionChamber;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Vector2i;

import kivo.millennium.milltek.capability.ExtendedSlot;
import kivo.millennium.milltek.container.Device.AbstractDeviceMenu;
import kivo.millennium.milltek.init.MillenniumBlocks;
import kivo.millennium.milltek.init.MillenniumMenuTypes;

public class FusionChamberMenu extends AbstractDeviceMenu<FusionChamberBE> {
    private static final Vector2i inputpos = new Vector2i(56, 37);
    private static final Vector2i outputpos = new Vector2i(107, 35);
    private int fluidCapacityIn;
    private int fluidAmountIn;
    private int fluidCapacityOut;
    private int fluidAmountOut;
    private int progressAndLit;
    private int fluidIdIn;
    private int fluidIdOut;
    public FusionChamberMenu(int pContainerId, Player player, BlockPos pos) {
        super(MillenniumMenuTypes.FUSION_FURNACE_MENU.get(), pContainerId, player, pos, new SimpleContainer(FusionChamberBE.SLOT_COUNT));
    }

    @Override
    protected void setupSlot(Container container, FusionChamberBE deviceBE) {
        super.setupSlot(container, deviceBE);
        addSlot(new ExtendedSlot(container, deviceBE.getItemHandler(),FusionChamberBE.INPUT_SLOT, inputpos));
    }

    public Vector2i getBatterySlotPos(){
        return new Vector2i(152, 66);
    }

    public Vector2i getPlayerInvPos() {
        return new Vector2i(8, 88);
    }

    @Override
    protected void setupDataSlot(Container container, FusionChamberBE deviceBE) {
        super.setupDataSlot(container, deviceBE);
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return deviceBE.getProgressAndLit() & 0xffff;
            }

            @Override
            public void set(int pValue) {
                FusionChamberMenu.this.progressAndLit = (FusionChamberMenu.this.progressAndLit & 0xffff0000) | (pValue & 0xffff);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return deviceBE.getFluidTank().getFluidAmount(0) & 0xffff;
            }

            @Override
            public void set(int pValue) {
                FusionChamberMenu.this.fluidAmountIn = (FusionChamberMenu.this.fluidAmountIn & 0xffff0000) | (pValue & 0xffff);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (deviceBE.getFluidTank().getFluidAmount(0) >> 16) & 0xffff;
            }

            @Override
            public void set(int pValue) {
                FusionChamberMenu.this.fluidAmountIn = (FusionChamberMenu.this.fluidAmountIn & 0xffff) | ((pValue & 0xffff) << 16);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return deviceBE.getFluidTank().getTankCapacity(1) & 0xffff;
            }

            @Override
            public void set(int pValue) {
                FusionChamberMenu.this.fluidCapacityIn = (FusionChamberMenu.this.fluidCapacityIn & 0xffff0000) | (pValue & 0xffff);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (deviceBE.getFluidTank().getTankCapacity(1) >> 16) & 0xffff;
            }

            @Override
            public void set(int pValue) {
                FusionChamberMenu.this.fluidCapacityIn = (FusionChamberMenu.this.fluidCapacityIn & 0xffff) | ((pValue & 0xffff) << 16);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return deviceBE.getFluidTank().getFluidAmount(1) & 0xffff;
            }

            @Override
            public void set(int pValue) {
                FusionChamberMenu.this.fluidAmountOut = (FusionChamberMenu.this.fluidAmountOut & 0xffff0000) | (pValue & 0xffff);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (deviceBE.getFluidTank().getFluidAmount(1) >> 16) & 0xffff;
            }

            @Override
            public void set(int pValue) {
                FusionChamberMenu.this.fluidAmountOut = (FusionChamberMenu.this.fluidAmountOut & 0xffff) | ((pValue & 0xffff) << 16);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return deviceBE.getFluidTank().getTankCapacity(1) & 0xffff;
            }

            @Override
            public void set(int pValue) {
                FusionChamberMenu.this.fluidCapacityOut = (FusionChamberMenu.this.fluidCapacityOut & 0xffff0000) | (pValue & 0xffff);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (deviceBE.getFluidTank().getTankCapacity(1) >> 16) & 0xffff;
            }

            @Override
            public void set(int pValue) {
                FusionChamberMenu.this.fluidCapacityOut = (FusionChamberMenu.this.fluidCapacityOut & 0xffff) | ((pValue & 0xffff) << 16);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return BuiltInRegistries.FLUID.getId(deviceBE.getFluidTank().getFluidInTank(0).getFluid());
            }

            @Override
            public void set(int pValue) {
                FusionChamberMenu.this.fluidIdIn = pValue;
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return BuiltInRegistries.FLUID.getId(deviceBE.getFluidTank().getFluidInTank(1).getFluid());
            }

            @Override
            public void set(int pValue) {
                FusionChamberMenu.this.fluidIdOut = pValue;
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
        return MillenniumBlocks.FUSION_CHAMBER_BL.get();
    }
}