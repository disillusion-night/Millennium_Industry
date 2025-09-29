package kivo.millennium.milltek.init;

import static kivo.millennium.milltek.Main.MODID;
import static kivo.millennium.milltek.Main.getKey;

import kivo.millennium.milltek.Main;
import kivo.millennium.milltek.entity.special.BlackHole;
import kivo.millennium.milltek.entity.special.NuclearTarget;
import kivo.millennium.milltek.entity.special.GroundTacticalMissile;
import kivo.millennium.milltek.entity.special.AirToAirMissile;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MillenniumEntities {
    public static DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);

    public static RegistryObject<EntityType<BlackHole>> BLACK_HOLE =
            ENTITIES.register("black_hole", () -> EntityType.Builder.<BlackHole>of(BlackHole::new, MobCategory.MISC)
                    .sized(11, 11)
                    .clientTrackingRange(64)
                    .build(Main.getRL("black_hole").toString()));

    public static RegistryObject<EntityType<NuclearTarget>> NUCLEAR_TARGET =
            ENTITIES.register("nuclear_target", () -> EntityType.Builder.<NuclearTarget>of(NuclearTarget::new, MobCategory.MISC)
                    .sized(1, 1)
                    .clientTrackingRange(256)
                    .build(Main.getRL("nuclear_target").toString()));

    public static RegistryObject<EntityType<GroundTacticalMissile>> GROUND_TACTICAL_MISSILE =
            ENTITIES.register("ground_tactical_missile", () -> EntityType.Builder.<GroundTacticalMissile>of(GroundTacticalMissile::new, MobCategory.MISC)
                    .sized(1.0f, 1.0f)
                    .clientTrackingRange(128)
                    .build(Main.getRL("ground_tactical_missile").toString()));

    public static RegistryObject<EntityType<AirToAirMissile>> AIR_TO_AIR_MISSILE =
            ENTITIES.register("air_to_air_missile", () -> EntityType.Builder.<AirToAirMissile>of(AirToAirMissile::new, MobCategory.MISC)
                    .sized(0.6f, 0.6f)
                    .clientTrackingRange(128)
                    .build(Main.getRL("air_to_air_missile").toString()));
}
