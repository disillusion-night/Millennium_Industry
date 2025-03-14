package kivo.millennium.millind.block.laser;

import kivo.millennium.millind.Main;
import kivo.millennium.millind.block.device.AbstractDeviceBE;
import kivo.millennium.millind.capability.DeviceEnergyStorage;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class SolarGeneratorBE extends AbstractDeviceBE {
    public static int basicPowerPerTick = 10;
    public static int MaxCapability = (int) 1e7;;
    public static int MaxTransferRate = 100;

    public SolarGeneratorBE(BlockPos pWorldPosition, BlockState pBlockState) {
        super(MillenniumBlockEntities.SOLAR_GENERATOR_BE.get(), pWorldPosition, pBlockState, 0);
    }

    @Override
    protected void tickServer() {
        generateEnergy();
        distributeEnergy();

    }
    @Override
    protected DeviceEnergyStorage createEnergyStorage() {
        return new DeviceEnergyStorage(MaxCapability, MaxTransferRate);
    }

    // NBT 数据读写
    @Override
    protected void saveData(CompoundTag pTag) {
        pTag.putInt("energy", energyStorage.getEnergyStored()); // 保存能量数据
    }

    @Override
    public void loadData(CompoundTag pTag) {
        if (pTag.contains("energy")){
            energyStorage.setEnergy(pTag.getInt("energy")); // 加载能量数据
        }
    }
    protected void distributeEnergy() {
        if (this.energyStorage.getEnergyStored() <= 0) {
            return;
        }
        BlockEntity be = level.getBlockEntity(getBlockPos().relative(Direction.DOWN));
        if (be != null) {
            be.getCapability(ForgeCapabilities.ENERGY, Direction.UP).map(e -> {
                if (e.canReceive()) {
                    int received = e.receiveEnergy(energyStorage.getEnergyStored(), false);
                    energyStorage.extractEnergy(received, false);
                    setChanged();
                    return received;
                }
                return 0;
            });
        }
    }

    protected void generateEnergy() {
        this.energyStorage.receiveEnergy(getSunlightStrength() * basicPowerPerTick, false);
    }

    private int getSunlightStrength() {
        int i = level.getBrightness(LightLayer.SKY, getBlockPos()) - level.getSkyDarken();
        float f = level.getSunAngle(1.0F);
        /*
        if (i > 0) {
            float f1 = f < (float) Math.PI ? 0.0F : ((float) Math.PI * 2F);
            f += (f1 - f) * 0.2F;
            i = Math.round((float) i * Mth.cos(f));
        }

        */
        i = Mth.clamp(i, 0, 15);
        i = 15;
        level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(SolarGeneratorBL.POWERED, (i > 0)));
        return i;
    }
}
