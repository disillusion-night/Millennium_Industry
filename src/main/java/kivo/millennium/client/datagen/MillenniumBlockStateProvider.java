package kivo.millennium.client.datagen;

import kivo.millennium.millind.init.MillenniumBlocks;
import kivo.millennium.millind.init.MillenniumMenuTypes;
import kivo.millennium.millind.util.ShapeUtils;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

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

        cubeAllBlockWithItem(MillenniumBlocks.LEAD_BLOCK, "ore");
        cubeAllBlockWithItem(MillenniumBlocks.LEAD_ORE, "ore");
        cubeAllBlockWithItem(MillenniumBlocks.DEEPSLATE_LEAD_ORE, "ore");
        cubeAllBlockWithItem(MillenniumBlocks.RAW_LEAD_BLOCK, "ore");

        sixFacing(MillenniumBlocks.NETHER_STAR_LASER_BL, "nether_star_laser");
        simpleOrientable(MillenniumBlocks.GENERATOR_BL.get(), "generator");
        simpleOrientableWithTop(MillenniumBlocks.INDUCTION_FURNACE_BL.get(), "induction_furnace");
        simpleOrientableWithTop(MillenniumBlocks.CRUSHER_BL.get(), "crusher");

        solarGenerator(MillenniumBlocks.SOLAR_GENERATOR);
        //simpleOrientableWithTop();

    }

    public void addWithHaveModel(Block block, String name){
        var model_path = models().getExistingFile(getRL(name));
        var model = new ConfiguredModel(model_path);
        getVariantBuilder(block).partialState().setModels(model);
        simpleBlockItem(block,model_path);
    }

    public <B extends Block> void cubeAllBlockWithItem(RegistryObject<B> block, String prefix) {
        ModelFile blockModel = models().getBuilder(prefix + "/" + block.getId().getPath())
                .parent(new ConfiguredModel(models().getExistingFile(new ResourceLocation("minecraft:block/cube_all"))).model)
                .texture("all", getRL("block/"+ prefix + "/" + block.getId().getPath()));

        getVariantBuilder(block.get()).partialState().setModels(new ConfiguredModel(blockModel));
        simpleBlockItem(block.get(), blockModel);
    }

    /**
     *  具有六个朝向和亮灭状态的方块的 BlockState 生成.
     *
     * @param block     要处理的方块实例
     * @param modelPath 模型在资源包下的路径
     */
    private <T extends Block> void sixFacing(RegistryObject<T> block, String modelPath) {
        VariantBlockStateBuilder builder = getVariantBuilder(block.get());

        builder.forAllStates(blockState -> {
            String poweredSuffix = blockState.getValue(BlockStateProperties.POWERED)? "_on" : "";

            return ConfiguredModel.builder()
                    .modelFile(new ConfiguredModel(models().getExistingFile(getRL(modelPath + poweredSuffix))).model)
                    .rotationX(ShapeUtils.getXRotation(blockState.getValue(BlockStateProperties.FACING)))
                    .rotationY(ShapeUtils.getYRotation(blockState.getValue(BlockStateProperties.FACING)))
                    .build();
        });

        simpleBlockItem(block.get(), new ConfiguredModel(models().getExistingFile(getRL(modelPath))).model);
    }


    /**
     *  具有四个朝向和亮灭状态的方块的 BlockState 生成.
     *
     * @param block     要处理的方块实例
     * @param modelPath 模型在资源包下的路径
     */
    private <T extends Block> void horizontalOrientable(RegistryObject<T> block, String modelPath) {
        VariantBlockStateBuilder builder = getVariantBuilder(block.get());

        builder.forAllStates(blockState -> {
            boolean IsPowered = blockState.getValue(BlockStateProperties.POWERED);

            return ConfiguredModel.builder()
                    .modelFile(new ConfiguredModel(models().getExistingFile(getRL(modelPath + (IsPowered ? "_on" : "")))).model)
                    .rotationY(ShapeUtils.getYRotation(blockState.getValue(BlockStateProperties.FACING)))
                    .build();
        });

        simpleBlockItem(block.get(), new ConfiguredModel(models().getExistingFile(getRL(modelPath))).model);
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
                    .rotationY(ShapeUtils.getYRotation(blockState.getValue(HorizontalDirectionalBlock.FACING)))
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



    private  <T extends Block> void solarGenerator(RegistryObject<T> block) {
        VariantBlockStateBuilder builder = getVariantBuilder(block.get());

        builder.forAllStates(blockState -> {

            ModelFile blockModel = models().getBuilder(block.getId().getPath())
                    .parent(itemModels().getExistingFile(new ResourceLocation("minecraft", "block/template_daylight_detector")))
                    .texture("side", getRL("block/" + block.getId().getPath() + "_side"))
                    .texture("top", getRL("block/" + block.getId().getPath() + "_top"));



            return ConfiguredModel.builder()
                    .modelFile(blockModel)
                    .build();
        });

        simpleBlockItem(block.get(), new ConfiguredModel(models().getExistingFile(getRL(block.getId().getPath()))).model);
    }

}
