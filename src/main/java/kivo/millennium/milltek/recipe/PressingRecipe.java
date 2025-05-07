package kivo.millennium.milltek.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import static kivo.millennium.milltek.Main.getKey;

import java.util.List;

import kivo.millennium.milltek.Main;

public class PressingRecipe extends GenericRecipe {
    public PressingRecipe(ResourceLocation id, List<RecipeComponent> input, List<RecipeComponent> output, int time, int energy) {
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

    public static class Type implements RecipeType<PressingRecipe> {
        private Type() { }
        public static final PressingRecipe.Type INSTANCE = new PressingRecipe.Type();
        public static final String ID = "pressing";
    }

    public static class Serializer extends GenericRecipe.Serializer<PressingRecipe> {
        public static final Serializer INSTANCE = new Serializer(new FusionRecipeFactory());
        public static final ResourceLocation ID = Main.getRL("pressing");

        public Serializer(RecipeFactory<PressingRecipe> factory) {
            super(factory);
        }
    }

    public static class FusionRecipeFactory implements GenericRecipe.Serializer.RecipeFactory<PressingRecipe> {
        @Override
        public PressingRecipe create(ResourceLocation id, String group, CookingBookCategory category, List<RecipeComponent> inputs, List<RecipeComponent> outputs, int time, int energy) {
            /*
            if (inputs.size() != 2 || !(inputs.get(0) instanceof ItemComponent)) {
                throw new IllegalArgumentException("MeltingRecipe must have exactly one ItemComponent as input.");
            }
            if (outputs.size() != 1 || !(outputs.get(0) instanceof ItemComponent)) {
                throw new IllegalArgumentException("MeltingRecipe must have exactly one FluidComponent as output.");
            }*/
            return new PressingRecipe(id, inputs, outputs, time, energy);
        }
    }
}