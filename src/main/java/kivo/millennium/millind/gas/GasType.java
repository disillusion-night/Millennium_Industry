package kivo.millennium.millind.gas;

public class GasType {
    private final int tintColor;

    public GasType(int tintColor){
        this.tintColor = tintColor;
    }

    public int getTintColor() {
        return tintColor;
    }


    public int getColor() {
        return tintColor & 0x00ffffff;
    }
}
