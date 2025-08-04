package kivo.millennium.client.datagen.loaderbuilder;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class PipeLoaderBuilder extends CustomLoaderBuilder<BlockModelBuilder> {
    private final boolean facade;

    public PipeLoaderBuilder(ResourceLocation loader, BlockModelBuilder parent, ExistingFileHelper existingFileHelper,
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
