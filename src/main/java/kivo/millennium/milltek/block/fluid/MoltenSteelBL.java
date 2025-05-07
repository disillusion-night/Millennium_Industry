package kivo.millennium.milltek.block.fluid;

import kivo.millennium.milltek.init.MillenniumFluids;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.MapColor;

public class MoltenSteelBL extends LiquidBlock {
    public MoltenSteelBL() {
        super(MillenniumFluids.MOLTEN_STEEL.get(), Properties.of()
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
