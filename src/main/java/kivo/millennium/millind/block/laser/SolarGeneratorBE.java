package kivo.millennium.millind.block.laser;

import kivo.millennium.millind.block.device.AbstractDeviceBE;
import kivo.millennium.millind.init.MillenniumBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SolarGeneratorBE extends AbstractDeviceBE {

    public SolarGeneratorBE(BlockPos pWorldPosition, BlockState pBlockState) {
        super(MillenniumBlockEntities.SOLAR_GENERATOR_BE.get(), pWorldPosition, pBlockState, 1);
    }

    @Override
    protected void tickServer() {

    }

    private static int getSunlightStrength(BlockState pState, Level pLevel, BlockPos pPos){
            int i = pLevel.getBrightness(LightLayer.SKY, pPos) - pLevel.getSkyDarken();
            float f = pLevel.getSunAngle(1.0F);
            if (i > 0) {
                float f1 = f < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);
                f += (f1 - f) * 0.2F;
                i = Math.round((float)i * Mth.cos(f));
            }

            i = Mth.clamp(i, 0, 15);
            pLevel.setBlockAndUpdate(pPos, pState.setValue(SolarGeneratorBL.POWERED, (i > 0)));

            return i;
    }
}
