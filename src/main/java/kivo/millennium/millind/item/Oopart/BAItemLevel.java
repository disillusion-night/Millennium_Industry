package kivo.millennium.millind.item.Oopart;

import net.minecraft.ChatFormatting;

import java.util.Map;

public  enum BAItemLevel {
    SSR, SR, R, N;

    private static Map<BAItemLevel, ChatFormatting> Level2Styles = Map.of(
            N, ChatFormatting.WHITE,
            R, ChatFormatting.AQUA,
            SR, ChatFormatting.GOLD,
            SSR, ChatFormatting.LIGHT_PURPLE
    );
    public ChatFormatting getStyleModifier(BAItemLevel level){
        return Level2Styles.get(level);
    }
}
