package kivo.millennium.milltek.init;

import static kivo.millennium.milltek.Main.MODID;
import static kivo.millennium.milltek.Main.getKey;

import kivo.millennium.milltek.Main;
import kivo.millennium.milltek.entity.special.BlackHole;
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

}
