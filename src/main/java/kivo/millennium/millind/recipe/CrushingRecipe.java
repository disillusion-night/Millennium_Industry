package kivo.millennium.millind.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import java.util.Arrays;
import java.util.List;

import static kivo.millennium.millind.Main.getRL;

public class CrushingRecipe extends GenericRecipe {
    public CrushingRecipe(ResourceLocation id, ItemComponent input, ItemComponent output, int time, int energy) {
        super(id, Arrays.asList(input), Arrays.asList(output), time, energy);
    }

    @Override
    public ComponentCollection getCollection(ExtendedContainer container) {
        return new ComponentCollection().addItemStack(container.getItem(0));
    }

    @Override
    public ItemStack assemble(ExtendedContainer pContainer, RegistryAccess pRegistryAccess) {
        return ((ItemComponent) this.outputs.get(0)).getItemStack().copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ((ItemComponent) this.outputs.get(0)).getItemStack().copy();
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
        public CrushingRecipe create(ResourceLocation id, String group, CookingBookCategory category, List<RecipeComponent> inputs, List<RecipeComponent> outputs, int time, int energy) {
            if (inputs.size() != 1 || !(inputs.get(0) instanceof ItemComponent)) {
                throw new IllegalArgumentException("CrushingRecipe must have exactly one ItemComponent as input.");
            }
            if (outputs.size() != 1 || !(outputs.get(0) instanceof ItemComponent)) {
                throw new IllegalArgumentException("CrushingRecipe must have exactly one ItemComponent as output.");
            }
            return new CrushingRecipe(id, (ItemComponent) inputs.get(0), (ItemComponent) outputs.get(0), time, energy);
        }
    }
}