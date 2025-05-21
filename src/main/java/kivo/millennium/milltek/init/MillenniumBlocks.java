package kivo.millennium.milltek.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static kivo.millennium.milltek.Main.MODID;

import java.util.ArrayList;
import java.util.function.Supplier;

import kivo.millennium.milltek.block.fluid.*;
import kivo.millennium.milltek.block.fluidContainer.MetalTankBL;
import kivo.millennium.milltek.block.hypercube.HDECBL;
import kivo.millennium.milltek.block.laser.NetherStarLaserBL;
import kivo.millennium.milltek.block.laser.SolarGeneratorBL;
import kivo.millennium.milltek.machine.Crusher.CrusherBL;
import kivo.millennium.milltek.machine.Crystallizer.CrystallizerBL;
import kivo.millennium.milltek.machine.Electrolyzer.ElectrolyzerBL;
import kivo.millennium.milltek.machine.FusionChamber.FusionChamberBL;
import kivo.millennium.milltek.machine.HydraulicPress.HydraulicPressBL;
import kivo.millennium.milltek.machine.InductionFurnace.InductionFurnaceBL;
import kivo.millennium.milltek.machine.MeltingFurnace.MeltingFurnaceBL;
import kivo.millennium.milltek.machine.ResonanceChamber.ResonanceChamberBL;
import kivo.millennium.milltek.pipe.client.CopperPipeBlock;
import kivo.millennium.milltek.pipe.client.EnergyPipeBE;
import kivo.millennium.milltek.pipe.client.EnergyPipeBlock;

public class MillenniumBlocks {
        public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

        public static final RegistryObject<ElectrolyzerBL> ELECTROLYZER = registerWithItem("electrolyzer",
                        ElectrolyzerBL::new, MillenniumItems.ENGINEERING_PARTS);

        public static final RegistryObject<LiquidBlock> ICY_WATER_BL = BLOCKS.register("icy_water", IcyWaterBlock::new);

        public static final RegistryObject<LiquidBlock> MOLTEN_ALUMINUM_BL = BLOCKS.register("molten_aluminum",
                        MoltenAluminumBL::new);
        public static final RegistryObject<LiquidBlock> MOLTEN_ALUMINUM_ALLOY_BL = BLOCKS.register(
                        "molten_aluminum_alloy",
                        MoltenAluminumAlloyBL::new);
        public static final RegistryObject<LiquidBlock> MOLTEN_IRON_BL = BLOCKS.register("molten_iron",
                        MoltenIronBL::new);
        public static final RegistryObject<LiquidBlock> MOLTEN_STEEL_BL = BLOCKS.register("molten_steel",
                        MoltenSteelBL::new);
        public static final RegistryObject<LiquidBlock> MOLTEN_CRYOLITE_BL = BLOCKS.register("molten_cryolite",
                        MoltenCryoliteBL::new);
        public static final RegistryObject<LiquidBlock> RAW_MOLTEN_ALUMINUM_BL = BLOCKS.register("raw_molten_aluminum",
                        RawMoltenAluminumBL::new);

        public static final RegistryObject<MetalTankBL> METAL_TANK_BL = registerWithItem("metal_tank", MetalTankBL::new,
                        MillenniumItems.ENGINEERING_PARTS);
        public static final RegistryObject<HDECBL> HDEC_BL = registerWithItem("hdec", HDECBL::new,
                        MillenniumItems.ENGINEERING_PARTS);

        public static final RegistryObject<NetherStarLaserBL> NETHER_STAR_LASER_BL = registerWithItem(
                        "nether_star_laser",
                        NetherStarLaserBL::new, MillenniumItems.ENGINEERING_PARTS);
        public static final RegistryObject<InductionFurnaceBL> INDUCTION_FURNACE_BL = registerWithItem(
                        "induction_furnace",
                        InductionFurnaceBL::new, MillenniumItems.ENGINEERING_PARTS);
        public static final RegistryObject<MeltingFurnaceBL> MELTING_FURNACE_BL = registerWithItem("melting_furnace",
                        MeltingFurnaceBL::new, MillenniumItems.ENGINEERING_PARTS);
        public static final RegistryObject<FusionChamberBL> FUSION_CHAMBER_BL = registerWithItem("fusion_chamber",
                        FusionChamberBL::new, MillenniumItems.ENGINEERING_PARTS);
        public static final RegistryObject<HydraulicPressBL> HYDRAULIC_PRESS_BL = registerWithItem("hydraulic_press",
                        HydraulicPressBL::new, MillenniumItems.ENGINEERING_PARTS);

        public static final RegistryObject<CrusherBL> CRUSHER_BL = registerWithItem("crusher", CrusherBL::new,
                        MillenniumItems.ENGINEERING_PARTS);

        public static final RegistryObject<ResonanceChamberBL> RESONANCE_CHAMBER_BL = registerWithItem(
                        "resonance_chamber",
                        ResonanceChamberBL::new, MillenniumItems.ENGINEERING_PARTS);

        public static final RegistryObject<CrystallizerBL> CRYSTALLIZER_BL = registerWithItem("crystallizer",
                        CrystallizerBL::new, MillenniumItems.ENGINEERING_PARTS);

        public static final RegistryObject<SolarGeneratorBL> SOLAR_GENERATOR = registerWithItem("solar_generator",
                        SolarGeneratorBL::new, MillenniumItems.ENGINEERING_PARTS);

        public static final RegistryObject<Block> STEEL_BLOCK = registerWithItem("steel_block",
                        () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                                        .instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops()
                                        .strength(6.0F, 8.0F)
                                        .sound(SoundType.METAL)));
        public static final RegistryObject<Block> ALUMINUM_ALLOY_BLOCK = registerWithItem("aluminum_alloy_block",
                        () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                                        .instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops()
                                        .strength(7.0F, 10.0F)
                                        .sound(SoundType.METAL)));
        public static final RegistryObject<Block> TITANIUM_ALLOY_BLOCK = registerWithItem("titanium_alloy_block",
                        () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                                        .instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops()
                                        .strength(7.0F, 10.0F)
                                        .sound(SoundType.METAL)));
        public static final RegistryObject<Block> WOLFRAM_STEEL_BLOCK = registerWithItem("wolfram_steel_block",
                        () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                                        .instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops()
                                        .strength(7.0F, 10.0F)
                                        .sound(SoundType.METAL)));

        public static final RegistryObject<CopperPipeBlock> COPPER_PIPE = registerWithItem("copper_pipe",
                        CopperPipeBlock::new, MillenniumItems.ENGINEERING_PARTS);

        public static final RegistryObject<Block> LEAD_ORE = registerWithItem("lead_ore",
                        () -> new DropExperienceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                                        .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops()
                                        .strength(3.0F, 3.0F)));
        public static final RegistryObject<Block> DEEPSLATE_LEAD_ORE = registerWithItem("deepslate_lead_ore",
                        () -> new DropExperienceBlock(
                                        BlockBehaviour.Properties.copy(LEAD_ORE.get()).mapColor(MapColor.DEEPSLATE)
                                                        .strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)));
        public static final RegistryObject<Block> LEAD_BLOCK = registerWithItem("lead_block",
                        () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                                        .instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops()
                                        .strength(5.0F, 6.0F)
                                        .sound(SoundType.METAL)));
        public static final RegistryObject<Block> RAW_LEAD_BLOCK = registerWithItem("raw_lead_block",
                        () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.RAW_IRON)
                                        .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops()
                                        .strength(4.0F, 6.0F)));

        public static final RegistryObject<Block> ALUMINUM_ORE = registerWithItem("aluminum_ore",
                        () -> new DropExperienceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                                        .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops()
                                        .strength(3.0F, 3.0F)));
        public static final RegistryObject<Block> DEEPSLATE_ALUMINUM_ORE = registerWithItem("deepslate_aluminum_ore",
                        () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(ALUMINUM_ORE.get())
                                        .mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)));
        public static final RegistryObject<Block> ALUMINUM_BLOCK = registerWithItem("aluminum_block",
                        () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                                        .instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops()
                                        .strength(5.0F, 6.0F)
                                        .sound(SoundType.METAL)));
        public static final RegistryObject<Block> RAW_ALUMINUM_BLOCK = registerWithItem("raw_aluminum_block",
                        () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.RAW_IRON)
                                        .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops()
                                        .strength(5.0F, 6.0F)));

        public static final RegistryObject<Block> ALERT_BLOCK = registerWithItem("alert_block",
                        () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                                        .instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops()
                                        .strength(5.0F, 6.0F)
                                        .sound(SoundType.STONE)));

        public static final RegistryObject<EnergyPipeBlock> ENERGY_PIPE = registerWithItem("energy_pipe",
                        EnergyPipeBlock::new, MillenniumItems.ENGINEERING_PARTS);

        public static <I extends Block> RegistryObject<I> registerWithItem(String name, Supplier<I> supplier,
                        ArrayList<RegistryObject<Item>> arrayList) {
                RegistryObject<I> object = BLOCKS.register(name, supplier);
                RegistryObject<Item> blockItem = MillenniumItems.ITEMS.register(name,
                                () -> new BlockItem(object.get(), new Item.Properties()));
                arrayList.add(blockItem);
                return object;
        }

        public static <I extends Block> RegistryObject<I> registerWithItem(String name, Supplier<I> supplier) {
                RegistryObject<I> object = BLOCKS.register(name, supplier);
                RegistryObject<Item> blockItem = MillenniumItems.ITEMS.register(name,
                                () -> new BlockItem(object.get(), new Item.Properties()));
                MillenniumItems.MATERIALS.add(blockItem);
                return object;
        }
}
