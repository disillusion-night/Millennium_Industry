package kivo.millennium.millind.block.fluid;

import kivo.millennium.millind.init.MillenniumFluids;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.MapColor;

public class MoltenAluminumAlloyBL extends LiquidBlock {
    public MoltenAluminumAlloyBL() {
        super(MillenniumFluids.MOLTEN_ALUMINUM_ALLOY.get(), Properties.of()
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
