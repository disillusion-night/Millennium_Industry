package kivo.millennium.millind.init;

import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static kivo.millennium.millind.Main.MODID;

public class MillenniumRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPE = DeferredRegister.create(ForgeRegistries.Keys.RECIPE_TYPES.registry(), MODID);

    //public static final RegistryObject<RecipeType<CrusherRecipe>> ICY_WATER_FLUID_TYPE = RECIPE_TYPE.register("crusher_recipe",  RecipeType.simple(getRL(CrusherRecipe.Serializer.NAME.toString())));

}
