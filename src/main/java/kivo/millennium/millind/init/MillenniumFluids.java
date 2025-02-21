package kivo.millennium.millind.init;

import kivo.millennium.millind.fluid.IcyWaterFluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static kivo.millennium.millind.Main.MODID;

public class MillenniumFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, MODID);

    public static final RegistryObject<FlowingFluid> ICY_WATER = FLUIDS.register("icy_water", IcyWaterFluid.Source::new);
    public static final RegistryObject<FlowingFluid> FLOWING_ICY_WATER = FLUIDS.register("flowing_icy_water", IcyWaterFluid.Flowing::new);

}
