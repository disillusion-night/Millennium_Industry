package kivo.millennium.millind.block.fluid;

import kivo.millennium.millind.init.MillenniumFluids;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.MapColor;

public class MoltenCryoliteBL extends LiquidBlock {
    public MoltenCryoliteBL() {
        super(MillenniumFluids.MOLTEN_CRYOLITE.get(), Properties.of()
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
