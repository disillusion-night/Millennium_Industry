package kivo.millennium.milltek.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import kivo.millennium.milltek.Main;
import kivo.millennium.milltek.init.*;
import kivo.millennium.milltek.util.RecipeUtils;

import static kivo.millennium.milltek.Main.getKey;

import java.util.function.Consumer;

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
                .nugget(MillenniumItems.ALUMINUM_ALLOY_NUGGET.get())
                .withBlockRecipe()
                .withIngotFromBlockRecipe()
                .build();


        RecipeUtils.createMineralRecipeBuilder(pWriter, "titanium_alloy")
                .block(MillenniumBlocks.TITANIUM_ALLOY_BLOCK.get())
                .ingot(MillenniumItems.TITANIUM_ALLOY_INGOT.get())
                .nugget(MillenniumItems.TITANIUM_ALLOY_NUGGET.get())
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
        
        electrolyzing(pWriter, new FluidStack(Fluids.WATER, 1000), RecipeCategory.MISC,
            new kivo.millennium.milltek.gas.GasStack(MillenniumGases.HYDROGEN.get(), 1000),
            new kivo.millennium.milltek.gas.GasStack(MillenniumGases.OXYGEN.get(), 500),
            200, 2000);
    }

    

    protected static void crystallizing(Consumer<FinishedRecipe> pFinishedRecipeConsumer,FluidStack fluidStack, Item model, RecipeCategory pCategory, ItemLike pResult, int pCookingTime, int energy) {
        SimpleSingleRecipeBuilder
                .crystallizing(fluidStack, model, pCategory, new ItemStack(pResult, 1), 0, pCookingTime,energy)
                .unlockedBy("has_" + Main.getKey(pResult.asItem()).getPath(), has(pResult.asItem()))
                .save(pFinishedRecipeConsumer,  Main.getRL(Main.getKey(pResult).getPath() + "_from_crystallizing_" + getKey(fluidStack.getFluid()).getPath()));
    }


    protected static void pressing(Consumer<FinishedRecipe> pFinishedRecipeConsumer, Item model, ItemStack itemStack, RecipeCategory pCategory, ItemLike pResult, int pCookingTime, int energy) {
        SimpleSingleRecipeBuilder
                .pressing(model, itemStack, pCategory, new ItemStack(pResult, 1), 0, pCookingTime,energy)
                .unlockedBy("has_" + Main.getKey(itemStack.getItem()).getPath(), has(itemStack.getItem()))
                .save(pFinishedRecipeConsumer,  Main.getRL(Main.getKey(pResult).getPath() + "_from_pressing_" + Main.getKey(itemStack.getItem()).getPath()));
    }

    protected static void fusion(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ItemLike pIngredientItem, FluidStack pIngredientFluid, RecipeCategory pCategory, FluidStack pResult, int pCookingTime, int energy) {
        SimpleSingleRecipeBuilder
                .fusion(new ItemStack(pIngredientItem),pIngredientFluid, pCategory, pResult, 0, pCookingTime, energy)
                .unlockedBy("has_" + Main.getKey(pIngredientItem).getPath(), has(pIngredientItem))
                .save(pFinishedRecipeConsumer,  Main.getRL(getKey(pResult.getFluid()).getPath() + "_from_fusion_" + Main.getKey(pIngredientItem).getPath()+ "_and_" + getKey(pIngredientFluid.getFluid()).getPath()));
    }

    protected static void melting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ItemLike pIngredient, RecipeCategory pCategory, FluidStack pResult, int pCookingTime, int energy) {
        SimpleSingleRecipeBuilder
                .melting(new ItemStack(pIngredient), pCategory, pResult, 0, pCookingTime, energy)
                .unlockedBy("has_" + Main.getKey(pIngredient).getPath(), has(pIngredient))
                .save(pFinishedRecipeConsumer,  Main.getRL(getKey(pResult.getFluid()).getPath() + "_from_melting_" + Main.getKey(pIngredient).getPath()));
    }


    protected static void crushing(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ItemLike pIngredient, RecipeCategory pCategory, ItemLike pResult, int pCookingTime, int energy) {
        SimpleSingleRecipeBuilder
                .crushing(new ItemStack(pIngredient), pCategory, new ItemStack(pResult, 1), 0, pCookingTime,energy)
                .unlockedBy("has_" + Main.getKey(pIngredient).getPath(), has(pIngredient))
                .save(pFinishedRecipeConsumer,  Main.getRL(Main.getKey(pResult).getPath() + "_from_crushing_" + Main.getKey(pIngredient).getPath()));
    }

    protected static void crushing(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ItemLike pIngredient, RecipeCategory pCategory, ItemLike pResult, int count, int pCookingTime, int energy) {
        SimpleSingleRecipeBuilder
                .crushing(new ItemStack(pIngredient), pCategory, new ItemStack(pResult, count), 0, pCookingTime, energy)
                .unlockedBy("has_" + Main.getKey(pIngredient).getPath(), has(pIngredient))
                .save(pFinishedRecipeConsumer,  Main.getRL(Main.getKey(pResult).getPath() + "_from_crushing_" + Main.getKey(pIngredient).getPath()));
    }

    protected static void electrolyzing(Consumer<FinishedRecipe> pFinishedRecipeConsumer, FluidStack fluidInput, RecipeCategory pCategory, kivo.millennium.milltek.gas.GasStack gasResult1, kivo.millennium.milltek.gas.GasStack gasResult2, int pCookingTime, int energy) {
        SimpleSingleRecipeBuilder
                .electrolyzing(fluidInput, pCategory, gasResult1, gasResult2, 0, pCookingTime, energy)
                .unlockedBy("has_" + Main.getKey(MillenniumBlocks.ELECTROLYZER.get()), has(MillenniumBlocks.ELECTROLYZER.get()))
                .save(pFinishedRecipeConsumer, Main.getRL(gasResult1.getGas().getRegistryName().getPath() + "_and_" + gasResult2.getGas().getRegistryName().getPath() + "_from_electrolyzing_" + getKey(fluidInput.getFluid()).getPath()));
    }
}
