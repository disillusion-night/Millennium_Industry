package kivo.millennium.millind.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Arrays;
import java.util.List;

import static kivo.millennium.millind.Main.getRL;

public class ResonanceRecipe extends GenericRecipe {
    public ResonanceRecipe(ResourceLocation id, List<ISlotProxy> inputItem, List<ISlotProxy> output, int time, int energy) {
        super(id, inputItem, output, time, energy);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<ResonanceRecipe> {
        private Type() { }
        public static final ResonanceRecipe.Type INSTANCE = new ResonanceRecipe.Type();
        public static final String ID = "resonance";
    }

    public static class Serializer extends GenericRecipe.Serializer<ResonanceRecipe> {
        public static final Serializer INSTANCE = new Serializer(new FusionRecipeFactory());
        public static final ResourceLocation ID = getRL("resonance");

        public Serializer(RecipeFactory<ResonanceRecipe> factory) {
            super(factory);
        }
    }

    public static class FusionRecipeFactory implements GenericRecipe.Serializer.RecipeFactory<ResonanceRecipe> {
        @Override
        public ResonanceRecipe create(ResourceLocation id, String group, CookingBookCategory category, List<ISlotProxy> inputs, List<ISlotProxy> outputs, int time, int energy) {
            return new ResonanceRecipe(id, inputs, outputs, time, energy);
        }
    }
}