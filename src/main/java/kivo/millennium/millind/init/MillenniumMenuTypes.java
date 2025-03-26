package kivo.millennium.millind.init;

import kivo.millennium.millind.container.Device.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static kivo.millennium.millind.Main.MODID;

public class MillenniumMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, MODID);

    public static final RegistryObject<MenuType<GeneratorMT>> GENERATOR_MENU = MENU_TYPES.register(
            "generator_menu",
            () -> IForgeMenuType.create((windowId, inv, data) -> new GeneratorMT(windowId, inv.player, data.readBlockPos()))
    );


    public static final RegistryObject<MenuType<InductionFurnaceMenu>> INDUCTION_FURNACE_MENU = MENU_TYPES.register(
            "induction_furnace_menu",
            () -> IForgeMenuType.create((windowId, inv, data) -> new InductionFurnaceMenu(windowId, inv.player, data.readBlockPos()))
    );

    public static final RegistryObject<MenuType<MeltingFurnaceContainer>> MELTING_FURNACE_MENU = MENU_TYPES.register(
            "melting_furnace_menu",
            () -> IForgeMenuType.create((windowId, inv, data) -> new MeltingFurnaceContainer(windowId, inv.player, data.readBlockPos()))
    );
    public static final RegistryObject<MenuType<FusionFurnaceContainer>> FUSION_FURNACE_MENU = MENU_TYPES.register(
            "fusion_furnace_menu",
            () -> IForgeMenuType.create((windowId, inv, data) -> new FusionFurnaceContainer(windowId, inv.player, data.readBlockPos()))
    );

    public static final RegistryObject<MenuType<HydraulicPressMenu>> HYDRAULIC_PRESS_MENU = MENU_TYPES.register(
            "hydraulic_press_menu",
            () -> IForgeMenuType.create((windowId, inv, data) -> new HydraulicPressMenu(windowId, inv.player, data.readBlockPos()))
    );

    public static final RegistryObject<MenuType<CrusherContainer>> CRUSHER_CONTAINER = MENU_TYPES.register(
            "crusher_container",
            () -> IForgeMenuType.create((windowId, inv, data) -> new CrusherContainer(windowId, inv.player, data.readBlockPos()))
    );
}
