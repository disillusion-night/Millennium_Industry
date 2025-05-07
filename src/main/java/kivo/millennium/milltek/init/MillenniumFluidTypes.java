package kivo.millennium.milltek.init;

import static kivo.millennium.milltek.Main.MODID;

import kivo.millennium.milltek.fluid.*;
import kivo.millennium.milltek.fluid.fluidType.*;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MillenniumFluidTypes {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, MODID);

    public static final RegistryObject<FluidType> ICY_WATER_FLUID_TYPE = FLUID_TYPES.register("icy_water", () -> new IcyWaterFluidType(FluidType.Properties.create()));

    public static final RegistryObject<FluidType> MOLTEN_ALUMINUM_FT = FLUID_TYPES.register("molten_aluminum", MoltenAluminumFL.FT::new);

    public static final RegistryObject<FluidType> MOLTEN_CRYOLITE_FT = FLUID_TYPES.register("molten_cryolite", MoltenCryoliteFL.FT::new);

    public static final RegistryObject<FluidType> RAW_MOLTEN_ALUMINUM_FT = FLUID_TYPES.register("raw_molten_aluminum", RawMoltenAluminumFL.FT::new);

    public static final RegistryObject<FluidType> MOLTEN_IRON_FT = FLUID_TYPES.register("molten_iron", MoltenIronFL.FT::new);

    public static final RegistryObject<FluidType> MOLTEN_STEEL_FT = FLUID_TYPES.register("molten_steel", MoltenSteelFL.FT::new);

    public static final RegistryObject<FluidType> MOLTEN_ALUMINUM_ALLOY_FT = FLUID_TYPES.register("molten_aluminum_alloy", MoltenAluminumAlloyFL.FT::new);

}
