package kivo.millennium.millind.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.List;

import static kivo.millennium.millind.Main.getRL;

public class MeltingRecipe extends GenericRecipe {
    public MeltingRecipe(ResourceLocation id, List<ISlotProxy> input, List<ISlotProxy> output, int time, int energy) {
        super(id, input, output, time, energy);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<MeltingRecipe> {
        private Type() { }
        public static final MeltingRecipe.Type INSTANCE = new MeltingRecipe.Type();
        public static final String ID = "melting";
    }

    public static class Serializer extends GenericRecipe.Serializer<MeltingRecipe> {
        public static final Serializer INSTANCE = new Serializer(new MeltingRecipeFactory());
        public static final ResourceLocation ID = getRL("melting");

        public Serializer(RecipeFactory<MeltingRecipe> factory) {
            super(factory);
        }
    }

    public static class MeltingRecipeFactory implements GenericRecipe.Serializer.RecipeFactory<MeltingRecipe> {
        @Override
        public MeltingRecipe create(ResourceLocation id, String group, CookingBookCategory category, List<ISlotProxy> inputs, List<ISlotProxy> outputs, int time, int energy) {
            /*
            if (inputs.size() != 1 || !(inputs.get(0) instanceof ItemComponent)) {
                throw new IllegalArgumentException("MeltingRecipe must have exactly one ItemComponent as input.");
            }
            if (outputs.size() != 1 || !(outputs.get(0) instanceof FluidComponent)) {
                throw new IllegalArgumentException("MeltingRecipe must have exactly one FluidComponent as output.");
            }*/
            return new MeltingRecipe(id, inputs, outputs, time, energy);
        }
    }
}