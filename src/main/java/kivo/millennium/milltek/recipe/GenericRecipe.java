package kivo.millennium.milltek.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import kivo.millennium.milltek.Main;
import kivo.millennium.milltek.capability.CapabilityType;
import kivo.millennium.milltek.recipe.component.FluidComponent;
import kivo.millennium.milltek.recipe.component.ItemComponent;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public abstract class GenericRecipe implements Recipe<ProxyContainer> {
    protected final ResourceLocation id;
    protected final List<RecipeComponent> inputs;
    protected final List<RecipeComponent> outputs;
    protected int energyCost;
    protected int time;

    public GenericRecipe(ResourceLocation id, List<RecipeComponent> inputs, List<RecipeComponent> outputs, int time, int energyCost) {
        this.id = id;
        this.inputs = inputs;
        this.outputs = outputs;
        this.time = time;
        this.energyCost = energyCost;
    }

    public List<RecipeComponent> getInputs(){
        return this.inputs;
    }

    public List<RecipeComponent> getOutputs(){
        return this.outputs;
    }

    public boolean canProcess(ProxyContainer pOutput){
        return pOutput.hasPlaceFor(outputs);
    }

    public boolean process(ProxyContainer pInput, ProxyContainer pOutput, int energyCost){
        if(pInput.isContain(inputs) && pOutput.hasPlaceFor(outputs)){
            pOutput.tryAdd(outputs);
            pInput.tryRemove(inputs);
            pOutput.clear();
            pInput.clear();
            return true;
        }
        pOutput.clear();
        pInput.clear();
        return false;
    }

    @Override
    public boolean matches(ProxyContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide()) {
            return false;
        }
        return pContainer.isContain(inputs);
    }

    public int getEnergyCost(){
        return this.energyCost;
    }

    @Override
    public @NotNull ItemStack assemble(ProxyContainer pContainer, RegistryAccess pRegistryAccess) {
        for (int i = 0; i < outputs.size(); i++){
            if (outputs.get(i) instanceof  ItemComponent itemComponent) return itemComponent.get();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        for (int i = 0; i < outputs.size(); i++){
            if (outputs.get(i) instanceof ItemComponent itemComponent) ingredients.add(Ingredient.of(itemComponent.get()));
        }
        return ingredients;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
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
            int energyCost = 0;

            // 解析输入成分 (支持单个 "ingredient" 或 "ingredients" 数组)
            if (json.has("ingredient")) {
                JsonElement ingredientElement = json.get("ingredient");
                RecipeComponent component = createSlotProxyFromJson(ingredientElement);
                if (component != null) {
                    inputs.add(component);
                } else {
                    throw new JsonSyntaxException("Could not determine ingredient type from JSON: " + ingredientElement);
                }
            } else if (json.has("ingredients")) {
                JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
                for (JsonElement ingredientElement : ingredientsJson) {
                    RecipeComponent component = createSlotProxyFromJson(ingredientElement);
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
                RecipeComponent component = createSlotProxyFromJson(resultElement);
                if (component != null) {
                    outputs.add(component);
                } else {
                    throw new JsonSyntaxException("Could not determine result type from JSON: " + resultElement);
                }
            } else if (json.has("results")) {
                JsonArray resultsJson = GsonHelper.getAsJsonArray(json, "results");
                for (JsonElement resultElement : resultsJson) {
                    RecipeComponent component = createSlotProxyFromJson(resultElement);
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
            }else {
                throw new JsonSyntaxException("Missing 'time', expected to find a int");
            }

            energyCost = GsonHelper.getAsInt(json, "energy", 0);


            return factory.create(recipeId, GsonHelper.getAsString(json, "group", ""), CookingBookCategory.MISC, inputs, outputs, time, energyCost);
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

            return factory.create(id, buf.readUtf(), buf.readEnum(CookingBookCategory.class), inputs, outputs, buf.readInt(), buf.readInt());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, T recipe) {
            buf.writeUtf(recipe.getGroup());
            buf.writeEnum(recipe.category());

            buf.writeVarInt(recipe.inputs.size());
            List<RecipeComponent> a = recipe.inputs;
            a.forEach(input -> {
                buf.writeUtf(input.getType().toString()); // 写入类型以便网络传输
                input.writeToNetwork(buf);
            });

            buf.writeVarInt(recipe.outputs.size());
            for (RecipeComponent output : recipe.outputs) {
                buf.writeUtf(output.getType().toString()); // 仍然写入类型以便网络传输
                output.writeToNetwork(buf);
            }
            buf.writeInt(recipe.time);
            buf.writeInt(recipe.energyCost);
        }

        protected RecipeComponent createSlotProxyFromJson(JsonElement element) {
            if (element.isJsonObject()) {
                JsonObject jsonObject = element.getAsJsonObject();
                if (jsonObject.has("item")) {
                    ItemComponent component = new ItemComponent(jsonObject);
                    return component;
                } else if (jsonObject.has("fluid")) {
                    FluidComponent component = new FluidComponent(jsonObject);
                    return component;
                }
            } else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                // 如果是简单的字符串，默认作为物品处理
                JsonObject tempJson = new JsonObject();
                tempJson.addProperty("item", element.getAsString());
                return new ItemComponent(tempJson);
            }
            return null;
        }

        protected RecipeComponent createComponentFromNetwork(String type) {
            if (CapabilityType.ITEM.toString().equals(type)) {
                return new ItemComponent(ItemStack.EMPTY);
            } else if (CapabilityType.FLUID.toString().equals(type)) {
                return new FluidComponent(FluidStack.EMPTY);
            }
            return null;
        }

        public interface RecipeFactory<T extends GenericRecipe> {
            T create(ResourceLocation id, String group, CookingBookCategory category, List<RecipeComponent> inputs, List<RecipeComponent> outputs, int time, int energyCost);
        }
    }
}