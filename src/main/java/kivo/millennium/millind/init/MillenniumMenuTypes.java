package kivo.millennium.millind.init;

import kivo.millennium.millind.container.GeneratorMT;
import kivo.millennium.millind.container.InductionFurnaceMT;
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


    public static final RegistryObject<MenuType<InductionFurnaceMT>> INDUCTION_FURNACE_MENU = MENU_TYPES.register(
            "induction_furnace_menu",
            () -> IForgeMenuType.create((windowId, inv, data) -> new InductionFurnaceMT(windowId, inv.player, data.readBlockPos()))
    );
}
