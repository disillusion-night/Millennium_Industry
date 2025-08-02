package kivo.millennium.milltek.block.tags;

import static kivo.millennium.milltek.Main.getKey;

import kivo.millennium.milltek.Main;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class MillenniumBlockTags {
    public static final TagKey<Block> PIPE = create("pipe");
    public static final TagKey<Block> ENERGY_PIPE = create("energy_pipe");
    public static final TagKey<Block> ITEM_PIPE = create("item_pipe");
    public static final TagKey<Block> FLUID_PIPE = create("fluid_pipe");
    public static final TagKey<Block> GAS_PIPE = create("gas_pipe");
    public static final TagKey<Block> ORE = create("ore");

    private static TagKey<Block> create(String pName) {
        return TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(), Main.getRL(pName));
    }

    public static TagKey<Block> create(ResourceLocation name) {
        return TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(), name);
    }
}
