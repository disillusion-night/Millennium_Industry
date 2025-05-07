package kivo.millennium.milltek.container.Device;

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
import kivo.millennium.milltek.init.MillenniumBlocks;
import kivo.millennium.milltek.init.MillenniumMenuTypes;
import kivo.millennium.milltek.machine.MeltingFurnace.MeltingFurnaceBE;

public class MeltingFurnaceMenu extends AbstractDeviceMenu<MeltingFurnaceBE> {
    private static final Vector2i inputpos = new Vector2i(53, 37);
    private static final Vector2i outputpos = new Vector2i(107, 35);
    private int fluidCapacity;
    private int fluidAmount;
    private int progressAndLit;
    private int fluidId;
    public MeltingFurnaceMenu(int pContainerId, Player player, BlockPos pos) {
        super(MillenniumMenuTypes.MELTING_FURNACE_MENU.get(), pContainerId, player, pos, new SimpleContainer(MeltingFurnaceBE.SLOT_COUNT));
    }

    @Override
    protected void setupSlot(Container container, MeltingFurnaceBE deviceBE) {
        super.setupSlot(container, deviceBE);
        addSlot(new ExtendedSlot(container, deviceBE.getItemHandler(), MeltingFurnaceBE.INPUT_SLOT, inputpos));
    }


    @Override
    protected void setupDataSlot(Container container, MeltingFurnaceBE deviceBE) {
        super.setupDataSlot(container, deviceBE);
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return deviceBE.getProgressAndLit() & 0xffff;
            }

            @Override
            public void set(int pValue) {
                MeltingFurnaceMenu.this.progressAndLit = (MeltingFurnaceMenu.this.progressAndLit & 0xffff0000) | (pValue & 0xffff);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return deviceBE.getFluidTank().getFluidAmount(0) & 0xffff;
            }

            @Override
            public void set(int pValue) {
                MeltingFurnaceMenu.this.fluidAmount= (MeltingFurnaceMenu.this.fluidAmount & 0xffff0000) | (pValue & 0xffff);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (deviceBE.getFluidTank().getFluidAmount(0) >> 16) & 0xffff;
            }

            @Override
            public void set(int pValue) {
                MeltingFurnaceMenu.this.fluidAmount = (MeltingFurnaceMenu.this.fluidAmount & 0xffff) | ((pValue & 0xffff) << 16);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return deviceBE.getFluidTank().getTankCapacity(0) & 0xffff;
            }

            @Override
            public void set(int pValue) {
                MeltingFurnaceMenu.this.fluidCapacity = (MeltingFurnaceMenu.this.fluidCapacity & 0xffff0000) | (pValue & 0xffff);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (deviceBE.getFluidTank().getTankCapacity(0) >> 16) & 0xffff;
            }

            @Override
            public void set(int pValue) {
                MeltingFurnaceMenu.this.fluidCapacity = (MeltingFurnaceMenu.this.fluidCapacity & 0xffff) | ((pValue & 0xffff) << 16);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                //MeltingFurnaceMenu.this.fluidId =
                return BuiltInRegistries.FLUID.getId(deviceBE.getFluidTank().getFluidInTank(0).getFluid());
            }

            @Override
            public void set(int pValue) {
                MeltingFurnaceMenu.this.fluidId = pValue;
            }
        });
    }

    public int getProgress(){
        return progressAndLit >> 1;
    }

    public boolean getLit(){
        return (progressAndLit & 1) > 0;
    }

    public int getFluidAmount(){
        return fluidAmount;
    }

    public int getFluidCapacity(){
        return fluidCapacity;
    }

    public int getFluidId(){
        return fluidId;
    }

    public FluidStack getFluid() {
        int amount = this.getFluidAmount();
        int fluidId = this.getFluidId();
        if (fluidId == -1 || amount <= 0) {
            return FluidStack.EMPTY;
        }
        return new FluidStack(BuiltInRegistries.FLUID.byId(fluidId), amount);
    }

    @Override
    protected Block getBlock() {
        return MillenniumBlocks.MELTING_FURNACE_BL.get();
    }
}