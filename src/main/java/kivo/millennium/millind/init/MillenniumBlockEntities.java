package kivo.millennium.millind.init;

import kivo.millennium.millind.block.device.crusher.CrusherBE;
import kivo.millennium.millind.block.fluidContainer.MetalFluidTankBE;
import kivo.millennium.millind.block.generator.GeneratorBE;
import kivo.millennium.millind.block.hypercube.HDECBE;
import kivo.millennium.millind.block.device.inductionFurnace.InductionFurnaceBE;
import kivo.millennium.millind.block.laser.NetherStarLaserBE;
import kivo.millennium.millind.block.laser.SolarGeneratorBE;
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


    public static final RegistryObject<BlockEntityType<HDECBE>> HDEC_BE = BLOCK_ENTITIES.register(
            "hdec_be", () -> BlockEntityType.Builder.of(HDECBE::new,
                    MillenniumBlocks.HDEC_BL.get()).build(null));


    public static final RegistryObject<BlockEntityType<NetherStarLaserBE>> NETHER_STAR_LASER_BE = BLOCK_ENTITIES.register(
            "nether_star_laser_be", () -> BlockEntityType.Builder.of(NetherStarLaserBE::new,
                    MillenniumBlocks.NETHER_STAR_LASER_BL.get()).build(null));

    public static final RegistryObject<BlockEntityType<GeneratorBE>> GENERATOR_BE = BLOCK_ENTITIES.register(
            "generator_be", () -> BlockEntityType.Builder.of(GeneratorBE::new,
                    MillenniumBlocks.GENERATOR_BL.get()).build(null));


    public static final RegistryObject<BlockEntityType<InductionFurnaceBE>> INDUCTION_FURNACE_BE = BLOCK_ENTITIES.register(
            "induction_furnace_be", () -> BlockEntityType.Builder.of(InductionFurnaceBE::new,
                    MillenniumBlocks.INDUCTION_FURNACE_BL.get()).build(null));


    public static final RegistryObject<BlockEntityType<CrusherBE>> Crusher_BE = BLOCK_ENTITIES.register(
            "crusher_be", () -> BlockEntityType.Builder.of(CrusherBE::new,
                    MillenniumBlocks.CRUSHER_BL.get()).build(null));

    public static final RegistryObject<BlockEntityType<SolarGeneratorBE>> SOLAR_GENERATOR_BE = BLOCK_ENTITIES.register(
            "solar_generator_be", () -> BlockEntityType.Builder.of(SolarGeneratorBE::new,
                    MillenniumBlocks.SLOAR_GENERATOR_BL.get()).build(null));

    /*
    public static final RegistryObject<BlockEntityType<ArcFurnaceBE>> ARC_FURNACE_BE = BLOCK_ENTITIES.register(
            "arc_furnace_be", () -> BlockEntityType.Builder.of(ArcFurnaceBE::new,
                    MillenniumBlocks.ARC_FURNACE_BL.get()).build(null));
    */

}
