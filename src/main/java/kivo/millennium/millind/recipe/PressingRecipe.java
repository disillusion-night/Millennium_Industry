package kivo.millennium.millind.recipe;

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

public class PressingRecipe extends GenericRecipe {
    public PressingRecipe(ResourceLocation id, ItemComponent input1, ItemComponent input2, ItemComponent output, int time, int energy) {
        super(id, Arrays.asList(input1, input2), Arrays.asList(output), time, energy);
        if (!(input1 instanceof ItemComponent) || !(input2 instanceof ItemComponent) || !(output instanceof ItemComponent)) {
            throw new IllegalArgumentException("MeltingRecipe input item must be an ItemComponent,input fluid must be an FluidComponent and output must be a FluidComponent.");
        }
    }

    @Override
    public ComponentCollection getCollection(ExtendedContainer container) {
        return new ComponentCollection()
                .addItemStack(container.getItem(0))
                .addItemStack(container.getItem(1));
    }

    public void costItem(ItemStack itemStack1, ItemStack itemStack2){
        if (this.inputs.get(0).asItemComponent().getCostChance() > 0.0f) {
            itemStack1.grow(-1);
        }
        if (this.inputs.get(1).asItemComponent().getCostChance() > 0.0f) {
            itemStack2.grow(-1);
        }
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return outputs.get(0).asItemComponent().getItemStack().copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<PressingRecipe> {
        private Type() { }
        public static final PressingRecipe.Type INSTANCE = new PressingRecipe.Type();
        public static final String ID = "pressing";
    }

    public static class Serializer extends GenericRecipe.Serializer<PressingRecipe> {
        public static final Serializer INSTANCE = new Serializer(new FusionRecipeFactory());
        public static final ResourceLocation ID = getRL("pressing");

        public Serializer(RecipeFactory<PressingRecipe> factory) {
            super(factory);
        }
    }

    public static class FusionRecipeFactory implements GenericRecipe.Serializer.RecipeFactory<PressingRecipe> {
        @Override
        public PressingRecipe create(ResourceLocation id, String group, CookingBookCategory category, List<RecipeComponent> inputs, List<RecipeComponent> outputs, int time, int energy) {
            if (inputs.size() != 2 || !(inputs.get(0) instanceof ItemComponent)) {
                throw new IllegalArgumentException("MeltingRecipe must have exactly one ItemComponent as input.");
            }
            if (outputs.size() != 1 || !(outputs.get(0) instanceof ItemComponent)) {
                throw new IllegalArgumentException("MeltingRecipe must have exactly one FluidComponent as output.");
            }
            return new PressingRecipe(id, inputs.get(0).asItemComponent(),inputs.get(1).asItemComponent(), outputs.get(0).asItemComponent(), time, energy);
        }
    }
}