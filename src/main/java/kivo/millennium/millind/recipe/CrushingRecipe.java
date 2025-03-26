package kivo.millennium.millind.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import java.util.Arrays;
import java.util.List;

import static kivo.millennium.millind.Main.getRL;
import static kivo.millennium.millind.Main.log;

public class CrushingRecipe extends GenericRecipe {
    public CrushingRecipe(ResourceLocation id, ItemComponent input, ItemComponent output, int time) {
        super(id, Arrays.asList(input), Arrays.asList(output), time);
    }

    public ItemComponent getInput() {
        return (ItemComponent) this.inputs.get(0);
    }

    public ItemComponent getOutput() {
        return (ItemComponent) this.outputs.get(0);
    }

    @Override
    public boolean matches(ExtendedContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide()) {
            return false;
        }
        return getInput().getItemStack().is(pContainer.getItem(0).getItem());
    }

    @Override
    public ItemStack assemble(ExtendedContainer pContainer, RegistryAccess pRegistryAccess) {
        log(getOutput().getItemStack().getItem().toString());
        return getOutput().getItemStack().copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return getOutput().getItemStack().copy();
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

        @Override
        protected RecipeComponent createComponentFromJson(JsonElement element) {
            if (element.isJsonObject()) {
                JsonObject jsonObject = element.getAsJsonObject();
                if (jsonObject.has("item")) {
                    ItemComponent component = new ItemComponent(ItemStack.EMPTY);
                    component.readFromJson(jsonObject);
                    return component;
                }
            } else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                ItemComponent component = new ItemComponent(ItemStack.EMPTY);
                JsonObject tempJson = new JsonObject();
                tempJson.addProperty("item", element.getAsString());
                component.readFromJson(tempJson);
                return component;
            }
            return null; // 或者抛出异常
        }

        @Override
        protected RecipeComponent createComponentFromNetwork(String type) {
            if ("item".equals(type)) {
                return new ItemComponent(ItemStack.EMPTY);
            }
            return null; // 或者抛出异常
        }
    }

    public static class CrushingRecipeFactory implements GenericRecipe.Serializer.RecipeFactory<CrushingRecipe> {
        @Override
        public CrushingRecipe create(ResourceLocation id, String group, CookingBookCategory category, List<RecipeComponent> inputs, List<RecipeComponent> outputs, int time) {
            if (inputs.size() != 1 || !(inputs.get(0) instanceof ItemComponent)) {
                throw new IllegalArgumentException("CrushingRecipe must have exactly one ItemComponent as input.");
            }
            if (outputs.size() != 1 || !(outputs.get(0) instanceof ItemComponent)) {
                throw new IllegalArgumentException("CrushingRecipe must have exactly one ItemComponent as output.");
            }
            return new CrushingRecipe(id, (ItemComponent) inputs.get(0), (ItemComponent) outputs.get(0), time);
        }
    }
}