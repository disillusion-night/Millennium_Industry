package kivo.millennium.millind.init;

import kivo.millennium.millind.item.Oopart.Oopart;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static kivo.millennium.millind.Main.MODID;


public class MillenniumCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> OOPARTS = CREATIVE_MODE_TABS.register("ooparts",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable(getCreativeTabTranslationKey("ooparts")))
                    .icon(() -> MillenniumItems.HighPurityWolfseggSteel.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        for (RegistryObject<Oopart> oopart : MillenniumItems.OOPARTS){
                            output.accept(oopart.get());
                        }
                    }).build());


    public static final RegistryObject<CreativeModeTab> ENGINEERING_PARTS = CREATIVE_MODE_TABS.register("engineering_parts",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable(getCreativeTabTranslationKey("engineering_parts")))
                    .icon(() -> MillenniumItems.LowPurityWolfseggSteel.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        for (RegistryObject<Item> item : MillenniumItems.ENGINEERING_PARTS){
                            output.accept(item.get());
                        }
                    }).build());



    public static final RegistryObject<CreativeModeTab> MATERIALS = CREATIVE_MODE_TABS.register("material",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable(getCreativeTabTranslationKey("material")))
                    .icon(() -> MillenniumItems.LEAD_INGOT.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        for (RegistryObject<Item> item : MillenniumItems.MATERIALS){
                            output.accept(item.get());
                        }
                    }).build());

    public static final RegistryObject<CreativeModeTab> TOOLS = CREATIVE_MODE_TABS.register("tool",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable(getCreativeTabTranslationKey("tool")))
                    .icon(() -> MillenniumItems.WOLFRAM_STEEL_PICKAXE.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        for (RegistryObject<Item> item : MillenniumItems.TOOLS){
                            output.accept(item.get());
                        }
                    }).build());

    public static final String getCreativeTabTranslationKey(String name){
        return "item_group." + MODID + "."+ name;
    }
}

