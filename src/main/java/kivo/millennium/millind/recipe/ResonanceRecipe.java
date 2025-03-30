package kivo.millennium.millind.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.List;

import static kivo.millennium.millind.Main.getRL;

public class ResonanceRecipe extends GenericRecipe {
    public ResonanceRecipe(ResourceLocation id, ItemComponent inputItem, ItemComponent output, int time) {
        super(id, Arrays.asList(inputItem), Arrays.asList(output), time);
        if (!(inputItem instanceof ItemComponent) || !(output instanceof ItemComponent)) {
            throw new IllegalArgumentException("MeltingRecipe input item must be an ItemComponent,input fluid must be an FluidComponent and output must be a FluidComponent.");
        }
    }

    public FluidComponent getOutput() {
        return (FluidComponent) this.outputs.get(0);
    }

    @Override
    public ComponentCollection getCollection(ExtendedContainer container) {
        return new ComponentCollection()
                .addItemStack(container.getItem(0));
    }


    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ((ItemComponent) outputs.get(0)).getItemStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<ResonanceRecipe> {
        private Type() { }
        public static final ResonanceRecipe.Type INSTANCE = new ResonanceRecipe.Type();
        public static final String ID = "resonance";
    }

    public static class Serializer extends GenericRecipe.Serializer<ResonanceRecipe> {
        public static final Serializer INSTANCE = new Serializer(new FusionRecipeFactory());
        public static final ResourceLocation ID = getRL("resonance");

        public Serializer(RecipeFactory<ResonanceRecipe> factory) {
            super(factory);
        }
    }

    public static class FusionRecipeFactory implements GenericRecipe.Serializer.RecipeFactory<ResonanceRecipe> {
        @Override
        public ResonanceRecipe create(ResourceLocation id, String group, CookingBookCategory category, List<RecipeComponent> inputs, List<RecipeComponent> outputs, int time) {
            if (inputs.size() != 2 || !(inputs.get(0) instanceof ItemComponent)) {
                throw new IllegalArgumentException("MeltingRecipe must have exactly one ItemComponent as input.");
            }
            if (outputs.size() != 1 || !(outputs.get(0) instanceof FluidComponent)) {
                throw new IllegalArgumentException("MeltingRecipe must have exactly one FluidComponent as output.");
            }
            return new ResonanceRecipe(id, (ItemComponent) inputs.get(0), (ItemComponent) outputs.get(0), time);
        }
    }
}