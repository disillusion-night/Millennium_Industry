package kivo.millennium.milltek.init;

import static kivo.millennium.milltek.Main.MODID;

import org.antlr.v4.parse.ANTLRParser.finallyClause_return;

import kivo.millennium.milltek.block.fluidContainer.MetalFluidTankBE;
import kivo.millennium.milltek.block.hypercube.HDECBE;
import kivo.millennium.milltek.block.laser.NetherStarLaserBE;
import kivo.millennium.milltek.block.laser.SolarGeneratorBE;
import kivo.millennium.milltek.machine.Crusher.CrusherBE;
import kivo.millennium.milltek.machine.Crystallizer.CrystallizerBE;
import kivo.millennium.milltek.machine.Electrolyzer.ElectrolyzerBE;
import kivo.millennium.milltek.machine.FusionChamber.FusionChamberBE;
import kivo.millennium.milltek.machine.HydraulicPress.HydraulicPressBE;
import kivo.millennium.milltek.machine.InductionFurnace.InductionFurnaceBE;
import kivo.millennium.milltek.machine.MeltingFurnace.MeltingFurnaceBE;
import kivo.millennium.milltek.machine.ResonanceChamber.ResonanceChamberBE;
import kivo.millennium.milltek.pipe.client.CopperPipeBE;
import kivo.millennium.milltek.pipe.client.EnergyPipeBE;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MillenniumBlockEntities {
        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
                        .create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

        public static final RegistryObject<BlockEntityType<ElectrolyzerBE>> ELECTROLYZER_BE = BLOCK_ENTITIES
                        .register("electrolyzer_be", () -> BlockEntityType.Builder.of(ElectrolyzerBE::new,MillenniumBlocks.ELECTROLYZER.get()).build(null));

        public static final RegistryObject<BlockEntityType<CopperPipeBE>> COPPER_PIPE_BE = BLOCK_ENTITIES.register(
                        "copper_pipe_be", () -> BlockEntityType.Builder.of(CopperPipeBE::new,
                                        MillenniumBlocks.COPPER_PIPE.get()).build(null));

        public static final RegistryObject<BlockEntityType<MetalFluidTankBE>> METAL_FLUID_TANK_BE = BLOCK_ENTITIES
                        .register(
                                        "metal_tank_be", () -> BlockEntityType.Builder.of(MetalFluidTankBE::new,
                                                        MillenniumBlocks.METAL_TANK_BL.get()).build(null));

        public static final RegistryObject<BlockEntityType<HDECBE>> HDEC_BE = BLOCK_ENTITIES.register(
                        "hdec_be", () -> BlockEntityType.Builder.of(HDECBE::new,
                                        MillenniumBlocks.HDEC_BL.get()).build(null));

        public static final RegistryObject<BlockEntityType<NetherStarLaserBE>> NETHER_STAR_LASER_BE = BLOCK_ENTITIES
                        .register(
                                        "nether_star_laser_be", () -> BlockEntityType.Builder.of(NetherStarLaserBE::new,
                                                        MillenniumBlocks.NETHER_STAR_LASER_BL.get()).build(null));

        public static final RegistryObject<BlockEntityType<InductionFurnaceBE>> INDUCTION_FURNACE_BE = BLOCK_ENTITIES
                        .register(
                                        "induction_furnace_be", () -> BlockEntityType.Builder
                                                        .of(InductionFurnaceBE::new,
                                                                        MillenniumBlocks.INDUCTION_FURNACE_BL.get())
                                                        .build(null));

        public static final RegistryObject<BlockEntityType<CrusherBE>> Crusher_BE = BLOCK_ENTITIES.register(
                        "crusher_be", () -> BlockEntityType.Builder.of(CrusherBE::new,
                                        MillenniumBlocks.CRUSHER_BL.get()).build(null));

        /*
         * public static final RegistryObject<BlockEntityType<MolecularReformerBE>>
         * MOLECULAR_REFORMER_BE = BLOCK_ENTITIES.register(
         * "molecular_reformer_be", () ->
         * BlockEntityType.Builder.of(MolecularReformerBE::new,
         * MillenniumBlocks.MOLECULAR_REFORMER_BL.get()).build(null));
         */


        public static final RegistryObject<BlockEntityType<ResonanceChamberBE>> RESONANCE_CHAMBER_BE = BLOCK_ENTITIES
                        .register(
                                        "resonance_chamber_be", () -> BlockEntityType.Builder
                                                        .of(ResonanceChamberBE::new,
                                                                        MillenniumBlocks.RESONANCE_CHAMBER_BL.get())
                                                        .build(null));

        public static final RegistryObject<BlockEntityType<CrystallizerBE>> CRYSTALLIZER_BE = BLOCK_ENTITIES.register(
                        "crystallizer_be", () -> BlockEntityType.Builder.of(CrystallizerBE::new,
                                        MillenniumBlocks.CRYSTALLIZER_BL.get()).build(null));

        public static final RegistryObject<BlockEntityType<MeltingFurnaceBE>> MELTING_FURNACE_BE = BLOCK_ENTITIES
                        .register(
                                        "melting_furnace_be", () -> BlockEntityType.Builder.of(MeltingFurnaceBE::new,
                                                        MillenniumBlocks.MELTING_FURNACE_BL.get()).build(null));

        public static final RegistryObject<BlockEntityType<FusionChamberBE>> FUSION_CHAMBER_BE = BLOCK_ENTITIES
                        .register(
                                        "fusion_chamber_be", () -> BlockEntityType.Builder.of(FusionChamberBE::new,
                                                        MillenniumBlocks.FUSION_CHAMBER_BL.get()).build(null));

        public static final RegistryObject<BlockEntityType<HydraulicPressBE>> HYDRAULIC_PRESS_BE = BLOCK_ENTITIES
                        .register(
                                        "hydraulic_press_be", () -> BlockEntityType.Builder.of(HydraulicPressBE::new,
                                                        MillenniumBlocks.HYDRAULIC_PRESS_BL.get()).build(null));

        public static final RegistryObject<BlockEntityType<SolarGeneratorBE>> SOLAR_GENERATOR_BE = BLOCK_ENTITIES
                        .register(
                                        "solar_generator_be", () -> BlockEntityType.Builder.of(SolarGeneratorBE::new,
                                                        MillenniumBlocks.SOLAR_GENERATOR.get()).build(null));

        public static final RegistryObject<BlockEntityType<EnergyPipeBE>> ENERGY_PIPE_BE = BLOCK_ENTITIES.register(
                        "energy_pipe_be", () -> BlockEntityType.Builder.of(EnergyPipeBE::new,
                                        MillenniumBlocks.ENERGY_PIPE.get()).build(null));

}
