package kivo.millennium.client.datagen;

import kivo.millennium.millind.block.multiblock.controller.HMIBL;
import kivo.millennium.millind.init.MillenniumBlocks;
import kivo.millennium.millind.init.MillenniumItems;
import kivo.millennium.millind.item.Oopart.Oopart;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import static kivo.millennium.millind.Main.MODID;

public class MillenniumItemModelProvider extends ItemModelProvider {

    public MillenniumItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (RegistryObject<Oopart> oopart : MillenniumItems.OOPARTS){
            registerOopartModel(oopart);
        }//Registry Ooparts

        blockWithExistingModel(MillenniumBlocks.HMI_BL.get());
        blockWithExistingModel(MillenniumBlocks.PROJECTOR_BL.get());

    }

    private void blockWithExistingModel(Block block){
        String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
        this.withExistingParent(path, modLoc("block/" + path));
    }

    private void blockWithExistingModel(Block block, String path){
        this.withExistingParent(BuiltInRegistries.BLOCK.getKey(block).getPath(), modLoc(path));
    }

    private void registerOopartModel(RegistryObject<Oopart> oopart){
        getBuilder(oopart.getId().toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", "item/oopart/" + oopart.getId().getPath());
    }


    private void registerBlockItemModel(RegistryObject<Oopart> oopart){
        getBuilder(oopart.getId().toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", "item/oopart/" + oopart.getId().getPath());
    }



}
