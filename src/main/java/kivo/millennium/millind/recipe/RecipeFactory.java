package kivo.millennium.millind.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CookingBookCategory;
import java.util.List;

public interface RecipeFactory<T extends GenericRecipe> {

    /**
     * 创建一个新的自定义配方实例。
     *
     * @param id       配方的资源位置 ID。
     * @param group    配方组。
     * @param category 配方书类别。
     * @param inputs   配方的输入成分列表。
     * @param outputs  配方的输出成分列表。
     * @return 创建的自定义配方实例。
     */
    T create(ResourceLocation id, String group, CookingBookCategory category, List<RecipeComponent> inputs, List<RecipeComponent> outputs);
}