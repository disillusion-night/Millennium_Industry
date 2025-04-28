package kivo.millennium.millind.init;

import kivo.millennium.millind.recipe.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static kivo.millennium.millind.Main.MODID;
import static kivo.millennium.millind.Main.getKey;

public class MillenniumRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    public static final RegistryObject<RecipeSerializer<CrushingRecipe>> CRUSHING_RECIPE =
            SERIALIZERS.register("crushing", () -> CrushingRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<MeltingRecipe>> MELTING_RECIPE =
            SERIALIZERS.register("melting", () -> MeltingRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<FusionRecipe>> FUSION_RECIPE =
            SERIALIZERS.register("fusion", () -> FusionRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<ResonanceRecipe>> RESONANCE_RECIPE =
            SERIALIZERS.register("resonance", () -> ResonanceRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<PressingRecipe>> PRESSING_RECIPE =
            SERIALIZERS.register("pressing", () -> PressingRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<CrystallizingRecipe>> CRYSTALLIZING_RECIPE =
            SERIALIZERS.register("crystallizing", () -> CrystallizingRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
