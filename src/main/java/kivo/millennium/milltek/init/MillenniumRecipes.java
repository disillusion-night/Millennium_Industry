package kivo.millennium.milltek.init;

import static kivo.millennium.milltek.Main.MODID;
import static kivo.millennium.milltek.Main.getKey;

import kivo.millennium.milltek.recipe.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MillenniumRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    public static final RegistryObject<RecipeSerializer<CrushingRecipe>> CRUSHING_RECIPE =
            RECIPE_SERIALIZERS.register("crushing", () -> CrushingRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<MeltingRecipe>> MELTING_RECIPE =
            RECIPE_SERIALIZERS.register("melting", () -> MeltingRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<FusionRecipe>> FUSION_RECIPE =
            RECIPE_SERIALIZERS.register("fusion", () -> FusionRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<ResonanceRecipe>> RESONANCE_RECIPE =
            RECIPE_SERIALIZERS.register("resonance", () -> ResonanceRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<PressingRecipe>> PRESSING_RECIPE =
            RECIPE_SERIALIZERS.register("pressing", () -> PressingRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<CrystallizingRecipe>> CRYSTALLIZING_RECIPE =
            RECIPE_SERIALIZERS.register("crystallizing", () -> CrystallizingRecipe.Serializer.INSTANCE);
    
    public static final RegistryObject<RecipeSerializer<ElectrolyzingRecipe>> ELECTROLYZING_RECIPE =    
            RECIPE_SERIALIZERS.register("electrolyzing", () -> ElectrolyzingRecipe.Serializer.INSTANCE);
}
