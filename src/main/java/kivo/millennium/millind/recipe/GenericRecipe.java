package kivo.millennium.millind.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import kivo.millennium.millind.Main;
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

public abstract class GenericRecipe implements Recipe<NeoContainer> {
    protected final ResourceLocation id;
    protected final NeoContainer inputs;
    protected final NeoContainer outputs;
    protected int energyCost;
    protected int time;

    public GenericRecipe(ResourceLocation id, List<ISlotProxy> inputs, List<ISlotProxy> outputs, int time, int energyCost) {
        this.id = id;
        this.inputs = new NeoContainer(inputs);
        this.outputs = new NeoContainer(outputs);
        this.time = time;
        this.energyCost = energyCost;
    }

    public NeoContainer getInputs(){
        return this.inputs;
    }

    public NeoContainer getOutputs(){
        return this.outputs;
    }

    public boolean process(NeoContainer pInput, NeoContainer pOutput, int energyCost){
        if(pInput.isContain(inputs) && pOutput.hasPlaceFor(outputs)){
            pOutput.tryAdd(outputs);
            pInput.tryRemove(inputs);
            return true;
        }
        return false;
    }

    @Override
    public boolean matches(NeoContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide()) {
            return false;
        }
        return pContainer.isContain(inputs);
    }

    public int getEnergyCost(){
        return this.energyCost;
    }

    @Override
    public @NotNull ItemStack assemble(NeoContainer pContainer, RegistryAccess pRegistryAccess) {
        return outputs.getFirstItem();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        inputs.getSlotProxies().forEach(input -> {
            if (input instanceof ItemProxy itemProxy){
                ingredients.add(Ingredient.of((itemProxy.get())));
            }
        });
        return ingredients;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return outputs.getItem(0);
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
            List<ISlotProxy> inputs = new ArrayList<>();
            List<ISlotProxy> outputs = new ArrayList<>();
            int time = 200;
            int energyCost = 0;

            // 解析输入成分 (支持单个 "ingredient" 或 "ingredients" 数组)
            if (json.has("ingredient")) {
                JsonElement ingredientElement = json.get("ingredient");
                ISlotProxy component = createSlotProxyFromJson(ingredientElement);
                if (component != null) {
                    inputs.add(component);
                } else {
                    throw new JsonSyntaxException("Could not determine ingredient type from JSON: " + ingredientElement);
                }
            } else if (json.has("ingredients")) {
                JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
                for (JsonElement ingredientElement : ingredientsJson) {
                    ISlotProxy component = createSlotProxyFromJson(ingredientElement);
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
                ISlotProxy component = createSlotProxyFromJson(resultElement);
                if (component != null) {
                    outputs.add(component);
                } else {
                    throw new JsonSyntaxException("Could not determine result type from JSON: " + resultElement);
                }
            } else if (json.has("results")) {
                JsonArray resultsJson = GsonHelper.getAsJsonArray(json, "results");
                for (JsonElement resultElement : resultsJson) {
                    ISlotProxy component = createSlotProxyFromJson(resultElement);
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
            List<ISlotProxy> inputs = new ArrayList<>();
            for (int i = 0; i < inputCount; i++) {
                String type = buf.readUtf();
                ISlotProxy component = createComponentFromNetwork(type);
                if (component == null) {
                    throw new IllegalStateException("Unknown component type from network: " + type);
                }
                component.readFromNetwork(buf);
                inputs.add(component);
            }

            int outputCount = buf.readVarInt();
            List<ISlotProxy> outputs = new ArrayList<>();
            for (int i = 0; i < outputCount; i++) {
                String type = buf.readUtf();
                ISlotProxy component = createComponentFromNetwork(type);
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

            buf.writeVarInt(recipe.inputs.getContainerSize());
            List<ISlotProxy> a = recipe.inputs.getSlotProxies();
            a.forEach(input -> {
                buf.writeUtf(input.getType().toString()); // 写入类型以便网络传输
                input.writeToNetwork(buf);
            });

            buf.writeVarInt(recipe.outputs.getContainerSize());
            for (ISlotProxy output : recipe.outputs.getSlotProxies()) {
                buf.writeUtf(output.getType().toString()); // 仍然写入类型以便网络传输
                output.writeToNetwork(buf);
            }
            buf.writeInt(recipe.time);
            buf.writeInt(recipe.energyCost);
        }

        protected ISlotProxy createSlotProxyFromJson(JsonElement element) {
            if (element.isJsonObject()) {
                JsonObject jsonObject = element.getAsJsonObject();
                if (jsonObject.has("item")) {
                    ItemProxy component = new ItemProxy();
                    component.readFromJson(jsonObject);
                    return component;
                } else if (jsonObject.has("fluid")) {
                    FluidProxy component = new FluidProxy();
                    component.readFromJson(jsonObject);
                    return component;
                }
            } else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                // 如果是简单的字符串，默认作为物品处理
                ItemProxy component = new ItemProxy();
                JsonObject tempJson = new JsonObject();
                tempJson.addProperty("item", element.getAsString());
                component.readFromJson(tempJson);
                return component;
            }
            return null;
        }

        protected ISlotProxy createComponentFromNetwork(String type) {
            if ("item".equals(type)) {
                return new ItemProxy();
            } else if ("fluid".equals(type)) {
                return new FluidProxy();
            }
            return null;
        }

        public interface RecipeFactory<T extends GenericRecipe> {
            T create(ResourceLocation id, String group, CookingBookCategory category, List<ISlotProxy> inputs, List<ISlotProxy> outputs, int time, int energyCost);
        }
    }
}