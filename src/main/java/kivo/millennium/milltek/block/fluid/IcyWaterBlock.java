package kivo.millennium.milltek.block.fluid;

import kivo.millennium.milltek.init.MillenniumFluids;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class IcyWaterBlock extends LiquidBlock {
    public IcyWaterBlock() {
        super(MillenniumFluids.ICY_WATER.get(), BlockBehaviour.Properties.of()
                .replaceable()
                .explosionResistance(100.0f)
                .mapColor(MapColor.WATER)
                .liquid()
                .noCollission()
                .noLootTable()
                .noParticlesOnBreak()
        );
    }
}
