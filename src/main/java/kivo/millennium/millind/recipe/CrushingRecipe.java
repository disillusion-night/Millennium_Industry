package kivo.millennium.millind.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.Arrays;
import java.util.List;

import static kivo.millennium.millind.Main.getRL;

public class CrushingRecipe extends GenericRecipe {
    public CrushingRecipe(ResourceLocation id, List<ISlotProxy> input, List<ISlotProxy> output, int time, int energy) {
        super(id, input, output, time, energy);
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return outputs.getItem(0);
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
        public static final ResourceLocation ID = getRL("crushing");

        public Serializer(CrushingRecipeFactory factory) {
            super(factory);
        }
    }

    public static class CrushingRecipeFactory implements GenericRecipe.Serializer.RecipeFactory<CrushingRecipe> {
        @Override
        public CrushingRecipe create(ResourceLocation id, String group, CookingBookCategory category, List<ISlotProxy> inputs, List<ISlotProxy> outputs, int time, int energy) {
            /*
            if (inputs.size() != 1 || !(inputs.get(0) instanceof ItemComponent)) {
                throw new IllegalArgumentException("CrushingRecipe must have exactly one ItemComponent as input.");
            }
            if (outputs.size() != 1 || !(outputs.get(0) instanceof ItemComponent)) {
                throw new IllegalArgumentException("CrushingRecipe must have exactly one ItemComponent as output.");
            }*/
            return new CrushingRecipe(id, inputs, outputs, time, energy);
        }
    }
}