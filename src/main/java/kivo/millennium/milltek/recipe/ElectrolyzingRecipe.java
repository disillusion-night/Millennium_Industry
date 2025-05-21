package kivo.millennium.milltek.recipe;

import kivo.millennium.milltek.Main;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

public class ElectrolyzingRecipe extends GenericRecipe {
  public ElectrolyzingRecipe(ResourceLocation id, List<RecipeComponent> inputs, List<RecipeComponent> outputs, int time,
      int energyCost) {
    super(id, inputs, outputs, time, energyCost);
  }
  
  @Override
  public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
    return ItemStack.EMPTY;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return Serializer.INSTANCE;
  }

  @Override
  public RecipeType<?> getType() {
    return Type.INSTANCE;
  }

  public static class Type implements RecipeType<ElectrolyzingRecipe> {
    private Type() {
    }

    public static final Type INSTANCE = new Type();
    public static final String ID = "electrolyzing";
  }

  public static class Serializer extends GenericRecipe.Serializer<ElectrolyzingRecipe> {
    public static final Serializer INSTANCE = new Serializer(new ElectrolyzingRecipeFactory());
    public static final ResourceLocation ID = Main.getRL("electrolyzing");

    public Serializer(ElectrolyzingRecipeFactory factory) {
      super(factory);
    }
  }

  public static class ElectrolyzingRecipeFactory
      implements GenericRecipe.Serializer.RecipeFactory<ElectrolyzingRecipe> {
    @Override
    public ElectrolyzingRecipe create(ResourceLocation id, String group, CookingBookCategory category,
        List<RecipeComponent> inputs, List<RecipeComponent> outputs, int time, int energyCost) {
      return new ElectrolyzingRecipe(id, inputs, outputs, time, energyCost);
    }
  }

}