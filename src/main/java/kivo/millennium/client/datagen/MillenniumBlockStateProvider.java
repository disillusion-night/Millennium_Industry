package kivo.millennium.client.datagen;

import com.google.gson.JsonObject;
import kivo.millennium.millind.Main;
import kivo.millennium.millind.block.device.MillenniumBlockProperty;
import kivo.millennium.millind.init.MillenniumBlocks;
import kivo.millennium.millind.util.ShapeUtils;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static kivo.millennium.millind.Main.MODID;
import static kivo.millennium.millind.Main.getKey;

public class MillenniumBlockStateProvider extends BlockStateProvider {
    ExistingFileHelper efh;
    public MillenniumBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MODID, exFileHelper);
        this.efh = exFileHelper;
    }

    @Override
    protected void registerStatesAndModels() {
        addWithHaveModel(MillenniumBlocks.METAL_TANK_BL.get(), "metal_tank");

        cubeAllBlockWithItem(MillenniumBlocks.STEEL_BLOCK, "material");
        cubeAllBlockWithItem(MillenniumBlocks.LEAD_BLOCK, "material");
        cubeAllBlockWithItem(MillenniumBlocks.LEAD_ORE, "material");
        cubeAllBlockWithItem(MillenniumBlocks.DEEPSLATE_LEAD_ORE, "material");
        cubeAllBlockWithItem(MillenniumBlocks.RAW_LEAD_BLOCK, "material");
        cubeAllBlockWithItem(MillenniumBlocks.ALUMINUM_BLOCK, "material");
        cubeAllBlockWithItem(MillenniumBlocks.ALUMINUM_ALLOY_BLOCK, "material");
        cubeAllBlockWithItem(MillenniumBlocks.TITANIUM_ALLOY_BLOCK, "material");
        cubeAllBlockWithItem(MillenniumBlocks.WOLFRAM_STEEL_BLOCK, "material");
        cubeAllBlockWithItem(MillenniumBlocks.ALUMINUM_ORE, "material");
        cubeAllBlockWithItem(MillenniumBlocks.DEEPSLATE_ALUMINUM_ORE, "material");
        cubeAllBlockWithItem(MillenniumBlocks.RAW_ALUMINUM_BLOCK, "material");
        cubeAllBlockWithItem(MillenniumBlocks.ALERT_BLOCK);

        horizontalOrientable(MillenniumBlocks.RESONANCE_CHAMBER_BL, "resonance_chamber");

        sixFacing(MillenniumBlocks.NETHER_STAR_LASER_BL, "nether_star_laser");
        MeltingFurnace(MillenniumBlocks.MELTING_FURNACE_BL);
        InductionFurnace(MillenniumBlocks.INDUCTION_FURNACE_BL);
        simpleOrientableWithTop(MillenniumBlocks.CRUSHER_BL.get(), "crusher");
        simpleOrientableExtra(MillenniumBlocks.CRYSTALLIZER_BL.get(), "crystallizer");

        solarGenerator(MillenniumBlocks.SOLAR_GENERATOR);


    }

    public static class CableLoaderBuilder extends CustomLoaderBuilder<BlockModelBuilder> {

        private final boolean facade;

        public CableLoaderBuilder(ResourceLocation loader, BlockModelBuilder parent, ExistingFileHelper existingFileHelper,
                                  boolean facade) {
            super(loader, parent, existingFileHelper);
            this.facade = facade;
        }

        @Override
        public JsonObject toJson(JsonObject json) {
            JsonObject obj = super.toJson(json);
            obj.addProperty("facade", facade);
            return obj;
        }
    }

    public void addWithHaveModel(Block block, String name){
        var model_path = models().getExistingFile(Main.getRL(name));
        var model = new ConfiguredModel(model_path);
        getVariantBuilder(block).partialState().setModels(model);
        simpleBlockItem(block,model_path);
    }

    public <B extends Block> void cubeAllBlockWithItem(RegistryObject<B> block) {
        ModelFile blockModel = models().getBuilder("block/" + block.getId().getPath())
                .parent(new ConfiguredModel(models().getExistingFile(new ResourceLocation("minecraft:block/cube_all"))).model)
                .texture("all", Main.getRL("block/" + block.getId().getPath()));

        getVariantBuilder(block.get()).partialState().setModels(new ConfiguredModel(blockModel));
        itemModels().getBuilder(block.getId().getPath()).parent(blockModel);
    }


    public <B extends Block> void cubeAllBlockWithItem(RegistryObject<B> block, String prefix) {
        ModelFile blockModel = models().getBuilder("block/" + prefix + "/" + block.getId().getPath())
                .parent(new ConfiguredModel(models().getExistingFile(new ResourceLocation("minecraft:block/cube_all"))).model)
                .texture("all", Main.getRL("block/"+ prefix + "/" + block.getId().getPath()));

        getVariantBuilder(block.get()).partialState().setModels(new ConfiguredModel(blockModel));
        itemModels().getBuilder(block.getId().getPath()).parent(blockModel);
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
            String poweredSuffix = blockState.getValue(MillenniumBlockProperty.WORKING)? "_on" : "";

            return ConfiguredModel.builder()
                    .modelFile(new ConfiguredModel(models().getExistingFile(Main.getRL(modelPath + poweredSuffix))).model)
                    .rotationX(ShapeUtils.getXRotation(blockState.getValue(BlockStateProperties.FACING)))
                    .rotationY(ShapeUtils.getYRotation(blockState.getValue(BlockStateProperties.FACING)))
                    .build();
        });

        simpleBlockItem(block.get(), new ConfiguredModel(models().getExistingFile(Main.getRL(modelPath))).model);
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
            boolean IsPowered = blockState.getValue(MillenniumBlockProperty.WORKING);

            return ConfiguredModel.builder()
                    .modelFile(new ConfiguredModel(models().getExistingFile(Main.getRL(modelPath + (IsPowered ? "_on" : "")))).model)
                    .rotationY(ShapeUtils.getYRotation(blockState.getValue(HorizontalDirectionalBlock.FACING)))
                    .build();
        });

        simpleBlockItem(block.get(), new ConfiguredModel(models().getExistingFile(Main.getRL(modelPath))).model);
    }



    /**
     *  具有四个朝向和亮灭状态的方块的 BlockState 生成，左右侧，后方和底部共用.
     *
     * @param block     要处理的方块实例
     * @param modelPath 模型在资源包下的路径
     */
    private void simpleOrientableWithTop(Block block, String modelPath) {
        VariantBlockStateBuilder builder = getVariantBuilder(block);

        builder.forAllStates(blockState -> {
            boolean IsPowered = blockState.getValue(MillenniumBlockProperty.WORKING);

            ModelFile blockModel = models().getBuilder(modelPath + (IsPowered ? "_on" : ""))
                    .parent(itemModels().getExistingFile(Main.getRL("block/orientable_with_top")))
                    .texture("front", Main.getRL("block/" + modelPath + "_front" + (IsPowered ? "_on" : "_off")))
                    .texture("side", Main.getRL("block/" + modelPath + "_side"))
                    .texture("top", Main.getRL("block/" + modelPath + "_top"));



            return ConfiguredModel.builder()
                    .modelFile(blockModel)
                    .rotationY(ShapeUtils.getYRotation(blockState.getValue(HorizontalDirectionalBlock.FACING)))
                    .build();
        });

        simpleBlockItem(block, new ConfiguredModel(models().getExistingFile(Main.getRL(modelPath))).model);
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
            boolean IsPowered = blockState.getValue(MillenniumBlockProperty.WORKING);

            ModelFile blockModel = models().getBuilder(modelPath + (IsPowered ? "_on" : ""))
                    .parent(itemModels().getExistingFile(new ResourceLocation("minecraft", "block/orientable")))
                    .texture("front", Main.getRL("block/" + modelPath + "_front" + (IsPowered ? "_on" : "_off")))
                    .texture("side", Main.getRL("block/" + modelPath + "_side"))
                    .texture("top", Main.getRL("block/" + modelPath + "_top"));



            return ConfiguredModel.builder()
                    .modelFile(blockModel)
                    .rotationY(ShapeUtils.getYRotation(blockState.getValue(BlockStateProperties.FACING)))
                    .build();
        });

        simpleBlockItem(block, new ConfiguredModel(models().getExistingFile(Main.getRL(modelPath))).model);
    }



    /**
     *  具有四个朝向和亮灭状态的方块的 BlockState 生成.
     *
     * @param block     要处理的方块实例
     * @param modelPath 模型在资源包下的路径
     */
    private void simpleOrientableExtra(Block block, String modelPath) {
        VariantBlockStateBuilder builder = getVariantBuilder(block);

        builder.forAllStates(blockState -> {
            boolean IsPowered = blockState.getValue(MillenniumBlockProperty.WORKING);

            ModelFile blockModel = models().getBuilder(modelPath + (IsPowered ? "_on" : ""))
                    .parent(itemModels().getExistingFile(Main.getRL("block/orientable_with_bottom_extra")))
                    .texture("front", Main.getRL("block/" + modelPath + "_front" + (IsPowered ? "_on" : "_off")))
                    .texture("side", Main.getRL("block/" + modelPath + "_side"))
                    .texture("bottom", Main.getRL("block/" + modelPath + "_bottom"))
                    .texture("top", Main.getRL("block/" + modelPath + "_top"));



            return ConfiguredModel.builder()
                    .modelFile(blockModel)
                    .rotationY(ShapeUtils.getYRotation(blockState.getValue(HorizontalDirectionalBlock.FACING)))
                    .build();
        });

        simpleBlockItem(block, new ConfiguredModel(models().getExistingFile(Main.getRL(modelPath))).model);
    }

    private  <T extends Block> void solarGenerator(RegistryObject<T> block) {
        VariantBlockStateBuilder builder = getVariantBuilder(block.get());

        builder.forAllStates(blockState -> {

            ModelFile blockModel = models().getBuilder(block.getId().getPath())
                    .parent(itemModels().getExistingFile(new ResourceLocation("minecraft", "block/template_daylight_detector")))
                    .texture("side", Main.getRL("block/" + block.getId().getPath() + "_side"))
                    .texture("top", Main.getRL("block/" + block.getId().getPath() + "_top"));



            return ConfiguredModel.builder()
                    .modelFile(blockModel)
                    .build();
        });

        simpleBlockItem(block.get(), new ConfiguredModel(models().getExistingFile(Main.getRL(block.getId().getPath()))).model);
    }

    private void Crystallizer(RegistryObject<Block> block) {
        VariantBlockStateBuilder builder = getVariantBuilder(block.get());
        ResourceLocation resource = block.getId();

        builder.forAllStates(blockState -> {
            boolean IsPowered = blockState.getValue(MillenniumBlockProperty.WORKING);


            ModelFile blockModel = models().getBuilder(resource.getPath() + (IsPowered ? "_on" : ""))
                    .parent(itemModels().getExistingFile(Main.getRL("block/cube")))
                    .texture("front", Main.getRL("block/" + resource.getPath() + "_front" + (IsPowered ? "_on" : "_off")))
                    .texture("side", Main.getRL("block/" + resource.getPath() + "_side"))
                    .texture("top", Main.getRL("block/" + resource.getPath() + "_top")  + (IsPowered ? "_on" : "_off"));



            return ConfiguredModel.builder()
                    .modelFile(blockModel)
                    .rotationY(ShapeUtils.getYRotation(blockState.getValue(HorizontalDirectionalBlock.FACING)))
                    .build();
        });

        simpleBlockItem(block.get(), new ConfiguredModel(models().getExistingFile(resource)).model);
    }

    private<T extends Block> void InductionFurnace(RegistryObject<T> block) {
        VariantBlockStateBuilder builder = getVariantBuilder(block.get());
        ResourceLocation resource = block.getId();

        builder.forAllStates(blockState -> {
            boolean IsPowered = blockState.getValue(MillenniumBlockProperty.WORKING);


            ModelFile blockModel = models().getBuilder(resource.getPath() + (IsPowered ? "_on" : ""))
                    .parent(itemModels().getExistingFile(Main.getRL("block/orientable_with_top")))
                    .texture("front", Main.getRL("block/" + resource.getPath() + "_front" + (IsPowered ? "_on" : "_off")))
                    .texture("side", Main.getRL("block/" + resource.getPath() + "_side"))
                    .texture("top", Main.getRL("block/" + resource.getPath() + "_top"));



            return ConfiguredModel.builder()
                    .modelFile(blockModel)
                    .rotationY(ShapeUtils.getYRotation(blockState.getValue(HorizontalDirectionalBlock.FACING)))
                    .build();
        });

        simpleBlockItem(block.get(), new ConfiguredModel(models().getExistingFile(resource)).model);
    }


    private<T extends Block> void MeltingFurnace(RegistryObject<T> block) {
        VariantBlockStateBuilder builder = getVariantBuilder(block.get());
        ResourceLocation resource = block.getId();

        builder.forAllStates(blockState -> {
            boolean IsPowered = blockState.getValue(MillenniumBlockProperty.WORKING);


            ModelFile blockModel = models().getBuilder(resource.getPath() + (IsPowered ? "_on" : ""))
                    .parent(itemModels().getExistingFile(Main.getRL("block/orientable_with_top")))
                    .texture("front", Main.getRL("block/" + resource.getPath() + "_front" + (IsPowered ? "_on" : "_off")))
                    .texture("side", Main.getRL("block/" + resource.getPath() + "_side"))
                    .texture("top", Main.getRL("block/" + resource.getPath() + "_top")  + (IsPowered ? "_on" : "_off"));



            return ConfiguredModel.builder()
                    .modelFile(blockModel)
                    .rotationY(ShapeUtils.getYRotation(blockState.getValue(HorizontalDirectionalBlock.FACING)))
                    .build();
        });

        simpleBlockItem(block.get(), new ConfiguredModel(models().getExistingFile(resource)).model);
    }
}
