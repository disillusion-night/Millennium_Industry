package kivo.millennium.client.datagen.language;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

import static kivo.millennium.millind.Main.MODID;


public class MillenniumLanguageProvider extends LanguageProvider {

    public MillenniumLanguageProvider(PackOutput output, String locale) {
        super(output, MODID, locale);
    }

    @Override
    protected void addTranslations() {

    }
}
