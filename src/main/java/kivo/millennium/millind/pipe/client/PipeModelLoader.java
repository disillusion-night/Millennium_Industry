package kivo.millennium.millind.pipe.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

import static kivo.millennium.millind.Main.getRL;

public class PipeModelLoader implements IGeometryLoader<PipeModelLoader.PipeModelGeometry> {
    public static final ResourceLocation GENERATOR_LOADER = getRL("pipe_loader");

    public static void register(ModelEvent.RegisterGeometryLoaders event) {
        event.register("pipe_loader", new PipeModelLoader());
    }


    @Override
    public PipeModelGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        JsonObject texture = GsonHelper.getAsJsonObject(jsonObject, "textures");

        ResourceLocation normal = new ResourceLocation(GsonHelper.getAsString(texture, "normal"));
        ResourceLocation none = new ResourceLocation(GsonHelper.getAsString(texture, "none"));
        ResourceLocation cross = new ResourceLocation(GsonHelper.getAsString(texture, "cross"));
        ResourceLocation three = new ResourceLocation(GsonHelper.getAsString(texture, "three"));
        ResourceLocation corner = new ResourceLocation(GsonHelper.getAsString(texture, "corner"));

        return new PipeModelGeometry(normal, three, corner, none, cross);
    }

    public static class PipeModelGeometry implements IUnbakedGeometry<PipeModelGeometry> {

        private final ResourceLocation normal;
        private final ResourceLocation none;
        private final ResourceLocation cross;
        private final ResourceLocation three;
        private final ResourceLocation corner;

        public PipeModelGeometry(ResourceLocation normal,  ResourceLocation three, ResourceLocation corner, ResourceLocation none, ResourceLocation cross) {
            this.corner = corner;
            this.normal = normal;
            this.none = none;
            this.three = three;
            this.cross = cross;
        }

        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
            return new PipeBakedModel(context, corner, normal, none, three, cross);
        }
    }
}