package kivo.millennium.millind.recipe;

import kivo.millennium.millind.Main;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

import static kivo.millennium.millind.Main.getKey;

public class CrystallizingRecipe extends GenericRecipe {
    public CrystallizingRecipe(ResourceLocation id, List<RecipeComponent> input, List<RecipeComponent> output, int time, int energy) {
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

    public static class Type implements RecipeType<CrystallizingRecipe> {
        private Type() { }
        public static final CrystallizingRecipe.Type INSTANCE = new CrystallizingRecipe.Type();
        public static final String ID = "crystallizing";
    }

    public static class Serializer extends GenericRecipe.Serializer<CrystallizingRecipe> {
        public static final Serializer INSTANCE = new Serializer(new FusionRecipeFactory());
        public static final ResourceLocation ID = Main.getRL("crystallizing");

        public Serializer(RecipeFactory<CrystallizingRecipe> factory) {
            super(factory);
        }
    }

    public static class FusionRecipeFactory implements GenericRecipe.Serializer.RecipeFactory<CrystallizingRecipe> {
        @Override
        public CrystallizingRecipe create(ResourceLocation id, String group, CookingBookCategory category, List<RecipeComponent> inputs, List<RecipeComponent> outputs, int time, int energy) {

            return new CrystallizingRecipe(id, inputs, outputs, time, energy);
        }
    }
}