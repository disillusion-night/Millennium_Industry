package kivo.millennium.client.datagen;

import kivo.millennium.millind.init.MillenniumBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;

import static kivo.millennium.millind.Main.MODID;
import static kivo.millennium.millind.Main.getRL;

public class MillenniumBlockStateProvider extends BlockStateProvider {
    ExistingFileHelper efh;
    public MillenniumBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MODID, exFileHelper);
        this.efh = exFileHelper;
    }

    @Override
    protected void registerStatesAndModels() {
        addWithHaveModel(MillenniumBlocks.METAL_TANK_BL.get(), "metal_tank");
    }

    public void addWithHaveModel(Block block, String name){
        var model_path = models().getExistingFile(getRL(name));
        var model = new ConfiguredModel(model_path);
        getVariantBuilder(block).partialState().setModels(model);
        simpleBlockItem(block,model_path);
    }
}
