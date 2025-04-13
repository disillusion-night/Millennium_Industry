package kivo.millennium.millind.datagen;

import kivo.millennium.client.datagen.MillenniumBlockStateProvider;
import kivo.millennium.client.datagen.MillenniumItemModelProvider;
import kivo.millennium.client.datagen.language.EnglishProvider;
import kivo.millennium.client.datagen.language.MillenniumLanguageProvider;
import kivo.millennium.client.datagen.language.SimplifiedChineseProvider;
import kivo.millennium.millind.datagen.tag.ItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

import static kivo.millennium.millind.Main.MODID;

@Mod.EventBusSubscriber(modid = MODID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGeneratorHandler {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        ExistingFileHelper efh = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        event.getGenerator().addProvider(
                event.includeClient(),
                (DataProvider.Factory<MillenniumLanguageProvider>) EnglishProvider::new
        );
        event.getGenerator().addProvider(
                event.includeClient(),
                (DataProvider.Factory<MillenniumLanguageProvider>) SimplifiedChineseProvider::new
        );

        event.getGenerator().addProvider(
                event.includeClient(),
                (DataProvider.Factory<MillenniumBlockStateProvider>) pOutput -> new MillenniumBlockStateProvider(pOutput,efh)
        );

        event.getGenerator().addProvider(
                event.includeClient(),
                (DataProvider.Factory<MillenniumItemModelProvider>) pOutput -> new MillenniumItemModelProvider(pOutput,efh)
        );

        event.getGenerator().addProvider(
                event.includeServer(),
                (DataProvider.Factory<ItemTagProvider>) pOutput -> new ItemTagProvider(pOutput, lookup, efh)
        );

        event.getGenerator().addProvider(
                event.includeServer(),
                (DataProvider.Factory<RecipeProvider>) MillenniumRecipeProvider::new
        );
    }
}