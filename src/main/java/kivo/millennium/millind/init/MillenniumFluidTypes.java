package kivo.millennium.millind.init;

import kivo.millennium.millind.fluid.fluidType.IcyWaterFluidType;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static kivo.millennium.millind.Main.MODID;

public class MillenniumFluidTypes {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, MODID);

    public static final RegistryObject<FluidType> ICY_WATER_FLUID_TYPE = FLUID_TYPES.register("icy_water", () -> new IcyWaterFluidType(FluidType.Properties.create()));
}
