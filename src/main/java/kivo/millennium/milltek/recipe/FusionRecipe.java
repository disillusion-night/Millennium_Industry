package kivo.millennium.milltek.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;

import static kivo.millennium.milltek.Main.getKey;

import java.util.List;

import kivo.millennium.milltek.Main;

public class FusionRecipe extends GenericRecipe {
    public FusionRecipe(ResourceLocation id, List<RecipeComponent> input, List<RecipeComponent> output, int time, int energy) {
        super(id, input, output, time, energy);
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<FusionRecipe> {
        private Type() { }
        public static final FusionRecipe.Type INSTANCE = new FusionRecipe.Type();
        public static final String ID = "fusion";
    }

    public static class Serializer extends GenericRecipe.Serializer<FusionRecipe> {
        public static final Serializer INSTANCE = new Serializer(new FusionRecipeFactory());
        public static final ResourceLocation ID = Main.getRL("fusion");

        public Serializer(RecipeFactory<FusionRecipe> factory) {
            super(factory);
        }
    }

    public static class FusionRecipeFactory implements GenericRecipe.Serializer.RecipeFactory<FusionRecipe> {
        @Override
        public FusionRecipe create(ResourceLocation id, String group, CookingBookCategory category, List<RecipeComponent> inputs, List<RecipeComponent> outputs, int time, int energy) {

            return new FusionRecipe(id, inputs, outputs, time, energy);
        }
    }
}