package kivo.millennium.millind.util;

import kivo.millennium.millind.Main;

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
}
