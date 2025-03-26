package kivo.millennium.millind.init;

import kivo.millennium.millind.fluid.*;
import kivo.millennium.millind.fluid.fluidType.MoltenAluminumAlloyFT;
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

    public static final RegistryObject<FlowingFluid> MOLTEN_ALUMINUM = FLUIDS.register("molten_aluminum", MoltenAluminumFL.Source::new);
    public static final RegistryObject<FlowingFluid> FLOWING_MOLTEN_ALUMINUM = FLUIDS.register("flowing_molten_aluminum", MoltenAluminumFL.Flowing::new);

    public static final RegistryObject<FlowingFluid> MOLTEN_CRYOLITE = FLUIDS.register("molten_cryolite", MoltenCryoliteFL.Source::new);
    public static final RegistryObject<FlowingFluid> FLOWING_MOLTEN_CRYOLITE = FLUIDS.register("flowing_molten_cryolite", MoltenCryoliteFL.Flowing::new);

    public static final RegistryObject<FlowingFluid> RAW_MOLTEN_ALUMINUM = FLUIDS.register("raw_molten_aluminum", RawMoltenAluminumFL.Source::new);
    public static final RegistryObject<FlowingFluid> FLOWING_RAW_MOLTEN_ALUMINUM = FLUIDS.register("flowing_raw_molten_aluminum", RawMoltenAluminumFL.Flowing::new);

    public static final RegistryObject<FlowingFluid> MOLTEN_IRON = FLUIDS.register("molten_iron", MoltenIronFL.Source::new);
    public static final RegistryObject<FlowingFluid> FLOWING_MOLTEN_IRON = FLUIDS.register("flowing_molten_iron", MoltenIronFL.Flowing::new);

    public static final RegistryObject<FlowingFluid> MOLTEN_STEEL = FLUIDS.register("molten_steel", MoltenSteelFL.Source::new);
    public static final RegistryObject<FlowingFluid> FLOWING_MOLTEN_STEEL = FLUIDS.register("flowing_molten_steel", MoltenSteelFL.Flowing::new);

    public static final RegistryObject<FlowingFluid> MOLTEN_ALUMINUM_ALLOY = FLUIDS.register("molten_aluminum_alloy", MoltenAluminumAlloyFL.Source::new);
    public static final RegistryObject<FlowingFluid> FLOWING_MOLTEN_ALUMINUM_ALLOY = FLUIDS.register("flowing_molten_aluminum_alloy", MoltenAluminumAlloyFL.Flowing::new);

}
