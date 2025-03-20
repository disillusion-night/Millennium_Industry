package kivo.millennium.millind.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import kivo.millennium.millind.Main;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import static kivo.millennium.millind.Main.getRL;

public class CrushingRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final Ingredient recipeItem;

    public CrushingRecipe(ResourceLocation id, ItemStack output, Ingredient recipeItem) {
        this.id = id;
        this.output = output;
        this.recipeItem = recipeItem;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if(pLevel.isClientSide()) {
            return false;
        }
        return recipeItem.test(pContainer.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return output;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.withSize(1, recipeItem);
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
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
        public static final Type INSTANCE = new Type();
        public static final String ID = "crushing";
    }


    public static class Serializer implements RecipeSerializer<CrushingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                getRL("crushing");

        @Override
        public CrushingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) { ItemStack output;
            if (pSerializedRecipe.has("result")) {
                JsonElement resultElement = pSerializedRecipe.get("result");
                if (resultElement.isJsonObject()) {
                    output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "result"));
                } else if (resultElement.isJsonPrimitive() && resultElement.getAsJsonPrimitive().isString()) {
                    String itemId = GsonHelper.getAsString(pSerializedRecipe, "result");
                    ResourceLocation itemLocation = new ResourceLocation(itemId);
                    output = new ItemStack(BuiltInRegistries.ITEM.getOptional(itemLocation).orElseThrow(() ->
                            new IllegalStateException("Item: " + itemId + " does not exist")), 1);
                } else {
                    throw new JsonSyntaxException("Expected 'result' to be a string or an object");
                }
            } else {
                throw new JsonSyntaxException("Missing 'result', expected to find a string or object");
            }

            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "ingredient"));

            return new CrushingRecipe(pRecipeId, output, ingredient);
        }

        @Override
        public @Nullable CrushingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }

            ItemStack output = buf.readItem();
            return new CrushingRecipe(id, output, inputs.get(0));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CrushingRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());

            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buf);
            }
            buf.writeItemStack(recipe.getResultItem(null), false);
        }
    }
}
