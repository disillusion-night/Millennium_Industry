package kivo.millennium.millind.datagen;

import kivo.millennium.millind.init.*;
import kivo.millennium.millind.recipe.CrushingRecipe;
import kivo.millennium.millind.util.RecipeUtils;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
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

        crushing(pWriter, Items.STONE, RecipeCategory.MISC, Items.GRAVEL, 100, 1000);
        crushing(pWriter, Items.GRAVEL, RecipeCategory.MISC, Items.SAND, 100, 1000);

        crushing(pWriter, MillenniumItems.CRYOLITE.get(), RecipeCategory.MISC, MillenniumItems.CRYOLITE_DUST.get(), 100, 1000);
        crushing(pWriter, MillenniumItems.RAW_ALUMINUM.get(), RecipeCategory.MISC, MillenniumItems.RAW_ALUMINUM_DUST.get(), 100, 1000);

        crushing(pWriter, Items.REDSTONE_BLOCK, RecipeCategory.REDSTONE, Items.REDSTONE, 9, 100);
        crushing(pWriter, Items.COPPER_INGOT, RecipeCategory.MISC, MillenniumItems.COPPER_DUST.get(), 100, 1000);
        crushing(pWriter, Items.IRON_INGOT, RecipeCategory.MISC, MillenniumItems.IRON_DUST.get(), 100, 1000);
        crushing(pWriter, Items.GOLD_INGOT, RecipeCategory.MISC, MillenniumItems.GOLD_DUST.get(), 100, 1000);

        melting(pWriter, Items.ICE, RecipeCategory.MISC, new FluidStack(Fluids.WATER, 1000), 100, 1000);
        melting(pWriter, Items.BLUE_ICE, RecipeCategory.MISC, new FluidStack(Fluids.WATER, 1000), 100, 1000);
        melting(pWriter, Items.PACKED_ICE, RecipeCategory.MISC, new FluidStack(Fluids.WATER, 1000), 100, 1000);

        melting(pWriter, Items.STONE, RecipeCategory.MISC, new FluidStack(Fluids.LAVA, 1000), 100, 1000);
        melting(pWriter, Items.SMOOTH_STONE, RecipeCategory.MISC, new FluidStack(Fluids.LAVA, 1000), 100, 1000);
        melting(pWriter, Items.COBBLESTONE, RecipeCategory.MISC, new FluidStack(Fluids.LAVA, 1000), 100, 1000);

        melting(pWriter, MillenniumItems.ALUMINUM_INGOT.get(), RecipeCategory.MISC, new FluidStack(MillenniumFluids.MOLTEN_ALUMINUM.get(), 100), 100, 1000);
        melting(pWriter, MillenniumBlocks.ALUMINUM_BLOCK.get(), RecipeCategory.MISC, new FluidStack(MillenniumFluids.MOLTEN_ALUMINUM.get(), 900), 100, 1000);

        melting(pWriter, Items.IRON_INGOT, RecipeCategory.MISC, new FluidStack(MillenniumFluids.MOLTEN_IRON.get(), 100), 100, 1000);
        melting(pWriter, Items.IRON_BLOCK, RecipeCategory.MISC, new FluidStack(MillenniumFluids.MOLTEN_IRON.get(), 900), 100, 1000);

        melting(pWriter, MillenniumItems.STEEL_INGOT.get(), RecipeCategory.MISC, new FluidStack(MillenniumFluids.MOLTEN_STEEL.get(), 100), 100, 1000);
        melting(pWriter, MillenniumBlocks.STEEL_BLOCK.get(), RecipeCategory.MISC, new FluidStack(MillenniumFluids.MOLTEN_STEEL.get(), 900), 100, 1000);

        melting(pWriter, MillenniumItems.ALUMINUM_ALLOY_INGOT.get(), RecipeCategory.MISC, new FluidStack(MillenniumFluids.MOLTEN_ALUMINUM_ALLOY.get(), 100), 100, 1000);
        melting(pWriter, MillenniumBlocks.ALUMINUM_ALLOY_BLOCK.get(), RecipeCategory.MISC, new FluidStack(MillenniumFluids.MOLTEN_ALUMINUM_ALLOY.get(), 900), 100, 1000);

        melting(pWriter, MillenniumItems.CRYOLITE_DUST.get(), RecipeCategory.MISC, new FluidStack(MillenniumFluids.MOLTEN_CRYOLITE.get(), 100), 100, 1000);

        fusion(pWriter, MillenniumItems.RAW_ALUMINUM_DUST.get(), new FluidStack(MillenniumFluids.MOLTEN_CRYOLITE.get(), 100), RecipeCategory.MISC, new FluidStack(MillenniumFluids.RAW_MOLTEN_ALUMINUM.get(), 100), 200, 1000);

        fusion(pWriter, MillenniumItems.CARBON_DUST.get(), new FluidStack(MillenniumFluids.MOLTEN_IRON.get(), 100), RecipeCategory.MISC, new FluidStack(MillenniumFluids.MOLTEN_STEEL.get(), 100), 200, 1000);

        pressing(pWriter, MillenniumItems.ALUMINUM_INGOT.get(), new ItemStack(MillenniumItems.ALUMINUM_ALLOY_INGOT.get(), 1), RecipeCategory.MISC, MillenniumItems.ALUMINUM_ALLOY_PANEL.get(), 200, 4000);

        crystallizing(pWriter, new FluidStack(MillenniumFluids.MOLTEN_IRON.get(), 100), Items.ICE, RecipeCategory.MISC, Items.IRON_INGOT, 100, 1000);

        crystallizing(pWriter, new FluidStack(MillenniumFluids.MOLTEN_STEEL.get(), 100), Items.ICE, RecipeCategory.MISC, MillenniumItems.STEEL_INGOT.get(), 100, 1000);

        crystallizing(pWriter, new FluidStack(MillenniumFluids.MOLTEN_ALUMINUM_ALLOY.get(), 100), Items.ICE, RecipeCategory.MISC, MillenniumItems.ALUMINUM_ALLOY_INGOT.get(), 100, 1000);

        crystallizing(pWriter, new FluidStack(MillenniumFluids.MOLTEN_ALUMINUM.get(), 100), Items.ICE, RecipeCategory.MISC, MillenniumItems.ALUMINUM_INGOT.get(), 100, 1000);

        //SimpleSingleRecipeBuilder.crushing()
        //oneToOneConversionRecipe(pWriter, Blocks.REDSTONE_BLOCK.asItem(), new ItemStack(Items.REDSTONE, 9), "a");
    }


    protected static void crystallizing(Consumer<FinishedRecipe> pFinishedRecipeConsumer,FluidStack fluidStack, Item model, RecipeCategory pCategory, ItemLike pResult, int pCookingTime, int energy) {
        SimpleSingleRecipeBuilder
                .crystallizing(fluidStack, model, pCategory, new ItemStack(pResult, 1), 0, pCookingTime,energy)
                .unlockedBy("has_" + getRL(pResult.asItem()).getPath(), has(pResult.asItem()))
                .save(pFinishedRecipeConsumer,  getRL(getRL(pResult).getPath() + "_from_crystallizing_" + getRL(fluidStack.getFluid()).getPath()));
    }


    protected static void pressing(Consumer<FinishedRecipe> pFinishedRecipeConsumer, Item model, ItemStack itemStack, RecipeCategory pCategory, ItemLike pResult, int pCookingTime, int energy) {
        SimpleSingleRecipeBuilder
                .pressing(model, itemStack, pCategory, new ItemStack(pResult, 1), 0, pCookingTime,energy)
                .unlockedBy("has_" + getRL(itemStack.getItem()).getPath(), has(itemStack.getItem()))
                .save(pFinishedRecipeConsumer,  getRL(getRL(pResult).getPath() + "_from_pressing_" + getRL(itemStack.getItem()).getPath()));
    }

    protected static void fusion(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ItemLike pIngredientItem, FluidStack pIngredientFluid, RecipeCategory pCategory, FluidStack pResult, int pCookingTime, int energy) {
        SimpleSingleRecipeBuilder
                .fusion(new ItemStack(pIngredientItem),pIngredientFluid, pCategory, pResult, 0, pCookingTime, energy)
                .unlockedBy("has_" + getRL(pIngredientItem).getPath(), has(pIngredientItem))
                .save(pFinishedRecipeConsumer,  getRL(getRL(pResult.getFluid()).getPath() + "_from_fusion_" + getRL(pIngredientItem).getPath()+ "_and_" + getRL(pIngredientFluid.getFluid()).getPath()));
    }

    protected static void melting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ItemLike pIngredient, RecipeCategory pCategory, FluidStack pResult, int pCookingTime, int energy) {
        SimpleSingleRecipeBuilder
                .melting(new ItemStack(pIngredient), pCategory, pResult, 0, pCookingTime, energy)
                .unlockedBy("has_" + getRL(pIngredient).getPath(), has(pIngredient))
                .save(pFinishedRecipeConsumer,  getRL(getRL(pResult.getFluid()).getPath() + "_from_melting_" + getRL(pIngredient).getPath()));
    }


    protected static void crushing(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ItemLike pIngredient, RecipeCategory pCategory, ItemLike pResult, int pCookingTime, int energy) {
        SimpleSingleRecipeBuilder
                .crushing(new ItemStack(pIngredient), pCategory, new ItemStack(pResult, 1), 0, pCookingTime,energy)
                .unlockedBy("has_" + getRL(pIngredient).getPath(), has(pIngredient))
                .save(pFinishedRecipeConsumer,  getRL(getRL(pResult).getPath() + "_from_crushing_" + getRL(pIngredient).getPath()));
    }

    protected static void crushing(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ItemLike pIngredient, RecipeCategory pCategory, ItemLike pResult, int count, int pCookingTime, int energy) {
        SimpleSingleRecipeBuilder
                .crushing(new ItemStack(pIngredient), pCategory, new ItemStack(pResult, count), 0, pCookingTime, energy)
                .unlockedBy("has_" + getRL(pIngredient).getPath(), has(pIngredient))
                .save(pFinishedRecipeConsumer,  getRL(getRL(pResult).getPath() + "_from_crushing_" + getRL(pIngredient).getPath()));
    }
}
