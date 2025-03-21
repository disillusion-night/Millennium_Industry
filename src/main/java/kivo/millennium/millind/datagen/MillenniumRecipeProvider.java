package kivo.millennium.millind.datagen;

import kivo.millennium.millind.init.MillenniumBlocks;
import kivo.millennium.millind.init.MillenniumItems;
import kivo.millennium.millind.init.MillenniumRecipes;
import kivo.millennium.millind.recipe.CrushingRecipe;
import kivo.millennium.millind.util.RecipeUtils;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

import static kivo.millennium.millind.Main.getRL;

public class MillenniumRecipeProvider extends RecipeProvider {
    public MillenniumRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> pWriter) {
        RecipeUtils.createMineralRecipeBuilder(pWriter, "lead")
                .block(MillenniumBlocks.LEAD_BLOCK.get())
                .rawBlock(MillenniumBlocks.RAW_LEAD_BLOCK.get())
                .ore(MillenniumBlocks.LEAD_ORE.get())
                .deepslateOre(MillenniumBlocks.DEEPSLATE_LEAD_ORE.get())
                .rawOre(MillenniumItems.RAW_LEAD.get())
                .nugget(MillenniumItems.LEAD_NUGGET.get())
                .ingot(MillenniumItems.LEAD_INGOT.get())
                .dust(MillenniumItems.LEAD_DUST.get())
                .withAllRecipe()
                .build();

        RecipeUtils.createMineralRecipeBuilder(pWriter, "aluminum")
                .block(MillenniumBlocks.ALUMINUM_BLOCK.get())
                .rawBlock(MillenniumBlocks.RAW_ALUMINUM_BLOCK.get())
                .ore(MillenniumBlocks.ALUMINUM_ORE.get())
                .deepslateOre(MillenniumBlocks.DEEPSLATE_ALUMINUM_ORE.get())
                .rawOre(MillenniumItems.RAW_ALUMINUM.get())
                .nugget(MillenniumItems.ALUMINUM_NUGGET.get())
                .ingot(MillenniumItems.ALUMINUM_INGOT.get())
                .withBlockRecipe()
                .withIngotFromBlockRecipe()
                .withIngotFromNuggetRecipe()
                .withNuggetFromIngotRecipe()
                .withRawBlockFromRawOreRecipe()
                .withRawOreFromRawBlockRecipe()
                .build();


        RecipeUtils.createMineralRecipeBuilder(pWriter, "steel")
                .block(MillenniumBlocks.STEEL_BLOCK.get())
                .nugget(MillenniumItems.STEEL_NUGGET.get())
                .ingot(MillenniumItems.STEEL_INGOT.get())
                .dust(MillenniumItems.STEEL_DUST.get())
                .withBlockRecipe()
                .withIngotFromBlockRecipe()
                .withIngotFromNuggetRecipe()
                .withNuggetFromIngotRecipe()
                .withDustFromCrushingIngot()
                .build();

        RecipeUtils.createMineralRecipeBuilder(pWriter, "aluminum_alloy")
                .block(MillenniumBlocks.ALUMINUM_ALLOY_BLOCK.get())
                .ingot(MillenniumItems.ALUMINUM_ALLOY_INGOT.get())
                .withBlockRecipe()
                .withIngotFromBlockRecipe()
                .build();

        crushing(pWriter, Items.REDSTONE_BLOCK, RecipeCategory.REDSTONE, Items.REDSTONE, 9, 90);
        crushing(pWriter, Items.COPPER_INGOT, RecipeCategory.MISC, MillenniumItems.COPPER_DUST.get(), 90);
        crushing(pWriter, Items.IRON_INGOT, RecipeCategory.MISC, MillenniumItems.IRON_DUST.get(), 90);
        crushing(pWriter, Items.GOLD_INGOT, RecipeCategory.MISC, MillenniumItems.GOLD_DUST.get(), 90);
        //SimpleSingleRecipeBuilder.crushing()
        //oneToOneConversionRecipe(pWriter, Blocks.REDSTONE_BLOCK.asItem(), new ItemStack(Items.REDSTONE, 9), "a");
    }

    protected static void crushing(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ItemLike pIngredient, RecipeCategory pCategory, ItemLike pResult, int pCookingTime) {
        SimpleSingleRecipeBuilder
                .crushing(Ingredient.of(pIngredient), pCategory, new ItemStack(pResult, 1), 0, pCookingTime)
                .unlockedBy("has_" + getRL(pIngredient).getPath(), has(pIngredient))
                .save(pFinishedRecipeConsumer,  getRL(getRL(pResult).getPath() + "_from_crushing"));
    }

    protected static void crushing(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ItemLike pIngredient, RecipeCategory pCategory, ItemLike pResult, int count, int pCookingTime) {
        SimpleSingleRecipeBuilder
                .crushing(Ingredient.of(pIngredient), pCategory, new ItemStack(pResult, count), 0, pCookingTime)
                .unlockedBy("has_" + getRL(pIngredient).getPath(), has(pIngredient))
                .save(pFinishedRecipeConsumer,  getRL(getRL(pResult).getPath() + "_from_crushing"));
    }
}
