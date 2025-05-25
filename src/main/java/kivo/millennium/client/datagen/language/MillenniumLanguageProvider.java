package kivo.millennium.client.datagen.language;

import static kivo.millennium.milltek.Main.MODID;

import com.lowdragmc.lowdraglib.syncdata.accessor.BuiltinRegistryAccessor;

import kivo.millennium.milltek.gas.Gas;
import kivo.millennium.milltek.init.MillenniumGases;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.ForgeRegistries;

public class MillenniumLanguageProvider extends LanguageProvider {

    public MillenniumLanguageProvider(PackOutput output, String locale) {
        super(output, MODID, locale);
    }

    @Override
    protected void addTranslations() {

    }

    public void add(Gas gas, String value) {
        add(MillenniumGases.getRL(gas).toLanguageKey("gas"), value);
    }

    public void add(CreativeModeTab tab, String value) {
        add("item_group." + MODID + "." + BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab).getPath(), value);
    }
}
