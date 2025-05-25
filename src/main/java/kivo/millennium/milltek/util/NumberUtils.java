package kivo.millennium.milltek.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.List;

import kivo.millennium.milltek.Main;

public class NumberUtils {
    public static String int2String(int number){
        if (number < 1000) {
            return String.valueOf(number); // 小于 1000，直接返回字符串
        }

        String[] magnitudes = {"", "K", "M", "G", "T", "P", "E"}; // 数量级缩写
        int magnitudeIndex = 0;
        double num = number;

        while (num >= 1000) {
            num /= 1000;
            magnitudeIndex++;
        }

        return String.format("%.1f %s", num, magnitudes[magnitudeIndex]); // 格式化输出
    }

    public static CompoundTag BlockPos2NBT(BlockPos pos){
        CompoundTag tag = new CompoundTag();
        tag.putIntArray("pos", List.of(pos.getX(), pos.getY(), pos.getZ()));
        return tag;
    }

    public static BlockPos NBT2BlockPos(CompoundTag compoundTag){
        int[] pos = compoundTag.getIntArray("pos");
        return new BlockPos(pos[0], pos[1], pos[2]);
    }
}
