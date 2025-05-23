package kivo.millennium.millind.init;

import kivo.millennium.millind.machine.FusionChamber.FusionChamberBE;
import kivo.millennium.millind.machine.HydraulicPress.HydraulicPressBE;
import kivo.millennium.millind.machine.MeltingFurnace.MeltingFurnaceBE;
import kivo.millennium.millind.machine.PipeBooster.PipeBoosterBE;
import kivo.millennium.millind.machine.ResonanceChamber.ResonanceChamberBE;
import kivo.millennium.millind.machine.Crusher.CrusherBE;
import kivo.millennium.millind.machine.Crystallizer.CrystallizerBE;
import kivo.millennium.millind.block.fluidContainer.MetalFluidTankBE;
import kivo.millennium.millind.block.hypercube.HDECBE;
import kivo.millennium.millind.machine.InductionFurnace.InductionFurnaceBE;
import kivo.millennium.millind.block.laser.NetherStarLaserBE;
import kivo.millennium.millind.block.laser.SolarGeneratorBE;
import kivo.millennium.millind.pipe.client.FluidPipeBE;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static kivo.millennium.millind.Main.MODID;

public class MillenniumBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

    public static final RegistryObject<BlockEntityType<MetalFluidTankBE>> METAL_FLUID_TANK_BE = BLOCK_ENTITIES.register(
            "metal_tank_be", () -> BlockEntityType.Builder.of(MetalFluidTankBE::new,
                            MillenniumBlocks.METAL_TANK_BL.get()).build(null));

    public static final RegistryObject<BlockEntityType<FluidPipeBE>> FLUID_PIPE_BE = BLOCK_ENTITIES.register(
            "fluid_pipe_be", () -> BlockEntityType.Builder.of(FluidPipeBE::new,
                    MillenniumBlocks.IRON_FLUID_PIPE.get()).build(null));


    public static final RegistryObject<BlockEntityType<HDECBE>> HDEC_BE = BLOCK_ENTITIES.register(
            "hdec_be", () -> BlockEntityType.Builder.of(HDECBE::new,
                    MillenniumBlocks.HDEC_BL.get()).build(null));

    public static final RegistryObject<BlockEntityType<NetherStarLaserBE>> NETHER_STAR_LASER_BE = BLOCK_ENTITIES.register(
            "nether_star_laser_be", () -> BlockEntityType.Builder.of(NetherStarLaserBE::new,
                    MillenniumBlocks.NETHER_STAR_LASER_BL.get()).build(null));

    public static final RegistryObject<BlockEntityType<InductionFurnaceBE>> INDUCTION_FURNACE_BE = BLOCK_ENTITIES.register(
            "induction_furnace_be", () -> BlockEntityType.Builder.of(InductionFurnaceBE::new,
                    MillenniumBlocks.INDUCTION_FURNACE_BL.get()).build(null));

    public static final RegistryObject<BlockEntityType<CrusherBE>> Crusher_BE = BLOCK_ENTITIES.register(
            "crusher_be", () -> BlockEntityType.Builder.of(CrusherBE::new,
                    MillenniumBlocks.CRUSHER_BL.get()).build(null));

    /*public static final RegistryObject<BlockEntityType<MolecularReformerBE>> MOLECULAR_REFORMER_BE = BLOCK_ENTITIES.register(
            "molecular_reformer_be", () -> BlockEntityType.Builder.of(MolecularReformerBE::new,
                    MillenniumBlocks.MOLECULAR_REFORMER_BL.get()).build(null));*/

    public static final RegistryObject<BlockEntityType<PipeBoosterBE>> PIPE_BOOSTER_BE = BLOCK_ENTITIES.register(
            "pipe_booster_be", () -> BlockEntityType.Builder.of(PipeBoosterBE::new,
                    MillenniumBlocks.RESONANCE_CHAMBER_BL.get()).build(null));

    public static final RegistryObject<BlockEntityType<ResonanceChamberBE>> RESONANCE_CHAMBER_BE = BLOCK_ENTITIES.register(
            "resonance_chamber_be", () -> BlockEntityType.Builder.of(ResonanceChamberBE::new,
                    MillenniumBlocks.RESONANCE_CHAMBER_BL.get()).build(null));

    public static final RegistryObject<BlockEntityType<CrystallizerBE>> CRYSTALLIZER_BE = BLOCK_ENTITIES.register(
            "crystallizer_be", () -> BlockEntityType.Builder.of(CrystallizerBE::new,
                    MillenniumBlocks.CRYSTALLIZER_BL.get()).build(null));

    public static final RegistryObject<BlockEntityType<MeltingFurnaceBE>> MELTING_FURNACE_BE = BLOCK_ENTITIES.register(
            "melting_furnace_be", () -> BlockEntityType.Builder.of(MeltingFurnaceBE::new,
                    MillenniumBlocks.MELTING_FURNACE_BL.get()).build(null));

    public static final RegistryObject<BlockEntityType<FusionChamberBE>> FUSION_CHAMBER_BE = BLOCK_ENTITIES.register(
            "fusion_chamber_be", () -> BlockEntityType.Builder.of(FusionChamberBE::new,
                    MillenniumBlocks.FUSION_CHAMBER_BL.get()).build(null));

    public static final RegistryObject<BlockEntityType<HydraulicPressBE>> HYDRAULIC_PRESS_BE = BLOCK_ENTITIES.register(
            "hydraulic_press_be", () -> BlockEntityType.Builder.of(HydraulicPressBE::new,
                    MillenniumBlocks.HYDRAULIC_PRESS_BL.get()).build(null));

    public static final RegistryObject<BlockEntityType<SolarGeneratorBE>> SOLAR_GENERATOR_BE = BLOCK_ENTITIES.register(
            "solar_generator_be", () -> BlockEntityType.Builder.of(SolarGeneratorBE::new,
                    MillenniumBlocks.SOLAR_GENERATOR.get()).build(null));


}
