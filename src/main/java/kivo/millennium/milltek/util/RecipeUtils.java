package kivo.millennium.milltek.util;

import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import static kivo.millennium.milltek.Main.getKey;

import java.util.function.Consumer;

import kivo.millennium.milltek.Main;
import kivo.millennium.milltek.datagen.SimpleSingleRecipeBuilder;

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

    public static MineralRecipeBuilder createMineralRecipeBuilder(Consumer<FinishedRecipe> writer, String mineralName) {
        return new MineralRecipeBuilder(writer, mineralName);
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
        private ItemLike rawBlock;
        private ItemLike dust;
        private ItemLike panle;
        private ItemLike pipe;
        private ItemLike rod;
        //private ItemLike

        private boolean createBlockRecipe = false;
        private boolean createIngotFromBlockRecipe = false;
        private boolean createRawOreFromRawBlockRecipe = false;
        private boolean createNuggetFromIngotRecipe = false;
        private boolean createIngotFromNuggetRecipe = false;
        private boolean createIngotFromSmeltingRawOreRecipe = false;
        private boolean createIngotFromSmeltingOreRecipe = false;
        private boolean createIngotFromSmeltingDeepslateOreRecipe = false;
        private boolean createRawBlockFromRawOreRecipe = false;
        private boolean createDustFromCrushingIngot = false;
        private boolean createPanleFromPressingIngot = false;
        private boolean createPanleFromPressingDust = false;
        private boolean createRodFromPressingIngot = false;
        private boolean createRodFromPressingDust = false;
        //private boolean createPipeFrom

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

        public MineralRecipeBuilder dust(ItemLike dust) {
            this.dust = dust;
            return this;
        }

        public MineralRecipeBuilder panle(ItemLike panle) {
            this.panle = panle;
            return this;
        }

        public MineralRecipeBuilder pipe(ItemLike pipe) {
            this.pipe = pipe;
            return this;
        }

        public MineralRecipeBuilder rod(ItemLike rod) {
            this.rod = rod;
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

        public MineralRecipeBuilder withRawOreFromRawBlockRecipe() {
            this.createRawOreFromRawBlockRecipe = true;
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

        public MineralRecipeBuilder withDustFromCrushingIngot() {
            this.createDustFromCrushingIngot = true;
            return this;
        }


        public MineralRecipeBuilder withoutSmelting() {
            return withBlockRecipe()
                    .withIngotFromBlockRecipe()
                    .withNuggetFromIngotRecipe()
                    .withIngotFromNuggetRecipe()
                    .withRawBlockFromRawOreRecipe()
                    .withRawOreFromRawBlockRecipe()
                    .withDustFromCrushingIngot();
        }

        public MineralRecipeBuilder withAllRecipe() {
            return withBlockRecipe()
                    .withIngotFromBlockRecipe()
                    .withNuggetFromIngotRecipe()
                    .withIngotFromNuggetRecipe()
                    .withIngotFromSmeltingRawOreRecipe()
                    .withIngotFromSmeltingOreRecipe()
                    .withIngotFromSmeltingDeepslateOreRecipe()
                    .withRawBlockFromRawOreRecipe()
                    .withRawOreFromRawBlockRecipe()
                    .withDustFromCrushingIngot();
        }

        private void checkIngot() {
            if (ingot == null) throw new IllegalStateException("Ingot must be set for mineral: " + mineralName);
        }

        private void checkNugget() {
            if (nugget == null && (
                    (createNuggetFromIngotRecipe || createIngotFromNuggetRecipe)
            )) throw new IllegalStateException("Nugget must be set for mineral: " + mineralName);
        }

        private void checkDust() {
            if (dust == null &&
                    createDustFromCrushingIngot
            ) throw new IllegalStateException("Dust must be set for mineral: " + mineralName);
        }

        private void checkOre() {
            if (ore == null &&
                    (createIngotFromSmeltingOreRecipe || createRawOreFromRawBlockRecipe || createRawBlockFromRawOreRecipe)
            ) throw new IllegalStateException("Ore must be set for mineral: " + mineralName);
        }

        private void checkRawOre() {
            if (ore == null &&
                    createIngotFromSmeltingRawOreRecipe
            ) throw new IllegalStateException("Ore must be set for mineral: " + mineralName);
        }

        private void checkDeepSlateOre() {
            if (ore == null && createIngotFromSmeltingDeepslateOreRecipe
            ) throw new IllegalStateException("Deepslate Ore must be set for mineral: " + mineralName);
        }

        public void build() {
            checkIngot();
            checkNugget();
            if (block == null) throw new IllegalStateException("Block must be set for mineral: " + mineralName);
            checkOre();
            checkRawOre();
            checkDeepSlateOre();
            checkDust();

            if (createBlockRecipe) {
                ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block)
                        .pattern("###")
                        .pattern("###")
                        .pattern("###")
                        .define('#', ingot)
                        .unlockedBy("has_" + getItemName(ingot), has(ingot))
                        .save(writer, Main.getRL(mineralName + "_block_from_ingot"));
            }

            if (createIngotFromBlockRecipe) {
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ingot, 9)
                        .requires(block)
                        .unlockedBy("has_" + getBlockName(block), has(block))
                        .save(writer, Main.getRL(mineralName + "_ingot_from_block"));
            }

            if (createNuggetFromIngotRecipe) {
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, nugget, 9)
                        .requires(ingot)
                        .unlockedBy("has_" + getItemName(ingot), has(ingot))
                        .save(writer, Main.getRL(mineralName + "_nugget_from_ingot"));
            }

            if (createIngotFromNuggetRecipe) {
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ingot)
                        .pattern("###")
                        .pattern("###")
                        .pattern("###")
                        .define('#', nugget)
                        .unlockedBy("has_" + getItemName(nugget), has(nugget))
                        .save(writer, Main.getRL(mineralName + "_ingot_from_nugget"));
            }

            if (createIngotFromSmeltingRawOreRecipe) {
                SimpleCookingRecipeBuilder.smelting(Ingredient.of(rawOre), RecipeCategory.MISC, ingot, 0.7F, 200)
                        .unlockedBy("has_" + getItemName(rawOre), has(rawOre))
                        .group(mineralName)
                        .save(writer, Main.getRL(mineralName + "_ingot_from_smelting_raw"));

                SimpleCookingRecipeBuilder.blasting(Ingredient.of(rawOre), RecipeCategory.MISC, ingot, 0.7F, 100)
                        .unlockedBy("has_" + getItemName(rawOre), has(rawOre))
                        .group(mineralName)
                        .save(writer, Main.getRL(mineralName + "_ingot_from_blasting_raw"));
            }

            if (createIngotFromSmeltingOreRecipe) {
                SimpleCookingRecipeBuilder.smelting(Ingredient.of(ore), RecipeCategory.MISC, ingot, 0.7F, 200)
                        .unlockedBy("has_" + getItemName(ore), has(ore))
                        .group(mineralName)
                        .save(writer, Main.getRL(mineralName + "_ingot_from_smelting"));

                SimpleCookingRecipeBuilder.blasting(Ingredient.of(ore), RecipeCategory.MISC, ingot, 0.7F, 100)
                        .unlockedBy("has_" + getItemName(ore), has(ore))
                        .group(mineralName)
                        .save(writer, Main.getRL(mineralName + "_ingot_from_blasting"));
            }

            if (createIngotFromSmeltingDeepslateOreRecipe) {
                SimpleCookingRecipeBuilder.smelting(Ingredient.of(deepslateOre), RecipeCategory.MISC, ingot, 0.7F, 200)
                        .unlockedBy("has_" + getBlockName(deepslateOre), has(deepslateOre))
                        .group(mineralName)
                        .save(writer, Main.getRL(mineralName + "_ingot_from_smelting_deepslate"));

                SimpleCookingRecipeBuilder.blasting(Ingredient.of(deepslateOre), RecipeCategory.MISC, ingot, 0.7F, 100)
                        .unlockedBy("has_" + getBlockName(deepslateOre), has(deepslateOre))
                        .group(mineralName)
                        .save(writer, Main.getRL(mineralName + "_ingot_from_blasting_deepslate"));
            }

            if (createDustFromCrushingIngot) {
                SimpleSingleRecipeBuilder.crushing(new ItemStack(ingot), RecipeCategory.MISC, new ItemStack(dust, 1), 0.7F, 100, 1000)
                        .unlockedBy("has_" + Main.getKey(ingot).getPath(), has(ingot))
                        .group(mineralName)
                        .save(writer, Main.getRL(mineralName + "_dust_from_crushing_ingot"));
            }

            if (createRawBlockFromRawOreRecipe) {
                ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, rawBlock)
                        .pattern("###")
                        .pattern("###")
                        .pattern("###")
                        .define('#', rawOre)
                        .unlockedBy("has_" + getBlockName(rawOre), has(rawOre))
                        .save(writer, Main.getRL("raw_" + mineralName + "_block_from_ore"));
            }

            if (createRawOreFromRawBlockRecipe) {
                ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, rawOre, 9)
                        .requires(rawBlock)
                        .unlockedBy("has_" + getBlockName(rawBlock), has(rawBlock))
                        .save(writer, Main.getRL(mineralName + "_raw_ore_from_raw_block"));
            }
        }

        private String getItemName(ItemLike itemLike) {
            return BuiltInRegistries.ITEM.getKey(itemLike.asItem()).getPath();
        }

        private String getBlockName(ItemLike itemLike) {
            return BuiltInRegistries.BLOCK.getKey(Block.byItem(itemLike.asItem())).getPath();
        }

    }
}
