package kivo.millennium.client.datagen;

import kivo.millennium.millind.init.MillenniumBlocks;
import kivo.millennium.millind.util.ShapeUtils;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
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

        simpleOrientableBlockState(MillenniumBlocks.NETHER_STAR_LASER_BL.get(), "nether_star_laser");
    }

    public void addWithHaveModel(Block block, String name){
        var model_path = models().getExistingFile(getRL(name));
        var model = new ConfiguredModel(model_path);
        getVariantBuilder(block).partialState().setModels(model);
        simpleBlockItem(block,model_path);
    }


    /**
     *  具有六个朝向和亮灭状态的方块的 BlockState 生成.
     *
     * @param block     要处理的方块实例
     * @param modelPath 模型在资源包下的路径
     */
    private void simpleOrientableBlockState(Block block, String modelPath) {
        VariantBlockStateBuilder builder = getVariantBuilder(block); // 获取 VariantBlockStateBuilder

        builder.forAllStates(blockState -> {
            String poweredSuffix = blockState.getValue(BlockStateProperties.LIT)? "_on" : "";

            return ConfiguredModel.builder()
                    .modelFile(new ConfiguredModel(models().getExistingFile(getRL(modelPath + poweredSuffix))).model)
                    .rotationX(ShapeUtils.getXRotation(blockState.getValue(BlockStateProperties.FACING)))
                    .rotationY(ShapeUtils.getYRotation(blockState.getValue(BlockStateProperties.FACING)))
                    .build();
        });

        simpleBlockItem(block, new ConfiguredModel(models().getExistingFile(getRL(modelPath))).model);
    }


}
