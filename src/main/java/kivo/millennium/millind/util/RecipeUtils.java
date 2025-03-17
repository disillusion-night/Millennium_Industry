package kivo.millennium.millind.util;

import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class RecipeUtils {

    public static MineralRecipeBuilder mineralRecipeBuilder(Consumer<FinishedRecipe> writer, String mineralName) {
        return new MineralRecipeBuilder(writer, mineralName);
    }

    protected static InventoryChangeTrigger.TriggerInstance has(MinMaxBounds.Ints pCount, ItemLike pItem) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(pItem).withCount(pCount).build());
    }

    /**
     * Creates a new {@link InventoryChangeTrigger} that checks for a player having a certain item.
     */
    protected static InventoryChangeTrigger.TriggerInstance has(ItemLike pItemLike) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(pItemLike).build());
    }

    /**
     * Creates a new {@link InventoryChangeTrigger} that checks for a player having an item within the given tag.
     */
    protected static InventoryChangeTrigger.TriggerInstance has(TagKey<Item> pTag) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(pTag).build());
    }

    /**
     * Creates a new {@link InventoryChangeTrigger} that checks for a player having a certain item.
     */
    protected static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... pPredicates) {
        return new InventoryChangeTrigger.TriggerInstance(ContextAwarePredicate.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, pPredicates);
    }

    public static class MineralRecipeBuilder {
        private final Consumer<FinishedRecipe> writer;
        private final String mineralName;
        private ItemLike ingot;
        private ItemLike block;
        private ItemLike ore;
        private ItemLike rawOre;
        private ItemLike nugget;
        private ItemLike deepslateOre;
        private ItemLike rawBlock; // 新增粗矿块属性

        private boolean createBlockRecipe = false;
        private boolean createIngotFromBlockRecipe = false;
        private boolean createRawOreFromOreRecipe = false; // 原矿合成粗矿物品的配方，保持原样
        private boolean createNuggetFromIngotRecipe = false;
        private boolean createIngotFromNuggetRecipe = false;
        private boolean createIngotFromSmeltingRawOreRecipe = false;
        private boolean createIngotFromSmeltingOreRecipe = false;
        private boolean createIngotFromSmeltingDeepslateOreRecipe = false;
        private boolean createRawBlockFromRawOreRecipe = false; // 新增粗矿块合成配方

        public MineralRecipeBuilder(Consumer<FinishedRecipe> writer, String mineralName) {
            this.writer = writer;
            this.mineralName = mineralName;
        }

        public MineralRecipeBuilder ingot(ItemLike ingot) {
            this.ingot = ingot;
            return this;
        }

        public MineralRecipeBuilder block(ItemLike block) {
            this.block = block;
            return this;
        }

        public MineralRecipeBuilder ore(ItemLike ore) {
            this.ore = ore;
            return this;
        }

        public MineralRecipeBuilder rawOre(ItemLike rawOre) {
            this.rawOre = rawOre;
            return this;
        }

        public MineralRecipeBuilder nugget(ItemLike nugget) {
            this.nugget = nugget;
            return this;
        }

        public MineralRecipeBuilder deepslateOre(ItemLike deepslateOre) {
            this.deepslateOre = deepslateOre;
            return this;
        }

        public MineralRecipeBuilder rawBlock(ItemLike rawBlock) {
            this.rawBlock = rawBlock;
            return this;
        }

        public MineralRecipeBuilder withBlockRecipe() {
            this.createBlockRecipe = true;
            return this;
        }

        public MineralRecipeBuilder withIngotFromBlockRecipe() {
            this.createIngotFromBlockRecipe = true;
            return this;
        }

        public MineralRecipeBuilder withRawOreFromOreRecipe() {
            this.createRawOreFromOreRecipe = true;
            return this;
        }

        public MineralRecipeBuilder withNuggetFromIngotRecipe() {
            this.createNuggetFromIngotRecipe = true;
            return this;
        }

        public MineralRecipeBuilder withIngotFromNuggetRecipe() {
            this.createIngotFromNuggetRecipe = true;
            return this;
        }

        public MineralRecipeBuilder withIngotFromSmeltingRawOreRecipe() {
            this.createIngotFromSmeltingRawOreRecipe = true;
            return this;
        }

        public MineralRecipeBuilder withIngotFromSmeltingOreRecipe() {
            this.createIngotFromSmeltingOreRecipe = true;
            return this;
        }

        public MineralRecipeBuilder withIngotFromSmeltingDeepslateOreRecipe() {
            this.createIngotFromSmeltingDeepslateOreRecipe = true;
            return this;
        }

        public MineralRecipeBuilder withRawBlockFromRawOreRecipe() {
            this.createRawBlockFromRawOreRecipe = true;
            return this;
        }

        public MineralRecipeBuilder withAllRecipe() {
            return withBlockRecipe()
                    .withIngotFromBlockRecipe()
                    .withNuggetFromIngotRecipe()
                    .withIngotFromNuggetRecipe()
                    .withIngotFromSmeltingRawOreRecipe()
                    .withIngotFromSmeltingOreRecipe()
                    .withIngotFromSmeltingDeepslateOreRecipe()
                    .withRawBlockFromRawOreRecipe(); // 添加粗矿块合成
        }

        public void build() {
            if (ingot == null) throw new IllegalStateException("Ingot must be set for mineral: " + mineralName);
            if (block == null) throw new IllegalStateException("Block must be set for mineral: " + mineralName);
            if (ore == null) throw new IllegalStateException("Ore must be set for mineral: " + mineralName);
            if (rawOre == null) throw new IllegalStateException("Raw Ore must be set for mineral: " + mineralName);
            if (nugget == null) throw new IllegalStateException("Nugget must be set for mineral: " + mineralName);
            if (deepslateOre == null)
                throw new IllegalStateException("Deepslate Ore must be set for mineral: " + mineralName);
            if (rawBlock == null) throw new IllegalStateException("Raw Block must be set for mineral: " + mineralName);

            if (createBlockRecipe) {
                ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block)
                        .pattern("###")
                        .pattern("###")
                        .pattern("###")
                        .define('#', ingot)
                        .unlockedBy("has_" + mineralItemName(ingot), has(ingot))
                        .save(writer, new ResourceLocation(getNamespace(ingot), mineralName + "_block"));
            }

            if (createIngotFromBlockRecipe) {
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ingot, 9)
                        .requires(block)
                        .unlockedBy("has_" + mineralBlockName(block), has(block))
                        .save(writer, new ResourceLocation(getNamespace(block), mineralName + "_ingot_from_block"));
            }

            if (createRawOreFromOreRecipe) {
                ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, rawOre) // 注意这里，之前是将 9 个矿石合成 1 个粗矿物品
                        .pattern("###")
                        .pattern("###")
                        .pattern("###")
                        .define('#', ore)
                        .unlockedBy("has_" + mineralOreName(ore), has(ore))
                        .save(writer, new ResourceLocation(getNamespace(ore), "raw_" + mineralName + "_from_ore"));
            }

            if (createNuggetFromIngotRecipe) {
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, nugget, 9)
                        .requires(ingot)
                        .unlockedBy("has_" + mineralItemName(ingot), has(ingot))
                        .save(writer, new ResourceLocation(getNamespace(ingot), mineralName + "_nugget_from_ingot"));
            }

            if (createIngotFromNuggetRecipe) {
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ingot)
                        .pattern("###")
                        .pattern("###")
                        .pattern("###")
                        .define('#', nugget)
                        .unlockedBy("has_" + mineralNuggetName(nugget), has(nugget))
                        .save(writer, new ResourceLocation(getNamespace(nugget), mineralName + "_ingot_from_nugget"));
            }

            if (createIngotFromSmeltingRawOreRecipe) {
                SimpleCookingRecipeBuilder.smelting(Ingredient.of(rawOre), RecipeCategory.MISC, ingot, 0.7F, 200)
                        .unlockedBy("has_" + mineralRawOreName(rawOre), has(rawOre))
                        .group(mineralName)
                        .save(writer, new ResourceLocation(getNamespace(rawOre), mineralName + "_ingot_from_smelting_raw"));
            }

            if (createIngotFromSmeltingOreRecipe) {
                SimpleCookingRecipeBuilder.smelting(Ingredient.of(ore), RecipeCategory.MISC, ingot, 0.7F, 200)
                        .unlockedBy("has_" + mineralOreName(ore), has(ore))
                        .group(mineralName)
                        .save(writer, new ResourceLocation(getNamespace(ore), mineralName + "_ingot_from_smelting"));
            }

            if (createIngotFromSmeltingDeepslateOreRecipe) {
                SimpleCookingRecipeBuilder.smelting(Ingredient.of(deepslateOre), RecipeCategory.MISC, ingot, 0.7F, 200)
                        .unlockedBy("has_" + mineralDeepslateOreName(deepslateOre), has(deepslateOre))
                        .group(mineralName)
                        .save(writer, new ResourceLocation(getNamespace(deepslateOre), mineralName + "_ingot_from_smelting_deepslate"));
            }

            if (createRawBlockFromRawOreRecipe) {
                ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, rawBlock)
                        .pattern("###")
                        .pattern("###")
                        .pattern("###")
                        .define('#', rawOre)
                        .unlockedBy("has_" + mineralRawOreName(rawOre), has(rawOre))
                        .save(writer, new ResourceLocation(getNamespace(rawOre), "raw_" + mineralName + "_block"));
            }
        }

        private String mineralItemName(ItemLike itemLike) {
            return BuiltInRegistries.ITEM.getKey(itemLike.asItem()).getPath();
        }

        private String mineralBlockName(ItemLike itemLike) {
            return BuiltInRegistries.BLOCK.getKey(Block.byItem(itemLike.asItem())).getPath();
        }

        private String mineralOreName(ItemLike itemLike) {
            return BuiltInRegistries.BLOCK.getKey(Block.byItem(itemLike.asItem())).getPath();
        }

        private String mineralRawOreName(ItemLike itemLike) {
            return BuiltInRegistries.ITEM.getKey(itemLike.asItem()).getPath();
        }

        private String mineralNuggetName(ItemLike itemLike) {
            return BuiltInRegistries.ITEM.getKey(itemLike.asItem()).getPath();
        }

        private String mineralDeepslateOreName(ItemLike itemLike) {
            return BuiltInRegistries.BLOCK.getKey(Block.byItem(itemLike.asItem())).getPath();
        }

        private String getNamespace(ItemLike itemLike) {
            return BuiltInRegistries.ITEM.getKey(itemLike.asItem()).getNamespace();
        }
    }
}
