package kivo.millennium.client.datagen.loaderbuilder;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class PipeLoaderBuilder extends CustomLoaderBuilder<BlockModelBuilder> {

    public PipeLoaderBuilder(ResourceLocation loader, BlockModelBuilder parent, ExistingFileHelper existingFileHelper) {
        super(loader, parent, existingFileHelper);
    }
}
