package kivo.millennium.milltek;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;

import kivo.millennium.milltek.init.MillenniumRecipes;
import kivo.millennium.milltek.pipe.client.PipeModelLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import static kivo.millennium.milltek.init.MillenniumBlockEntities.BLOCK_ENTITIES;
import static kivo.millennium.milltek.init.MillenniumBlocks.BLOCKS;
import static kivo.millennium.milltek.init.MillenniumCreativeTab.CREATIVE_MODE_TABS;
import static kivo.millennium.milltek.init.MillenniumEntities.ENTITIES;
import static kivo.millennium.milltek.init.MillenniumFluidTypes.FLUID_TYPES;
import static kivo.millennium.milltek.init.MillenniumFluids.FLUIDS;
import static kivo.millennium.milltek.init.MillenniumItems.ITEMS;
import static kivo.millennium.milltek.init.MillenniumGases.GASES;
import static kivo.millennium.milltek.init.MillenniumLevelNetworkType.LEVEL_NETWORK_TYPES;
import static kivo.millennium.milltek.init.MillenniumMenuTypes.MENU_TYPES;
import static kivo.millennium.milltek.init.MillenniumRecipes.RECIPE_SERIALIZERS;

import org.slf4j.Logger;

@Mod(Main.MODID)
public class Main {
    public static final String MODID = "milltek";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void log(String string) {
        LOGGER.info(string);
    }

    public static void log(Integer integer) {
        LOGGER.info(integer.toString());
    }

    public ResourceLocation modLoc(String name) {
        return new ResourceLocation(MODID, name);
    }

    @SuppressWarnings("removal")
    public Main() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        //modEventBus.addListener(this::onRegisterCommands);

        ITEMS.register(modEventBus);
        ENTITIES.register(modEventBus);
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        FLUIDS.register(modEventBus);
        FLUID_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        MENU_TYPES.register(modEventBus);
        LEVEL_NETWORK_TYPES.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        GASES.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        
        modEventBus.addListener(this::addCreative);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
    
    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        // LOGGER.info("HELLO FROM COMMON SETUP");
        // LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        // if (Config.logDirtBlock) LOGGER.info("DIRT BLOCK >> {}",
        // ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        // LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        // Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    @SubscribeEvent
    public void onRegisterCommands(final RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        //PipeNetworkCommand.register(dispatcher);
    }

    // You can use EventBusSubscriber to automatically register all static methods
    // in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }

        @SubscribeEvent
        public static void modelInit(ModelEvent.RegisterGeometryLoaders event) {
            PipeModelLoader.register(event);
        }

        @SubscribeEvent
        public static void registerBlockColor(RegisterColorHandlersEvent.Block event) {
        }
    }

    public static ResourceLocation getRL(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static ResourceLocation getKey(ItemLike itemLike) {
        return ForgeRegistries.ITEMS.getKey(itemLike.asItem());
    }

    public static ResourceLocation getKey(Fluid fluid) {
        return ForgeRegistries.FLUIDS.getKey(fluid);
    }

    public static ResourceLocation getKey(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    public static ResourceLocation getResourceKey(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

}
