package kivo.millennium.millind.init;

import kivo.millennium.millind.block.projector.ProjectorMenu;
import kivo.millennium.millind.item.Oopart.Oopart;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static kivo.millennium.millind.Main.MODID;

public class MillenniumMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, MODID);

    public static final RegistryObject<MenuType<ProjectorMenu>> PROJECTOR_MENU = MENU_TYPES.register("projector_menu",
            () -> IForgeMenuType.create(ProjectorMenu::new));


}
