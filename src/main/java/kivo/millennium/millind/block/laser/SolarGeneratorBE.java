package kivo.millennium.millind.block.laser;

import kivo.millennium.millind.block.device.AbstractMachineBE;
import kivo.millennium.millind.capability.CapabilityCache;
import kivo.millennium.millind.capability.MillenniumEnergyStorage;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class SolarGeneratorBE extends AbstractMachineBE {
    public static int basicPowerPerTick = 100;
    public static int MaxCapability = (int) 1e7;;
    public static int MaxTransferRate = 100;

    public SolarGeneratorBE(BlockPos pWorldPosition, BlockState pBlockState) {
        super(MillenniumBlockEntities.SOLAR_GENERATOR_BE.get(), pWorldPosition, pBlockState, new CapabilityCache.Builder()
                .withEnergy(100000, 2000));
    }

    @Override
    protected void tickServer() {
        generateEnergy();
        distributeEnergy();

    }

    @Override
    public boolean isWorking() {
        return false;
    }

    @Override
    public int getProgressPercent() {
        return 0;
    }

    protected void distributeEnergy() {
        if (getEnergyStorage().getEnergyStored() <= 0) {
            return;
        }
        BlockEntity be = level.getBlockEntity(getBlockPos().relative(Direction.DOWN));
        if (be != null) {
            be.getCapability(ForgeCapabilities.ENERGY, Direction.UP).map(e -> {
                if (e.canReceive()) {
                    int received = e.receiveEnergy(getEnergyStorage().getEnergyStored(), false);
                    getEnergyStorage().extractEnergy(received, false);
                    setChanged();
                    return received;
                }
                return 0;
            });
        }
    }

    protected void generateEnergy() {
         getEnergyStorage().receiveEnergy(getSunlightStrength() * basicPowerPerTick, false);
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
        level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(SolarGeneratorBL.WORKING, (i > 0)));
        return i;
    }
}
