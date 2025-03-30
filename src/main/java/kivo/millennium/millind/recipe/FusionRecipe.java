package kivo.millennium.millind.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kivo.millennium.millind.Main;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static kivo.millennium.millind.Main.getRL;

public class FusionRecipe extends GenericRecipe {
    public FusionRecipe(ResourceLocation id, ItemComponent inputItem, FluidComponent inputFluid, FluidComponent output, int time) {
        super(id, Arrays.asList(inputItem, inputFluid), Arrays.asList(output), time);
        if (!(inputItem instanceof ItemComponent) || !(inputFluid instanceof FluidComponent) || !(output instanceof FluidComponent)) {
            throw new IllegalArgumentException("MeltingRecipe input item must be an ItemComponent,input fluid must be an FluidComponent and output must be a FluidComponent.");
        }
    }

    public FluidComponent getInputFluid() {
        return (FluidComponent) this.inputs.get(1);
    }

    public FluidComponent getOutput() {
        return (FluidComponent) this.outputs.get(0);
    }

    @Override
    public ComponentCollection getCollection(ExtendedContainer container) {
        return new ComponentCollection()
                .addItemStack(container.getItem(0))
                .addFluid(container.getFluid(0));
    }


    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY; // Similar to assemble, represents the recipe output for display purposes (can be empty)
    }

    public FluidStack getResultFluid() {
        return getOutput().getFluidStack().copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<FusionRecipe> {
        private Type() { }
        public static final FusionRecipe.Type INSTANCE = new FusionRecipe.Type();
        public static final String ID = "fusion";
    }

    public static class Serializer extends GenericRecipe.Serializer<FusionRecipe> {
        public static final Serializer INSTANCE = new Serializer(new FusionRecipeFactory());
        public static final ResourceLocation ID = getRL("fusion");

        public Serializer(RecipeFactory<FusionRecipe> factory) {
            super(factory);
        }
    }

    public static class FusionRecipeFactory implements GenericRecipe.Serializer.RecipeFactory<FusionRecipe> {
        @Override
        public FusionRecipe create(ResourceLocation id, String group, CookingBookCategory category, List<RecipeComponent> inputs, List<RecipeComponent> outputs, int time) {
            if (inputs.size() != 2 || !(inputs.get(0) instanceof ItemComponent)) {
                throw new IllegalArgumentException("MeltingRecipe must have exactly one ItemComponent as input.");
            }
            if (outputs.size() != 1 || !(outputs.get(0) instanceof FluidComponent)) {
                throw new IllegalArgumentException("MeltingRecipe must have exactly one FluidComponent as output.");
            }
            return new FusionRecipe(id, (ItemComponent) inputs.get(0),(FluidComponent) inputs.get(1), (FluidComponent) outputs.get(0), time);
        }
    }
}