package kivo.millennium.millind.cables.blocks;

import net.minecraft.world.level.block.SoundType;

public class BasicEnergyPipe extends AbstractPipeBL{
    public BasicEnergyPipe() {
        super(Properties.of()
                .strength(1.0f)
                .sound(SoundType.METAL)
                .noOcclusion()
        );
    }
}
