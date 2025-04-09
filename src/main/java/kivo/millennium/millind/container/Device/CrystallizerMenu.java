package kivo.millennium.millind.container.Device;

import kivo.millennium.millind.machine.Crystallizer.CrystallizerBE;
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

public class CrystallizerMenu extends AbstractDeviceMenu<CrystallizerBE> {
    private static final Vector2i inputpos = new Vector2i(116, 17);
    private static final Vector2i outputpos = new Vector2i(116, 54);
    private int fluidCapacity;
    private int fluidAmount;
    private int progressAndLit;
    private int fluidId;
    public CrystallizerMenu(int pContainerId, Player player, BlockPos pos) {
        super(MillenniumMenuTypes.CRYSTALLIZER_MENU.get(), pContainerId, player, pos, new SimpleContainer(CrystallizerBE.SLOT_COUNT));
    }

    @Override
    protected void setupSlot(Container container, CrystallizerBE deviceBE) {
        super.setupSlot(container, deviceBE);
        addSlot(new ExtendedSlot(container, deviceBE.getItemHandler(), CrystallizerBE.INPUT_SLOT, inputpos));
        addSlot(new ExtendedSlot(container, deviceBE.getItemHandler(), CrystallizerBE.OUTPUT_SLOT, outputpos));
    }


    @Override
    protected void setupDataSlot(Container container, CrystallizerBE deviceBE) {
        super.setupDataSlot(container, deviceBE);
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return deviceBE.getProgressAndLit() & 0xffff;
            }

            @Override
            public void set(int pValue) {
                CrystallizerMenu.this.progressAndLit = (CrystallizerMenu.this.progressAndLit & 0xffff0000) | (pValue & 0xffff);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return deviceBE.getFluidTank().getFluidAmount(0) & 0xffff;
            }

            @Override
            public void set(int pValue) {
                CrystallizerMenu.this.fluidAmount= (CrystallizerMenu.this.fluidAmount & 0xffff0000) | (pValue & 0xffff);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (deviceBE.getFluidTank().getFluidAmount(0) >> 16) & 0xffff;
            }

            @Override
            public void set(int pValue) {
                CrystallizerMenu.this.fluidAmount = (CrystallizerMenu.this.fluidAmount & 0xffff) | ((pValue & 0xffff) << 16);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return deviceBE.getFluidTank().getTankCapacity(0) & 0xffff;
            }

            @Override
            public void set(int pValue) {
                CrystallizerMenu.this.fluidCapacity = (CrystallizerMenu.this.fluidCapacity & 0xffff0000) | (pValue & 0xffff);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (deviceBE.getFluidTank().getTankCapacity(0) >> 16) & 0xffff;
            }

            @Override
            public void set(int pValue) {
                CrystallizerMenu.this.fluidCapacity = (CrystallizerMenu.this.fluidCapacity & 0xffff) | ((pValue & 0xffff) << 16);
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return BuiltInRegistries.FLUID.getId(deviceBE.getFluidTank().getFluidInTank(0).getFluid());
            }

            @Override
            public void set(int pValue) {
                CrystallizerMenu.this.fluidId = pValue;
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
        return MillenniumBlocks.CRYSTALLIZER_BL.get();
    }
}