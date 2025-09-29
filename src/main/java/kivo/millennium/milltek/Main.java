package kivo.millennium.milltek;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import com.mojang.brigadier.arguments.DoubleArgumentType;

import kivo.millennium.milltek.eventHandler.NetworkTickHandler;
import kivo.millennium.milltek.init.MillenniumGases;
import kivo.millennium.milltek.init.MillenniumRecipes;
import kivo.millennium.milltek.network.MillenniumNetwork;
import kivo.millennium.milltek.pipe.client.PipeModelLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
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
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        modEventBus.addListener(this::commonSetup);
        forgeEventBus.addListener(NetworkTickHandler::onWorldTick);
        // modEventBus.addListener(this::onRegisterCommands);

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
        MillenniumGases.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        // 注册网络包
        MillenniumNetwork.register();

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

    public static ResourceLocation getRL(String path) {
        return new ResourceLocation(MODID, path);
    }
    public static ResourceLocation getKey(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem());
    }
    public static ResourceLocation getKey(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }
    public static ResourceLocation getKey(Fluid fluid) {
        return BuiltInRegistries.FLUID.getKey(fluid);
    }

    @SubscribeEvent
    public void onRegisterCommands(final RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        // PipeNetworkCommand.register(dispatcher);
        // 动态根据枚举构建子命令，以便后续扩展只需修改枚举
        var root = Commands.literal("launch_missile").requires(cs -> cs.hasPermission(2));

        // AIR 分支
        var airBranch = Commands.literal(kivo.millennium.milltek.entity.special.MissileType.AIR.getName())
                .then(Commands.argument("shooter", EntityArgument.entity())
                        .then(Commands.argument("target", EntityArgument.entity())
                                .executes(ctx -> {
                                    var shooterEntity = EntityArgument.getEntity(ctx, "shooter");
                                    var targetEntity = EntityArgument.getEntity(ctx, "target");
                                    if (!(shooterEntity instanceof LivingEntity) || !(targetEntity instanceof LivingEntity)) {
                                        ctx.getSource().sendFailure(Component.literal("Shooter and target must be living entities."));
                                        return 0;
                                    }
                                    LivingEntity shooter = (LivingEntity) shooterEntity;
                                    LivingEntity target = (LivingEntity) targetEntity;
                                    net.minecraft.world.level.Level level = shooter.level();
                                    kivo.millennium.milltek.entity.special.MissileUtils.launchAirToAirMissile(level, shooter, target);
                                    ctx.getSource().sendSuccess(() -> Component.literal("Launched air-to-air missile."), true);
                                    return 1;
                                })));

        // GROUND 分支
        var groundBranch = Commands.literal(kivo.millennium.milltek.entity.special.MissileType.GROUND.getName())
                .then(Commands.argument("shooter", EntityArgument.entity())
                        .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                                .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                                        .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                                                .executes(ctx -> {
                                                    var shooterEntity = EntityArgument.getEntity(ctx, "shooter");
                                                    if (!(shooterEntity instanceof LivingEntity)) {
                                                        ctx.getSource().sendFailure(Component.literal("Shooter must be a living entity."));
                                                        return 0;
                                                    }
                                                    LivingEntity shooter = (LivingEntity) shooterEntity;
                                                    double x = DoubleArgumentType.getDouble(ctx, "x");
                                                    double y = DoubleArgumentType.getDouble(ctx, "y");
                                                    double z = DoubleArgumentType.getDouble(ctx, "z");
                                                    net.minecraft.world.phys.Vec3 targetPos = new net.minecraft.world.phys.Vec3(x, y, z);
                                                    kivo.millennium.milltek.entity.special.MissileUtils.launchGroundTacticalMissile(shooter.level(), shooter, targetPos);
                                                    ctx.getSource().sendSuccess(() -> Component.literal("Launched ground-tactical missile."), true);
                                                    return 1;
                                                })))));
        // 将构建好的子命令注册到 dispatcher
        dispatcher.register(root.then(airBranch).then(groundBranch));
    }
}