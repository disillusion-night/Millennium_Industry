package kivo.millennium.millind.datagen;

import kivo.millennium.millind.init.MillenniumBlocks;
import kivo.millennium.millind.init.MillenniumItems;
import kivo.millennium.millind.util.RecipeUtils;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import org.jetbrains.annotations.NotNull;

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

    }
}
