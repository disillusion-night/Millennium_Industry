package kivo.millennium.client.datagen;

import kivo.millennium.millind.init.MillenniumItems;
import kivo.millennium.millind.item.Oopart.Oopart;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import static kivo.millennium.millind.Main.*;

public class MillenniumItemModelProvider extends ItemModelProvider {

    public MillenniumItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        log("Collecting Item Models");

        for (RegistryObject<Oopart> oopart : MillenniumItems.OOPARTS){
            registerOopartModel(oopart);
        }

        for (RegistryObject<Item> material : MillenniumItems.MATERIALS){
            if(material.get() instanceof BlockItem) break;
            simpleMaterialItem(material);
        }


        simpleToolItem(MillenniumItems.STEEL_AXE);
        simpleToolItem(MillenniumItems.STEEL_HOE);
        simpleToolItem(MillenniumItems.STEEL_PICKAXE);
        simpleToolItem(MillenniumItems.STEEL_SHOVEL);
        simpleToolItem(MillenniumItems.STEEL_SWORD);

        simpleToolItem(MillenniumItems.WRENCH);

        simpleToolItem(MillenniumItems.WOLFRAM_STEEL_AXE);
        simpleToolItem(MillenniumItems.WOLFRAM_STEEL_HOE);
        simpleToolItem(MillenniumItems.WOLFRAM_STEEL_PICKAXE);
        simpleToolItem(MillenniumItems.WOLFRAM_STEEL_SHOVEL);
        simpleToolItem(MillenniumItems.WOLFRAM_STEEL_SWORD);

        simpleToolItem(MillenniumItems.TITANIUM_ALLOY_AXE);
        simpleToolItem(MillenniumItems.TITANIUM_ALLOY_HOE);
        simpleToolItem(MillenniumItems.TITANIUM_ALLOY_PICKAXE);
        simpleToolItem(MillenniumItems.TITANIUM_ALLOY_SHOVEL);
        simpleToolItem(MillenniumItems.TITANIUM_ALLOY_SWORD);


        SimpleItem(MillenniumItems.VRLA);
    }

    private void createMetalOreItem(Item ingot){
        //SimpleItem(ingot, );
    }

    private void blockWithExistingModel(Block block){
        String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
        this.withExistingParent(path, modLoc("block/" + path));
    }


    private void blockWithExistingModel(Block block, String rootPath){
        String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
        this.withExistingParent(path, modLoc(rootPath + path));
    }

    /*
    private void blockWithExistingModel(Block block, String path){
        this.withExistingParent(BuiltInRegistries.BLOCK.getKey(block).getPath(), modLoc(path));
    }*/

    private void registerOopartModel(RegistryObject<Oopart> oopart){
        getBuilder(oopart.getId().toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", "item/oopart/" + oopart.getId().getPath());
    }



    private <I extends Item> void SimpleItem(RegistryObject<I> item){
        getBuilder(MODID + ":" + item.getId().getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", "item/" + item.getId().getPath());
    }

    private <I extends Item> void SimpleItem(RegistryObject<I> item, String prefix){
        getBuilder(MODID + ":" + item.getId().getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", "item/" + prefix + "/" + item.getId().getPath());
    }

    private <I extends Item> void simpleMaterialItem(RegistryObject<I> item){
        getBuilder(MODID + ":" + item.getId().getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", "item/material/" + item.getId().getPath());
    }

    private <I extends Item> void simpleToolItem(RegistryObject<I> item){
        getBuilder(MODID + ":" + item.getId().getPath())
                .parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", "item/tool/" + item.getId().getPath());
    }


    private void registerBlockItemModel(RegistryObject<Oopart> oopart){
        getBuilder(oopart.getId().toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", "item/oopart/" + oopart.getId().getPath());
    }



}
