package kivo.millennium.milltek.block.fluid;

import kivo.millennium.milltek.init.MillenniumFluids;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.MapColor;

public class RawMoltenAluminumBL extends LiquidBlock {
    public RawMoltenAluminumBL() {
        super(MillenniumFluids.RAW_MOLTEN_ALUMINUM.get(), Properties.of()
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
