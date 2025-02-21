package kivo.millennium.millind.helper;

public class TintHelper {
    public static int RGBA2Tint(int red, int green, int blue, int alpha){
        return ( ( alpha << 8 + red ) << 8 + green ) << 8 + blue;
    }
}
