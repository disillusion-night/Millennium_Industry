package kivo.millennium.millind.recipe;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ComponentCollection {
    private ArrayList<RecipeComponent> recipeComponents;

    public ComponentCollection(ArrayList<RecipeComponent> components){
        this.recipeComponents = components;
    }

    public ComponentCollection(List<RecipeComponent> components){
        this.recipeComponents = new ArrayList<>(components);
    }

    public ComponentCollection(RecipeComponent... components){
        this.recipeComponents = new ArrayList<>(List.of(components));
    }

    public boolean matches(ComponentCollection componentCollection){
        if(componentCollection.getSize() != recipeComponents.size()) return false;
        for(int i = 0; i < getSize(); i++){
            if(!get(i).matches(componentCollection.get(i))){
                return false;
            }
        }
        return true;
    }

    public RecipeComponent get(int index){
        return this.recipeComponents.get(index);
    }

    public List<RecipeComponent> getRecipeComponents() {
        return recipeComponents;
    }

    public int getSize(){
        return recipeComponents.size();
    }

    public ComponentCollection addItemStack(ItemStack stack){
        this.recipeComponents.add(new ItemComponent(stack));
        return this;
    }
    public ComponentCollection addFluid(FluidStack stack){
        this.recipeComponents.add(new FluidComponent(stack));
        return this;
    }
}
