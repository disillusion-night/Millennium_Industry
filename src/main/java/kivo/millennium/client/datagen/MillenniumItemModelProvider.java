package kivo.millennium.client.datagen;

import kivo.millennium.millind.init.MillenniumItems;
import kivo.millennium.millind.item.Oopart.Oopart;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
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

        SimpleItem(MillenniumItems.LEAD_INGOT, "ore");
        SimpleItem(MillenniumItems.LEAD_NUGGET, "ore");
        SimpleItem(MillenniumItems.RAW_LEAD, "ore");
        SimpleItem(MillenniumItems.ALUMINUM_INGOT, "ore");
        SimpleItem(MillenniumItems.ALUMINUM_NUGGET, "ore");
        SimpleItem(MillenniumItems.RAW_ALUMINUM, "ore");
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


    private void registerBlockItemModel(RegistryObject<Oopart> oopart){
        getBuilder(oopart.getId().toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", "item/oopart/" + oopart.getId().getPath());
    }



}
