package kivo.millennium.millind.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class GenericRecipe implements Recipe<SimpleContainer> {
    protected final ResourceLocation id;
    protected final NonNullList<RecipeComponent> inputs;
    protected final NonNullList<RecipeComponent> outputs;

    public GenericRecipe(ResourceLocation id, List<RecipeComponent> inputs, List<RecipeComponent> outputs) {
        this.id = id;
        this.inputs = NonNullList.of(null, inputs.toArray(new RecipeComponent[0]));
        this.outputs = NonNullList.of(null, outputs.toArray(new RecipeComponent[0]));
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide()) {
            return false;
        }
        // 这里需要更复杂的匹配逻辑，根据具体的机器和输入槽位来判断
        // 简单的实现可以假设输入槽位 0 对应第一个输入成分，以此类推
        if (inputs.size() > 0 && inputs.get(0) instanceof ItemComponent) {
            return ((ItemComponent) inputs.get(0)).getItemStack().is(pContainer.getItem(0).getItem());
        }
        // 对于更复杂的匹配，子类需要覆盖这个方法
        return false;
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        // 默认只处理物品输出的第一个成分
        for (RecipeComponent output : outputs) {
            if (output instanceof ItemComponent) {
                return ((ItemComponent) output).getItemStack().copy();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        for (RecipeComponent input : inputs) {
            if (input instanceof ItemComponent) {
                ingredients.add(Ingredient.of(((ItemComponent) input).getItemStack()));
            }
        }
        return ingredients;
    }

    public List<RecipeComponent> getRecipeInputs() {
        return this.inputs;
    }

    public List<RecipeComponent> getRecipeOutputs() {
        return this.outputs;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        for (RecipeComponent output : outputs) {
            if (output instanceof ItemComponent) {
                return ((ItemComponent) output).getItemStack().copy();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    protected CookingBookCategory category(){
        return CookingBookCategory.MISC;
    }

    @Override
    public abstract RecipeSerializer<?> getSerializer();

    @Override
    public abstract RecipeType<?> getType();

    // 抽象的 Serializer
     public abstract static class Serializer<T extends GenericRecipe> implements RecipeSerializer<T> {

        protected final RecipeFactory<T> factory;

        public Serializer(RecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Override
        public T fromJson(ResourceLocation recipeId, JsonObject json) {
            List<RecipeComponent> inputs = new ArrayList<>();
            List<RecipeComponent> outputs = new ArrayList<>();

            // 解析输入成分 (支持单个 "ingredient" 或 "ingredients" 数组)
            if (json.has("ingredient")) {
                JsonElement ingredientElement = json.get("ingredient");
                RecipeComponent component = createComponentFromJson(ingredientElement);
                if (component != null) {
                    inputs.add(component);
                } else {
                    throw new JsonSyntaxException("Could not determine ingredient type from JSON: " + ingredientElement);
                }
            } else if (json.has("ingredients")) {
                JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
                for (JsonElement ingredientElement : ingredientsJson) {
                    RecipeComponent component = createComponentFromJson(ingredientElement);
                    if (component != null) {
                        inputs.add(component);
                    } else {
                        throw new JsonSyntaxException("Could not determine ingredient type from JSON: " + ingredientElement);
                    }
                }
            } else {
                throw new JsonSyntaxException("Missing 'ingredient' or 'ingredients', expected to find one for input");
            }

            // 解析输出成分 (支持单个 "result" 或 "results" 数组)
            if (json.has("result")) {
                JsonElement resultElement = json.get("result");
                RecipeComponent component = createComponentFromJson(resultElement);
                if (component != null) {
                    outputs.add(component);
                } else {
                    throw new JsonSyntaxException("Could not determine result type from JSON: " + resultElement);
                }
            } else if (json.has("results")) {
                JsonArray resultsJson = GsonHelper.getAsJsonArray(json, "results");
                for (JsonElement resultElement : resultsJson) {
                    RecipeComponent component = createComponentFromJson(resultElement);
                    if (component != null) {
                        outputs.add(component);
                    } else {
                        throw new JsonSyntaxException("Could not determine result type from JSON: " + resultElement);
                    }
                }
            } else {
                throw new JsonSyntaxException("Missing 'result' or 'results', expected to find one for output");
            }

            return factory.create(recipeId, GsonHelper.getAsString(json, "group", ""), CookingBookCategory.MISC, inputs, outputs);
        }

        @Override
        public @Nullable T fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int inputCount = buf.readVarInt();
            List<RecipeComponent> inputs = new ArrayList<>();
            for (int i = 0; i < inputCount; i++) {
                String type = buf.readUtf();
                RecipeComponent component = createComponentFromNetwork(type);
                if (component == null) {
                    throw new IllegalStateException("Unknown component type from network: " + type);
                }
                component.readFromNetwork(buf);
                inputs.add(component);
            }

            int outputCount = buf.readVarInt();
            List<RecipeComponent> outputs = new ArrayList<>();
            for (int i = 0; i < outputCount; i++) {
                String type = buf.readUtf();
                RecipeComponent component = createComponentFromNetwork(type);
                if (component == null) {
                    throw new IllegalStateException("Unknown component type from network: " + type);
                }
                component.readFromNetwork(buf);
                outputs.add(component);
            }

            return factory.create(id, buf.readUtf(), buf.readEnum(CookingBookCategory.class), inputs, outputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, T recipe) {
            buf.writeUtf(recipe.getGroup());
            buf.writeEnum(recipe.category());

            buf.writeVarInt(recipe.getRecipeInputs().size());
            for (RecipeComponent input : recipe.getRecipeInputs()) {
                buf.writeUtf(input.getType()); // 仍然写入类型以便网络传输
                input.writeToNetwork(buf);
            }

            buf.writeVarInt(recipe.getRecipeOutputs().size());
            for (RecipeComponent output : recipe.getRecipeOutputs()) {
                buf.writeUtf(output.getType()); // 仍然写入类型以便网络传输
                output.writeToNetwork(buf);
            }
        }

        // 修改后的 createComponentFromJson
        protected RecipeComponent createComponentFromJson(JsonElement element) {
            if (element.isJsonObject()) {
                JsonObject jsonObject = element.getAsJsonObject();
                if (jsonObject.has("item")) {
                    ItemComponent component = new ItemComponent(ItemStack.EMPTY);
                    component.readFromJson(jsonObject);
                    return component;
                } else if (jsonObject.has("fluid")) {
                    // 假设 FluidComponent 存在并且有 readFromJson 方法
                    FluidComponent component = new FluidComponent(null); // 你可能需要根据你的 FluidComponent 的构造函数进行调整
                    component.readFromJson(jsonObject);
                    return component;
                }
            } else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                // 如果是简单的字符串，默认作为物品处理
                ItemComponent component = new ItemComponent(ItemStack.EMPTY);
                JsonObject tempJson = new JsonObject();
                tempJson.addProperty("item", element.getAsString());
                component.readFromJson(tempJson);
                return component;
            }
            return null;
        }

        // createComponentFromNetwork 方法保持不变，因为网络传输仍然需要类型信息
        protected abstract RecipeComponent createComponentFromNetwork(String type);

        public interface RecipeFactory<T extends GenericRecipe> {
            T create(ResourceLocation id, String group, CookingBookCategory category, List<RecipeComponent> inputs, List<RecipeComponent> outputs);
        }
    }
}