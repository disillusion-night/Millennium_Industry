package kivo.millennium.client.datagen;

import kivo.millennium.millind.init.MillenniumItems;
import kivo.millennium.millind.item.Oopart.Oopart;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import static kivo.millennium.millind.Main.MODID;
import static kivo.millennium.millind.Main.getRL;

public class MillenniumItemModelProvider extends ItemModelProvider {

    public MillenniumItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (RegistryObject<Oopart> oopart : MillenniumItems.OOPARTS){
            registerOopartModel(oopart);
        }

        SimpleMaterialItem(MillenniumItems.COPPER_DUST);

        SimpleMaterialItem(MillenniumItems.IRON_DUST);
        SimpleMaterialItem(MillenniumItems.IRON_PIPE);
        SimpleMaterialItem(MillenniumItems.IRON_ROD);
        SimpleMaterialItem(MillenniumItems.IRON_PANEL);
        SimpleMaterialItem(MillenniumItems.GOLD_DUST);
        SimpleMaterialItem(MillenniumItems.GOLD_PANEL);

        SimpleMaterialItem(MillenniumItems.STEEL_DUST);
        SimpleMaterialItem(MillenniumItems.STEEL_INGOT);
        SimpleMaterialItem(MillenniumItems.STEEL_NUGGET);
        SimpleMaterialItem(MillenniumItems.STEEL_PANEL);
        SimpleMaterialItem(MillenniumItems.STEEL_PIPE);
        SimpleMaterialItem(MillenniumItems.STEEL_ROD);

        SimpleMaterialItem(MillenniumItems.LEAD_DUST);
        SimpleMaterialItem(MillenniumItems.LEAD_INGOT);
        SimpleMaterialItem(MillenniumItems.LEAD_NUGGET);
        SimpleMaterialItem(MillenniumItems.LEAD_PANEL);
        //SimpleMaterialItem(MillenniumItems.LEAD_PIPE);
        SimpleMaterialItem(MillenniumItems.LEAD_ROD);

        SimpleMaterialItem(MillenniumItems.RAW_LEAD);
        SimpleMaterialItem(MillenniumItems.ALUMINIUM_DUST);
        SimpleMaterialItem(MillenniumItems.ALUMINUM_INGOT);
        SimpleMaterialItem(MillenniumItems.ALUMINUM_NUGGET);
        SimpleMaterialItem(MillenniumItems.ALUMINUM_ALLOY_INGOT);
        SimpleMaterialItem(MillenniumItems.ALUMINUM_ALLOY_PANEL);
        SimpleMaterialItem(MillenniumItems.ALUMINUM_ALLOY_PIPE);
        SimpleMaterialItem(MillenniumItems.ALUMINUM_ALLOY_ROD);
        SimpleMaterialItem(MillenniumItems.RAW_ALUMINUM);
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

    private <I extends Item> void SimpleMaterialItem(RegistryObject<I> item){
        getBuilder(MODID + ":" + item.getId().getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", "item/material/" + item.getId().getPath());
    }

    private void registerBlockItemModel(RegistryObject<Oopart> oopart){
        getBuilder(oopart.getId().toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", "item/oopart/" + oopart.getId().getPath());
    }



}
