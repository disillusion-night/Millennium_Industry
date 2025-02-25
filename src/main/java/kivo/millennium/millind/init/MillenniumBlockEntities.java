package kivo.millennium.millind.init;

import kivo.millennium.millind.block.projector.ProjectorBE;
import kivo.millennium.millind.block.fluidContainer.MetalFluidTankBE;
import kivo.millennium.millind.block.multiblock.controller.HMIBE;
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
    public static final RegistryObject<BlockEntityType<HMIBE>> HMI_BE = BLOCK_ENTITIES.register(
            "hmi_be", () -> BlockEntityType.Builder.of(HMIBE::new,
                    MillenniumBlocks.HMI_BL.get()).build(null));
    public static final RegistryObject<BlockEntityType<ProjectorBE>> PROJECTOR_BE = BLOCK_ENTITIES.register(
            "projector_be", () -> BlockEntityType.Builder.of(ProjectorBE::new,
                    MillenniumBlocks.HMI_BL.get()).build(null));

}
