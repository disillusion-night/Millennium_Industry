package kivo.millennium.millind.init;

import kivo.millennium.millind.block.fluidContainer.MetalFluidTankEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import static kivo.millennium.millind.Main.MODID;

public class MillenniumBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

    public static final RegistryObject<BlockEntityType<MetalFluidTankEntity>> METAL_FLUID_TANK_ENTITY = BLOCK_ENTITIES.register(
            "metal_tank_entity", () -> BlockEntityType.Builder.of(MetalFluidTankEntity::new,
                            MillenniumBlocks.METAL_TANK_BLOCK.get()).build(null));
}
