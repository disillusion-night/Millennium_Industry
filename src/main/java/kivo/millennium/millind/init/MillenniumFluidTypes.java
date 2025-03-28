package kivo.millennium.millind.init;

import kivo.millennium.millind.fluid.MoltenAluminumAlloyFL;
import kivo.millennium.millind.fluid.fluidType.*;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static kivo.millennium.millind.Main.MODID;

public class MillenniumFluidTypes {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, MODID);

    public static final RegistryObject<FluidType> ICY_WATER_FLUID_TYPE = FLUID_TYPES.register("icy_water", () -> new IcyWaterFluidType(FluidType.Properties.create()));


    public static final RegistryObject<FluidType> MOLTEN_ALUMINUM_FT = FLUID_TYPES.register("molten_aluminum", () -> new MoltenAluminumFT(FluidType.Properties.create()));

    public static final RegistryObject<FluidType> MOLTEN_CRYOLITE_FT = FLUID_TYPES.register("molten_cryolite", () -> new MoltenCryoliteFT(FluidType.Properties.create()));

    public static final RegistryObject<FluidType> RAW_MOLTEN_ALUMINUM_FT = FLUID_TYPES.register("raw_molten_aluminum", () -> new RawMoltenAluminumFT(FluidType.Properties.create()));

    public static final RegistryObject<FluidType> MOLTEN_IRON_FT = FLUID_TYPES.register("molten_iron", () -> new MoltenIronFT(FluidType.Properties.create()));

    public static final RegistryObject<FluidType> MOLTEN_STEEL_FT = FLUID_TYPES.register("molten_steel", () -> new MoltenSteelFT(FluidType.Properties.create()));

    public static final RegistryObject<FluidType> MOLTEN_ALUMINUM_ALLOY_FT = FLUID_TYPES.register("molten_aluminum_alloy", () -> new MoltenAluminumAlloyFL.FT(FluidType.Properties.create()));

}
