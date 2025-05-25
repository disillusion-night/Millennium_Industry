package kivo.millennium.milltek.block.fluid;

import kivo.millennium.milltek.init.MillenniumFluids;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.MapColor;

public class MoltenIronBL extends LiquidBlock {
    public MoltenIronBL() {
        super(MillenniumFluids.MOLTEN_IRON.get(), Properties.of()
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
