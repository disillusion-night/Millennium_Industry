package kivo.millennium.milltek.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import kivo.millennium.milltek.container.Device.ItemProxy;
import kivo.millennium.milltek.gas.GasStack;
import kivo.millennium.milltek.init.MillenniumRecipes;
import kivo.millennium.milltek.recipe.*;
import kivo.millennium.milltek.recipe.component.FluidComponent;
import kivo.millennium.milltek.recipe.component.ItemComponent;
import kivo.millennium.milltek.recipe.component.GasComponent;
import java.util.function.Consumer;
import javax.annotation.Nullable;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.fluids.FluidStack;

import static net.minecraft.data.recipes.RecipeBuilder.ROOT_RECIPE_ADVANCEMENT;

public class SimpleSingleRecipeBuilder {
    private final RecipeCategory category;
    private final CookingBookCategory bookCategory;
    private final NonNullList<RecipeComponent> results;
    private final NonNullList<RecipeComponent> ingredients;
    private final float experience;
    private final int time;
    private final int energy;
    private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
    @Nullable
    private String group;
    private final RecipeSerializer<? extends Recipe> serializer;

    private SimpleSingleRecipeBuilder(RecipeCategory pCategory, CookingBookCategory pBookCategory, NonNullList<RecipeComponent> pResult, NonNullList<RecipeComponent> pIngredient, float pExperience, int pTime,int pEnergy, RecipeSerializer<? extends Recipe> pSerializer) {
        this.category = pCategory;
        this.bookCategory = pBookCategory;
        this.results = pResult;
        this.ingredients = pIngredient;
        this.experience = pExperience;
        this.time = pTime;
        this.energy = pEnergy;
        this.serializer = pSerializer;
    }
    public static SimpleSingleRecipeBuilder crystallizing(FluidStack fluidStack,  Item model, RecipeCategory pCategory, ItemStack pResult, float pExperience, int pCookingTime, int pEnergyCost) {
        NonNullList<RecipeComponent> recipeComponents = NonNullList.create();
        recipeComponents.add(new FluidComponent(fluidStack));
        ItemComponent a = new ItemComponent(new ItemStack(model, 1));
        recipeComponents.add(a);
        return new SimpleSingleRecipeBuilder(pCategory, CookingBookCategory.MISC, NonNullList.withSize(1, new ItemComponent(pResult)),recipeComponents, pExperience, pCookingTime,pEnergyCost,  MillenniumRecipes.CRYSTALLIZING_RECIPE.get());
    }

    public static SimpleSingleRecipeBuilder pressing(Item model, ItemStack pIngredient2, RecipeCategory pCategory, ItemStack pResult, float pExperience, int pCookingTime, int pEnergyCost) {
        NonNullList<RecipeComponent> recipeComponents = NonNullList.create();
        ItemComponent a = new ItemComponent(new ItemStack(model, 1));
        a.setNoCost();
        recipeComponents.add(a);
        recipeComponents.add(new ItemComponent(pIngredient2));
        return new SimpleSingleRecipeBuilder(pCategory, CookingBookCategory.MISC, NonNullList.withSize(1, new ItemComponent(pResult)),recipeComponents, pExperience, pCookingTime,pEnergyCost,  MillenniumRecipes.PRESSING_RECIPE.get());
    }

    public static SimpleSingleRecipeBuilder resonance(ItemStack pIngredient, RecipeCategory pCategory, ItemStack pResult, float pExperience, int pCookingTime, int pEnergyCost) {
        return new SimpleSingleRecipeBuilder(pCategory, CookingBookCategory.MISC, NonNullList.withSize(1, new ItemComponent(pResult)),NonNullList.withSize(1, new ItemComponent(pIngredient)), pExperience, pCookingTime,pEnergyCost,  MillenniumRecipes.RESONANCE_RECIPE.get());
    }

    public static SimpleSingleRecipeBuilder fusion(ItemStack pIngredientItem, FluidStack pIngredientFluid, RecipeCategory pCategory, FluidStack pResult, float pExperience, int pCookingTime, int pEnergyCost) {
        NonNullList<RecipeComponent> recipeComponents = NonNullList.create();
        recipeComponents.add(new FluidComponent(pIngredientFluid));
        recipeComponents.add(new ItemComponent(pIngredientItem));
        return new SimpleSingleRecipeBuilder(pCategory, CookingBookCategory.MISC, NonNullList.withSize(1, new FluidComponent(pResult)), recipeComponents, pExperience, pCookingTime,pEnergyCost, MillenniumRecipes.FUSION_RECIPE.get());
    }

    public static SimpleSingleRecipeBuilder crushing(ItemStack pIngredient, RecipeCategory pCategory, ItemStack pResult, float pExperience, int pCookingTime, int pEnergyCost) {
        return new SimpleSingleRecipeBuilder(pCategory, CookingBookCategory.MISC, NonNullList.withSize(1, new ItemComponent(pResult)),NonNullList.withSize(1, new ItemComponent(pIngredient)), pExperience, pCookingTime,pEnergyCost, MillenniumRecipes.CRUSHING_RECIPE.get());
    }

    public static SimpleSingleRecipeBuilder melting(ItemStack pIngredient, RecipeCategory pCategory, FluidStack pResult, float pExperience, int pCookingTime, int pEnergyCost) {
        return new SimpleSingleRecipeBuilder(pCategory, CookingBookCategory.MISC, NonNullList.withSize(1, new FluidComponent(pResult)),NonNullList.withSize(1, new ItemComponent(pIngredient)), pExperience, pCookingTime,pEnergyCost, MillenniumRecipes.MELTING_RECIPE.get());
    }

    public static SimpleSingleRecipeBuilder electrolyzing(FluidStack fluidInput, RecipeCategory pCategory, GasStack gasResult1, GasStack gasResult2, float pExperience, int pCookingTime, int pEnergyCost) {
        NonNullList<RecipeComponent> recipeComponents = NonNullList.create();
        recipeComponents.add(new FluidComponent(fluidInput));
        NonNullList<RecipeComponent> results = NonNullList.create();
        results.add(new GasComponent(gasResult1));
        results.add(new GasComponent(gasResult2));
        return new SimpleSingleRecipeBuilder(pCategory, CookingBookCategory.MISC, results, recipeComponents, pExperience, pCookingTime, pEnergyCost, MillenniumRecipes.ELECTROLYZING_RECIPE.get());
    }

    public SimpleSingleRecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    public SimpleSingleRecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    //@Override
    public Item getResult() {
        return getResultItem();
    }

    public Item getResultItem() {
        if (this.results.get(0) instanceof ItemProxy proxy) {
            return proxy.get().getItem();
        }
        return null; // Or handle FluidStack differently if needed
    }

    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        this.ensureValid(pRecipeId);
        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId)).rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(RequirementsStrategy.OR);
        pFinishedRecipeConsumer.accept(new Result(pRecipeId, this.group == null ? "" : this.group, this.bookCategory, this.ingredients, this.results, this.experience, this.time,this.energy, this.advancement, pRecipeId.withPrefix("recipes/" + this.category.getFolderName() + "/"), this.serializer));
    }

    private void ensureValid(ResourceLocation pId) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + pId);
        }
    }

    static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final String group;
        private final CookingBookCategory category;
        private final NonNullList<RecipeComponent> ingredients;
        private final NonNullList<RecipeComponent> results;
        private final float experience;
        private final int time;
        private final int energy;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final RecipeSerializer<? extends Recipe> serializer;

        public Result(ResourceLocation pId, String pGroup, CookingBookCategory pCategory, NonNullList<RecipeComponent> pIngredient, NonNullList<RecipeComponent> pResult, float pExperience, int pTime,int pEnergy, Advancement.Builder pAdvancement, ResourceLocation pAdvancementId, RecipeSerializer<? extends Recipe> pSerializer) {
            this.id = pId;
            this.group = pGroup;
            this.category = pCategory;
            this.ingredients = pIngredient;
            this.results = pResult;
            this.experience = pExperience;
            this.time = pTime;
            this.energy = pEnergy;
            this.advancement = pAdvancement;
            this.advancementId = pAdvancementId;
            this.serializer = pSerializer;
        }

        public void serializeRecipeData(JsonObject pJson) {
            if (!this.group.isEmpty()) {
                pJson.addProperty("group", this.group);
            }
            pJson.addProperty("category", this.category.getSerializedName());

            if(this.ingredients.size() == 1){
                pJson.add("ingredient", this.ingredients.get(0).toJson());
            } else {
                JsonArray jsonArray = new JsonArray();
                this.ingredients.forEach(recipeComponent -> {
                    jsonArray.add(recipeComponent.toJson());
                });
                pJson.add("ingredients", jsonArray);
            }

            if(this.results.size() == 1){
                pJson.add("result", this.results.get(0).toJson());
            }
            else {
                JsonArray jsonArray = new JsonArray();
                this.results.forEach(recipeComponent -> {
                    jsonArray.add(recipeComponent.toJson());
                });
                pJson.add("results", jsonArray);
            }

            pJson.addProperty("experience", this.experience);
            pJson.addProperty("time", this.time);
            pJson.addProperty("energy", this.energy);
        }

        public RecipeSerializer<?> getType() {
            return this.serializer;
        }

        public ResourceLocation getId() {
            return this.id;
        }

        @Nullable
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}