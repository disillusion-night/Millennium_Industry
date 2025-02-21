package kivo.millennium.client.datagen;

import kivo.millennium.millind.init.MillenniumItems;
import kivo.millennium.millind.item.Oopart.Oopart;
import net.minecraft.data.PackOutput;
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
        //getBuilder().parent()
        //withExistingParent("metal_tank", modLoc("block/metal_tank"));
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
