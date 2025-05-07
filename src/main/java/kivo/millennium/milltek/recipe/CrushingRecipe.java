package kivo.millennium.milltek.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import static kivo.millennium.milltek.Main.getKey;

import java.util.List;

import kivo.millennium.milltek.Main;

public class CrushingRecipe extends GenericRecipe {
    public CrushingRecipe(ResourceLocation id, List<RecipeComponent> input, List<RecipeComponent> output, int time, int energy) {
        super(id, input, output, time, energy);
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return outputs.get(0).asItemComponent().get().copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<CrushingRecipe> {
        private Type() { }
        public static final CrushingRecipe.Type INSTANCE = new CrushingRecipe.Type();
        public static final String ID = "crushing";
    }


    public static class Serializer extends GenericRecipe.Serializer<CrushingRecipe> {
        public static final Serializer INSTANCE = new Serializer(new CrushingRecipeFactory());
        public static final ResourceLocation ID = Main.getRL("crushing");

        public Serializer(CrushingRecipeFactory factory) {
            super(factory);
        }
    }

    public static class CrushingRecipeFactory implements GenericRecipe.Serializer.RecipeFactory<CrushingRecipe> {
        @Override
        public CrushingRecipe create(ResourceLocation id, String group, CookingBookCategory category, List<RecipeComponent> inputs, List<RecipeComponent> outputs, int time, int energy) {

            return new CrushingRecipe(id, inputs, outputs, time, energy);
        }
    }
}