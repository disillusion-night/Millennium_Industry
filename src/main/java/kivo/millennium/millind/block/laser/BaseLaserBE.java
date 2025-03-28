package kivo.millennium.millind.block.laser;

import kivo.millennium.millind.capability.MillenniumEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public abstract class BaseLaserBE extends BlockEntity {
    public static final String ENERGY_TAG = "Energy";

    public static final int MAXTRANSFER = 10000;
    public static final int costPerTick = 2000;
    public static final int CAPACITY = 100000;

    protected final MillenniumEnergyStorage energy = new MillenniumEnergyStorage(CAPACITY);

    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energy);

    public BaseLaserBE(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public void tickServer(Level pLevel, BlockPos pPos, BlockState pState, BaseLaserBE pBlockEntity) {
        checkPower(pLevel, pPos, pState, pBlockEntity);
        onlaser(pLevel, pPos, pState, pBlockEntity);

    }

    private void checkPower(Level pLevel, BlockPos pPos, BlockState pState, BaseLaserBE pBlockEntity){
        if(getStoredPower() > 0){
            level.setBlockAndUpdate(pPos, pState.setValue(BaseLaserBL.WORKING, true));
            onlaser(pLevel, pPos, pState, pBlockEntity);
        }else {
            level.setBlockAndUpdate(pPos, pState.setValue(BaseLaserBL.WORKING, false));
        }
    }

    public void onlaser(Level pLevel, BlockPos pPos, BlockState pState, BaseLaserBE pBlockEntity){
        energy.costEnergy(costPerTick);
    }

    public int getStoredPower() {
        return energy.getEnergyStored();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(ENERGY_TAG, energy.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(ENERGY_TAG)) {
            energy.deserializeNBT(tag.get(ENERGY_TAG));
        }
    }

    @Nonnull
    private EnergyStorage createEnergyStorage() {
        return new EnergyStorage(CAPACITY, MAXTRANSFER, MAXTRANSFER);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY && side == this.getBlockState().getValue(BaseLaserBL.FACING).getOpposite()) {
            return energyHandler.cast();
        } else {
            return super.getCapability(cap, side);
        }
    }

    @NotNull
    public void onRemove(){

    }

}
