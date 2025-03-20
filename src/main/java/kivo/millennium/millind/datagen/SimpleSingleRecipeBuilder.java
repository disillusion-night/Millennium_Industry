package kivo.millennium.millind.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;

import kivo.millennium.millind.Main;
import kivo.millennium.millind.init.MillenniumRecipes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import static kivo.millennium.millind.Main.getRL;

public class SimpleSingleRecipeBuilder implements RecipeBuilder {
    private final RecipeCategory category;
    private final CookingBookCategory bookCategory;
    private final ItemStack result;
    private final Ingredient ingredient;
    private final float experience;
    private final int cookingTime;
    private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
    @Nullable
    private String group;
    private final RecipeSerializer<? extends Recipe> serializer;

    private SimpleSingleRecipeBuilder(RecipeCategory pCategory, CookingBookCategory pBookCategory, ItemStack pResult, Ingredient pIngredient, float pExperience, int pCookingTime, RecipeSerializer<? extends Recipe> pSerializer) {
        this.category = pCategory;
        this.bookCategory = pBookCategory;
        this.result = pResult;
        this.ingredient = pIngredient;
        this.experience = pExperience;
        this.cookingTime = pCookingTime;
        this.serializer = pSerializer;
    }
    /*
    public static SimpleSingleRecipeBuilder generic(Ingredient pIngredient, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, RecipeSerializer<? extends AbstractCookingRecipe> pSerializer) {
        return new SimpleSingleRecipeBuilder(pCategory, determineRecipeCategory(pSerializer, pResult), pResult, pIngredient, pExperience, pCookingTime, pSerializer);
    }*/

    public static SimpleSingleRecipeBuilder crushing(Ingredient pIngredient, RecipeCategory pCategory, ItemStack pResult, float pExperience, int pCookingTime) {
        return new SimpleSingleRecipeBuilder(pCategory, CookingBookCategory.MISC, pResult, pIngredient, pExperience, pCookingTime, MillenniumRecipes.CRUSHING_RECIPE.get());
    }

    public SimpleSingleRecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    public SimpleSingleRecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    public Item getResult() {
        return this.result.getItem();
    }

    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        this.ensureValid(pRecipeId);
        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId)).rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(RequirementsStrategy.OR);
        pFinishedRecipeConsumer.accept(new SimpleSingleRecipeBuilder.Result(pRecipeId, this.group == null ? "" : this.group, this.bookCategory, this.ingredient, this.result, this.experience, this.cookingTime, this.advancement, pRecipeId.withPrefix("recipes/" + this.category.getFolderName() + "/"), this.serializer));
    }
    /*
    private static CookingBookCategory determineSmeltingRecipeCategory(ItemLike pResult) {
        if (pResult.asItem().isEdible()) {
            return CookingBookCategory.FOOD;
        } else {
            return pResult.asItem() instanceof BlockItem ? CookingBookCategory.BLOCKS : CookingBookCategory.MISC;
        }
    }

    private static CookingBookCategory determineBlastingRecipeCategory(ItemLike pResult) {
        return pResult.asItem() instanceof BlockItem ? CookingBookCategory.BLOCKS : CookingBookCategory.MISC;
    }

    private static CookingBookCategory determineRecipeCategory(RecipeSerializer<? extends AbstractCookingRecipe> pSerializer, ItemLike pResult) {
        if (pSerializer == RecipeSerializer.SMELTING_RECIPE) {
            return determineSmeltingRecipeCategory(pResult);
        } else if (pSerializer == RecipeSerializer.BLASTING_RECIPE) {
            return determineBlastingRecipeCategory(pResult);
        } else if (pSerializer != RecipeSerializer.SMOKING_RECIPE && pSerializer != RecipeSerializer.CAMPFIRE_COOKING_RECIPE) {
            throw new IllegalStateException("Unknown cooking recipe type");
        } else {
            return CookingBookCategory.FOOD;
        }
    }*/

    /**
     * Makes sure that this obtainable.
     */
    private void ensureValid(ResourceLocation pId) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + pId);
        }
    }

    /**
     *  copy form forge,I don't know
     */
    static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final String group;
        private final CookingBookCategory category;
        private final Ingredient ingredient;
        private final ItemStack result;
        private final float experience;
        private final int cookingTime;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final RecipeSerializer<? extends Recipe> serializer;

        public Result(ResourceLocation pId, String pGroup, CookingBookCategory pCategory, Ingredient pIngredient, ItemStack pResult, float pExperience, int pCookingTime, Advancement.Builder pAdvancement, ResourceLocation pAdvancementId, RecipeSerializer<? extends Recipe> pSerializer) {
            this.id = pId;
            this.group = pGroup;
            this.category = pCategory;
            this.ingredient = pIngredient;
            this.result = pResult;
            this.experience = pExperience;
            this.cookingTime = pCookingTime;
            this.advancement = pAdvancement;
            this.advancementId = pAdvancementId;
            this.serializer = pSerializer;
        }

        public void serializeRecipeData(JsonObject pJson) {
            if (!this.group.isEmpty()) {
                pJson.addProperty("group", this.group);
            }
            pJson.addProperty("category", this.category.getSerializedName());
            pJson.add("ingredient", this.ingredient.toJson());
            if(result.getCount() == 1) pJson.addProperty("result", getRL(this.result.getItem()).toString());
            else {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("count",  this.result.getCount());
                jsonObject.addProperty("item",  getRL(this.result.getItem()).toString());
                pJson.add("result", jsonObject);
            }
            pJson.addProperty("experience", this.experience);
            pJson.addProperty("cookingtime", this.cookingTime);
        }

        public RecipeSerializer<?> getType() {
            return this.serializer;
        }

        /**
         * Gets the ID for the recipe.
         */
        public ResourceLocation getId() {
            return this.id;
        }

        /**
         * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
         */
        @Nullable
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        /**
         * Gets the ID for the advancement associated with this recipe. Should not be null if {@link #getAdvancementJson}
         * is non-null.
         */
        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}