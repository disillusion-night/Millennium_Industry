package kivo.millennium.millind.init;

import kivo.millennium.millind.block.device.crusher.CrusherBL;
import kivo.millennium.millind.block.fluid.IcyWaterBlock;
import kivo.millennium.millind.block.fluidContainer.MetalTankBL;
import kivo.millennium.millind.block.generator.GeneratorBL;
import kivo.millennium.millind.block.hypercube.HDECBL;
import kivo.millennium.millind.block.device.inductionFurnace.InductionFurnaceBL;
import kivo.millennium.millind.block.laser.NetherStarLaserBL;
import kivo.millennium.millind.block.laser.SolarGeneratorBL;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.function.Supplier;

import static kivo.millennium.millind.Main.MODID;


public class MillenniumBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    public static final RegistryObject<Block> ICY_WATER_BL = BLOCKS.register("icy_water", IcyWaterBlock::new);

    public static final RegistryObject<MetalTankBL> METAL_TANK_BL = registerWithItem("metal_tank", MetalTankBL::new, MillenniumItems.ENGINEERING_PARTS);

    public static final RegistryObject<HDECBL> HDEC_BL = registerWithItem("hdec", HDECBL::new,  MillenniumItems.ENGINEERING_PARTS);

    public static final RegistryObject<NetherStarLaserBL> NETHER_STAR_LASER_BL = registerWithItem("nether_star_laser", NetherStarLaserBL::new,  MillenniumItems.ENGINEERING_PARTS);

    public static final RegistryObject<GeneratorBL> GENERATOR_BL = registerWithItem("generator", GeneratorBL::new,  MillenniumItems.ENGINEERING_PARTS);

    public static final RegistryObject<InductionFurnaceBL> INDUCTION_FURNACE_BL = registerWithItem("induction_furnace", InductionFurnaceBL::new,  MillenniumItems.ENGINEERING_PARTS);

    public static final RegistryObject<CrusherBL> CRUSHER_BL = registerWithItem("crusher", CrusherBL::new, MillenniumItems.ENGINEERING_PARTS);

    public static final RegistryObject<SolarGeneratorBL> SOLAR_GENERATOR = registerWithItem("solar_generator", SolarGeneratorBL::new, MillenniumItems.ENGINEERING_PARTS);


    public static final RegistryObject<Block> LEAD_ORE = registerWithItem("lead_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
    public static final RegistryObject<Block> DEEPSLATE_LEAD_ORE = registerWithItem("deepslate_lead_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(LEAD_ORE.get()).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)));
    public static final RegistryObject<Block> LEAD_BLOCK = registerWithItem("lead_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> RAW_LEAD_BLOCK = registerWithItem("raw_lead_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.RAW_IRON).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.0F, 6.0F)));

    //public static final RegistryObject<ArcFurnaceBL> ARC_FURNACE_BL = registerWithItem("arc_furnace", ArcFurnaceBL::new);

    //public static final RegistryObject<ProjectorBL> PROJECTOR_BL = registerWithItem("projector", ProjectorBL::new);


    /*
    public static <I extends Block> RegistryObject<I> registerWithItem(String name, I block){
        RegistryObject<I> object = BLOCKS.register(name,() -> block);
        RegistryObject<Item> blockItem = MillenniumItems.ITEMS.register(name, () -> new BlockItem(object.get(), new Item.Properties()));
        MillenniumItems.ENGINEERING_PARTS.add(blockItem);
        return object;
    }*/

    public static <I extends Block> RegistryObject<I> registerWithItem(String name, Supplier<I> supplier, ArrayList<RegistryObject<Item>> arrayList){
        RegistryObject<I> object = BLOCKS.register(name, supplier);
        RegistryObject<Item> blockItem = MillenniumItems.ITEMS.register(name, () -> new BlockItem(object.get(), new Item.Properties()));
        arrayList.add(blockItem);
        return object;
    }

    public static <I extends Block> RegistryObject<I> registerWithItem(String name, Supplier<I> supplier){
        RegistryObject<I> object = BLOCKS.register(name, supplier);
        RegistryObject<Item> blockItem = MillenniumItems.ITEMS.register(name, () -> new BlockItem(object.get(), new Item.Properties()));
        MillenniumItems.MATERIALS.add(blockItem);
        return object;
    }
}


