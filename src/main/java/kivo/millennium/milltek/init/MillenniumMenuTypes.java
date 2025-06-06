package kivo.millennium.milltek.init;

import static kivo.millennium.milltek.Main.MODID;

import kivo.millennium.milltek.container.Device.*;
import kivo.millennium.milltek.machine.Crusher.CrusherMenu;
import kivo.millennium.milltek.machine.Crystallizer.CrystallizerMenu;
import kivo.millennium.milltek.machine.Electrolyzer.ElectrolyzerMenu;
import kivo.millennium.milltek.machine.FusionChamber.FusionChamberMenu;
import kivo.millennium.milltek.machine.MeltingFurnace.MeltingFurnaceMenu;
import kivo.millennium.milltek.machine.ResonanceChamber.ResonanceChamberMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MillenniumMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, MODID);

    public static final RegistryObject<MenuType<InductionFurnaceMenu>> INDUCTION_FURNACE_MENU = MENU_TYPES.register(
            "induction_furnace_menu",
            () -> IForgeMenuType.create((windowId, inv, data) -> new InductionFurnaceMenu(windowId,
                    inv.player, data.readBlockPos())));

    public static final RegistryObject<MenuType<ElectrolyzerMenu>> ELECTOLYZER_MENU = MENU_TYPES.register(
            "electolyzer_menu",
            () -> IForgeMenuType.create((windowId, inv, data) -> new ElectrolyzerMenu(windowId,
                    inv.player, data.readBlockPos())));

    public static final RegistryObject<MenuType<MeltingFurnaceMenu>> MELTING_FURNACE_MENU = MENU_TYPES.register(
            "melting_furnace_menu",
            () -> IForgeMenuType.create((windowId, inv, data) -> new MeltingFurnaceMenu(windowId,
                    inv.player, data.readBlockPos())));

    public static final RegistryObject<MenuType<CrystallizerMenu>> CRYSTALLIZER_MENU = MENU_TYPES.register(
            "crystallizer_menu",
            () -> IForgeMenuType.create((windowId, inv, data) -> new CrystallizerMenu(windowId, inv.player,
                    data.readBlockPos())));

    public static final RegistryObject<MenuType<ResonanceChamberMenu>> RESONANCE_CHAMBER_MENU = MENU_TYPES.register(
            "resonance_chamber_menu",
            () -> IForgeMenuType.create((windowId, inv, data) -> new ResonanceChamberMenu(windowId,
                    inv.player, data.readBlockPos())));

    public static final RegistryObject<MenuType<FusionChamberMenu>> FUSION_FURNACE_MENU = MENU_TYPES.register(
            "fusion_furnace_menu",
            () -> IForgeMenuType.create((windowId, inv, data) -> new FusionChamberMenu(windowId, inv.player,
                    data.readBlockPos())));

    public static final RegistryObject<MenuType<HydraulicPressMenu>> HYDRAULIC_PRESS_MENU = MENU_TYPES.register(
            "hydraulic_press_menu",
            () -> IForgeMenuType.create((windowId, inv, data) -> new HydraulicPressMenu(windowId,
                    inv.player, data.readBlockPos())));

    public static final RegistryObject<MenuType<CrusherMenu>> CRUSHER_MENU = MENU_TYPES.register(
            "crusher_menu",
            () -> IForgeMenuType.create((windowId, inv, data) -> new CrusherMenu(windowId, inv.player,
                    data.readBlockPos())));
}
