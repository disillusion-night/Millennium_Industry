package kivo.millennium.millind.cables.blocks;

import net.minecraft.world.level.block.SoundType;

public class GlassPipe extends AbstractPipeBL{
    public GlassPipe() {
        super(Properties.of().noOcclusion().noCollission().sound(SoundType.GLASS));
    }
}
