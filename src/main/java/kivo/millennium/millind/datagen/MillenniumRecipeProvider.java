package kivo.millennium.millind.datagen;

import kivo.millennium.millind.util.RecipeUtils;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;

import java.util.function.Consumer;

public class MillenniumRecipeProvider extends RecipeProvider {
    public MillenniumRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        //new RecipeUtils.MineralRecipeBuilder(pWriter, "lead").block().rawOre().nugget()
    }
}
