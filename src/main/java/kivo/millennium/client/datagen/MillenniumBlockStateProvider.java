package kivo.millennium.client.datagen;

import kivo.millennium.millind.init.MillenniumBlocks;
import kivo.millennium.millind.util.ShapeUtils;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
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

        sixFacing(MillenniumBlocks.NETHER_STAR_LASER_BL.get(), "nether_star_laser");
        simpleOrientable(MillenniumBlocks.GENERATOR_BL.get(), "generator");
        //simpleOrientableWithTop();

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
    private void sixFacing(Block block, String modelPath) {
        VariantBlockStateBuilder builder = getVariantBuilder(block);

        builder.forAllStates(blockState -> {
            String poweredSuffix = blockState.getValue(BlockStateProperties.POWERED)? "_on" : "";

            return ConfiguredModel.builder()
                    .modelFile(new ConfiguredModel(models().getExistingFile(getRL(modelPath + poweredSuffix))).model)
                    .rotationX(ShapeUtils.getXRotation(blockState.getValue(BlockStateProperties.FACING)))
                    .rotationY(ShapeUtils.getYRotation(blockState.getValue(BlockStateProperties.FACING)))
                    .build();
        });

        simpleBlockItem(block, new ConfiguredModel(models().getExistingFile(getRL(modelPath))).model);
    }


    /**
     *  具有四个朝向和亮灭状态的方块的 BlockState 生成.
     *
     * @param block     要处理的方块实例
     * @param modelPath 模型在资源包下的路径
     */
    private void horizontalOrientable(Block block, String modelPath) {
        VariantBlockStateBuilder builder = getVariantBuilder(block);

        builder.forAllStates(blockState -> {
            boolean IsPowered = blockState.getValue(BlockStateProperties.POWERED);

            return ConfiguredModel.builder()
                    .modelFile(new ConfiguredModel(models().getExistingFile(getRL(modelPath + (IsPowered ? "_on" : "")))).model)
                    .rotationY(ShapeUtils.getYRotation(blockState.getValue(BlockStateProperties.FACING)))
                    .build();
        });

        simpleBlockItem(block, new ConfiguredModel(models().getExistingFile(getRL(modelPath))).model);
    }


    /**
     *  具有四个朝向和亮灭状态的方块的 BlockState 生成.
     *
     * @param block     要处理的方块实例
     * @param modelPath 模型在资源包下的路径
     */
    private void simpleOrientableWithTop(Block block, String modelPath) {
        VariantBlockStateBuilder builder = getVariantBuilder(block);

        builder.forAllStates(blockState -> {
            boolean IsPowered = blockState.getValue(BlockStateProperties.POWERED);

            ModelFile blockModel = models().getBuilder(modelPath + (IsPowered ? "_on" : ""))
                    .parent(itemModels().getExistingFile(getRL("block/orientable_with_top")))
                    .texture("front", getRL("block/" + modelPath + "_front" + (IsPowered ? "_on" : "_off")))
                    .texture("side", getRL("block/" + modelPath + "_side"))
                    .texture("top", getRL("block/" + modelPath + "_top"));



            return ConfiguredModel.builder()
                    .modelFile(blockModel)
                    .rotationY(ShapeUtils.getYRotation(blockState.getValue(BlockStateProperties.FACING)))
                    .build();
        });

        simpleBlockItem(block, new ConfiguredModel(models().getExistingFile(getRL(modelPath))).model);
    }

    /**
     *  具有四个朝向和亮灭状态的方块的 BlockState 生成.
     *
     * @param block     要处理的方块实例
     * @param modelPath 模型在资源包下的路径
     */
    private void simpleOrientable(Block block, String modelPath) {
        VariantBlockStateBuilder builder = getVariantBuilder(block);

        builder.forAllStates(blockState -> {
            boolean IsPowered = blockState.getValue(BlockStateProperties.POWERED);

            ModelFile blockModel = models().getBuilder(modelPath + (IsPowered ? "_on" : ""))
                    .parent(itemModels().getExistingFile(new ResourceLocation("minecraft", "block/orientable")))
                    .texture("front", getRL("block/" + modelPath + "_front" + (IsPowered ? "_on" : "_off")))
                    .texture("side", getRL("block/" + modelPath + "_side"))
                    .texture("top", getRL("block/" + modelPath + "_top"));



            return ConfiguredModel.builder()
                    .modelFile(blockModel)
                    .rotationY(ShapeUtils.getYRotation(blockState.getValue(BlockStateProperties.FACING)))
                    .build();
        });

        simpleBlockItem(block, new ConfiguredModel(models().getExistingFile(getRL(modelPath))).model);
    }
}
