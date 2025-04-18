package kivo.millennium.millind.machine.MolecularReformer;

import kivo.millennium.millind.block.device.AbstractMachineBE;
import kivo.millennium.millind.machine.MeltingFurnace.MeltingFurnaceBL;
import kivo.millennium.millind.capability.CapabilityCache;
import kivo.millennium.millind.capability.CapabilityType;
import kivo.millennium.millind.capability.MillenniumFluidStorage;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import kivo.millennium.millind.recipe.ItemProxy;
import kivo.millennium.millind.recipe.MeltingRecipe;
import kivo.millennium.millind.recipe.NeoContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Optional;

public class MolecularReformerBE extends AbstractMachineBE{
    public static final int SLOT_COUNT = 2;
    public static final int BATTERY_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    private int totalTime = 100;
    private FluidStack currentRecipeOutput = FluidStack.EMPTY;
    private static final int FLUID_CAPACITY = 12000; // 毫升
    private boolean isWorking;


    public MolecularReformerBE(BlockPos pWorldPosition, BlockState pBlockState) {
        super(MillenniumBlockEntities.MOLECULAR_REFORMER_BE.get(), pWorldPosition, pBlockState, new CapabilityCache.Builder()
                .withEnergy(100000, 2000)
                .withFluid(1, FLUID_CAPACITY)
                .withItems(2)
                .withProgress()
        );
    }

    @Override
    public void tickServer() {
        // 1. 从电池槽充电
        ItemStack batteryStack = getItemHandler().getStackInSlot(BATTERY_SLOT);
        batteryStack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {
            if (getEnergyStorage().canReceive()) {
                int received = getEnergyStorage().receiveEnergy(energy.extractEnergy(getEnergyStorage().getMaxEnergyStored() - getEnergyStorage().getEnergyStored(), false), false);
                if (received > 0) {
                    setChanged();
                }
            }
        });

        ItemStack inputStack = getItemHandler().getStackInSlot(INPUT_SLOT);

        if (!inputStack.isEmpty()) {
            Optional<MeltingRecipe> recipe = level.getRecipeManager().getRecipeFor(MeltingRecipe.Type.INSTANCE, new NeoContainer(new ItemProxy(inputStack)), level);

            recipe.ifPresent(meltingRecipe -> {
                FluidStack recipeOutput = meltingRecipe.getOutputs().getFluid(0);
                int recipeMeltingTime = meltingRecipe.getTime();

                if (this.canProcess(recipeOutput)) {
                    if(!isWorking){
                        isWorking = true;
                        level.setBlock(getBlockPos(),getBlockState().setValue(MeltingFurnaceBL.WORKING, true), 3);
                    }
                    this.totalTime = recipeMeltingTime; // 从配方中获取熔融时间
                    this.currentRecipeOutput = recipeOutput;
                    this.setChanged();
                    addProgress();
                    if (getProgress() >= this.totalTime) {
                        resetProgress();
                        this.meltItem(recipeOutput);
                    }
                } else {
                    isWorking = false;
                    level.setBlock(getBlockPos(),getBlockState().setValue(MeltingFurnaceBL.WORKING, false), 3);
                }
            });
        } else {
            isWorking = false;
            resetProgress();
            level.setBlock(getBlockPos(),getBlockState().setValue(MeltingFurnaceBL.WORKING, false), 3);
        }
    }

    private void addProgress(){
        cache.addProgress(1);
    }

    private int getProgress(){
        return cache.getProgress();
    }

    private boolean canProcess(FluidStack recipeOutput) {
        if (this.currentRecipeOutput.isEmpty() || (this.currentRecipeOutput.isFluidEqual(recipeOutput) && this.cache.getFluidCapability().getFluidAmount(0) + recipeOutput.getAmount() <= this.cache.getFluidCapability().getTankCapacity(0))) {
            return true;
        }
        return false;
    }

    private void meltItem(FluidStack recipeOutput) {
        getItemHandler().extractItem(0, 1, false); // 移除一个输入物品
        this.cache.getFluidCapability().fill(new FluidStack(recipeOutput.getFluid(), recipeOutput.getAmount()), IFluidHandler.FluidAction.EXECUTE);
        this.currentRecipeOutput = FluidStack.EMPTY; // 重置当前配方输出
        this.setChanged();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }

    private void resetProgress() {
        cache.setProgress(0);
        this.currentRecipeOutput = FluidStack.EMPTY;
        this.setChanged();
    }

    public int getProgressPercent() {
        return (int) (((float) getProgress() / totalTime) * 100);
    }

    @Override
    public boolean isWorking() {
        return isWorking;
    }


    @Override
    public void setCapabilityChanged(CapabilityType type) {
        super.setCapabilityChanged(type);
        if(type == CapabilityType.FLUID) level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }

    public MillenniumFluidStorage getFluidTank(){
        return this.cache.getFluidCapability();
    }
}