package kivo.millennium.millind.cables.blocks;

import net.minecraft.world.level.block.SoundType;

public class GlassLine extends AbstractPipeBL{
    public GlassLine() {
        super(Properties.of().noOcclusion().noCollission().sound(SoundType.GLASS));
    }
}
