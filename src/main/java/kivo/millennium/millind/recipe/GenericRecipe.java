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
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class GenericRecipe implements Recipe<ExtendedContainer> {
    protected final ResourceLocation id;
    protected final ComponentCollection inputs;
    protected final NonNullList<? extends RecipeComponent> outputs;
    protected int time;

    public GenericRecipe(ResourceLocation id, List<RecipeComponent> inputs, List<? extends RecipeComponent> outputs, int time) {
        this.id = id;
        this.inputs = new ComponentCollection(inputs);
        this.outputs = NonNullList.of(null, outputs.toArray(new RecipeComponent[0]));
        this.time = time;
    }

    @Override
    public boolean matches(ExtendedContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide()) {
            return false;
        }
        return serverMatches(getCollection(pContainer));
    }

    public abstract ComponentCollection getCollection(ExtendedContainer container);

    protected boolean serverMatches(ComponentCollection target){
        return inputs.matches(target);
    }

    @Override
    public ItemStack assemble(ExtendedContainer pContainer, RegistryAccess pRegistryAccess) {
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
        inputs.getRecipeComponents().forEach(input -> {
            if (input instanceof ItemComponent itemComponent){
                ingredients.add(Ingredient.of((itemComponent).getItemStack()));
            }
        });
        return ingredients;
    }

    public ComponentCollection getRecipeInputs() {
        return this.inputs;
    }

    public List<? extends RecipeComponent> getRecipeOutputs() {
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

    public int getTime(){
        return 200;
    }

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
            int time = 200;

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
            if (json.has("time")){
                time = json.get("time").getAsInt();
            }


            return factory.create(recipeId, GsonHelper.getAsString(json, "group", ""), CookingBookCategory.MISC, inputs, outputs, time);
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

            return factory.create(id, buf.readUtf(), buf.readEnum(CookingBookCategory.class), inputs, outputs, buf.readInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, T recipe) {
            buf.writeUtf(recipe.getGroup());
            buf.writeEnum(recipe.category());

            buf.writeVarInt(recipe.getRecipeInputs().getSize());
            List<RecipeComponent> a = recipe.getRecipeInputs().getRecipeComponents();
            a.forEach(input -> {
                buf.writeUtf(input.getType()); // 写入类型以便网络传输
                input.writeToNetwork(buf);
            });

            buf.writeVarInt(recipe.getRecipeOutputs().size());
            for (RecipeComponent output : recipe.getRecipeOutputs()) {
                buf.writeUtf(output.getType()); // 仍然写入类型以便网络传输
                output.writeToNetwork(buf);
            }
            buf.writeInt(recipe.time);
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
                    FluidComponent component = new FluidComponent(FluidStack.EMPTY); // 你可能需要根据你的 FluidComponent 的构造函数进行调整
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

        protected RecipeComponent createComponentFromNetwork(String type) {
            if ("item".equals(type)) {
                return new ItemComponent(ItemStack.EMPTY);
            } else if ("fluid".equals(type)) {
                return new FluidComponent(FluidStack.EMPTY);
            }
            return null;
        }

        public interface RecipeFactory<T extends GenericRecipe> {
            T create(ResourceLocation id, String group, CookingBookCategory category, List<RecipeComponent> inputs, List<RecipeComponent> outputs, int time);
        }
    }
}