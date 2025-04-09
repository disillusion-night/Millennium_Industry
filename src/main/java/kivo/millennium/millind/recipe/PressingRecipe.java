package kivo.millennium.millind.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Arrays;
import java.util.List;

import static kivo.millennium.millind.Main.getRL;

public class PressingRecipe extends GenericRecipe {
    public PressingRecipe(ResourceLocation id, List<ISlotProxy> input, List<ISlotProxy> output, int time, int energy) {
        super(id, input, output, time, energy);
        /*
        if (!(input1 instanceof ItemComponent) || !(input2 instanceof ItemComponent) || !(output instanceof ItemComponent)) {
            throw new IllegalArgumentException("MeltingRecipe input item must be an ItemComponent,input fluid must be an FluidComponent and output must be a FluidComponent.");
        }*/
    }

    public void costItem(ItemStack itemStack1, ItemStack itemStack2){
        if (this.inputs.getProxyInSlot(0).asItemProxy().getDamage() > 0) {
            itemStack1.setDamageValue(itemStack1.getDamageValue() - 1);
        }
        if (this.inputs.getProxyInSlot(1).asItemProxy().getDamage() > 0) {
            itemStack2.setDamageValue(itemStack2.getDamageValue() - 1);
        }
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
        public PressingRecipe create(ResourceLocation id, String group, CookingBookCategory category, List<ISlotProxy> inputs, List<ISlotProxy> outputs, int time, int energy) {
            /*
            if (inputs.size() != 2 || !(inputs.get(0) instanceof ItemComponent)) {
                throw new IllegalArgumentException("MeltingRecipe must have exactly one ItemComponent as input.");
            }
            if (outputs.size() != 1 || !(outputs.get(0) instanceof ItemComponent)) {
                throw new IllegalArgumentException("MeltingRecipe must have exactly one FluidComponent as output.");
            }*/
            return new PressingRecipe(id, inputs, outputs, time, energy);
        }
    }
}