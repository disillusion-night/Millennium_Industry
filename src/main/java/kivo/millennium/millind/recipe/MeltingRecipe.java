package kivo.millennium.millind.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kivo.millennium.millind.Main;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.List;

import static kivo.millennium.millind.Main.getRL;

public class MeltingRecipe extends GenericRecipe {
    public MeltingRecipe(ResourceLocation id, ItemComponent input, FluidComponent output, int time, int energy) {
        super(id, Arrays.asList(input), Arrays.asList(output), time, energy);
        if (!(input instanceof ItemComponent) || !(output instanceof FluidComponent)) {
            throw new IllegalArgumentException("MeltingRecipe input must be an ItemComponent and output must be a FluidComponent.");
        }
    }

    public FluidComponent getOutput() {
        return (FluidComponent) this.outputs.get(0);
    }

    @Override
    public ComponentCollection getCollection(ExtendedContainer container) {
        return new ComponentCollection().addItemStack(container.getItem(0));
    }

    @Override
    public ItemStack assemble(ExtendedContainer pContainer, RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY; // Melting produces a fluid, not an item in the traditional sense for assemble
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY; // Similar to assemble, represents the recipe output for display purposes (can be empty)
    }

    public FluidStack getResultFluid() {
        return getOutput().getFluidStack().copy();
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
        public MeltingRecipe create(ResourceLocation id, String group, CookingBookCategory category, List<RecipeComponent> inputs, List<RecipeComponent> outputs, int time, int energy) {
            if (inputs.size() != 1 || !(inputs.get(0) instanceof ItemComponent)) {
                throw new IllegalArgumentException("MeltingRecipe must have exactly one ItemComponent as input.");
            }
            if (outputs.size() != 1 || !(outputs.get(0) instanceof FluidComponent)) {
                throw new IllegalArgumentException("MeltingRecipe must have exactly one FluidComponent as output.");
            }
            return new MeltingRecipe(id, (ItemComponent) inputs.get(0), (FluidComponent) outputs.get(0), time, energy);
        }
    }
}