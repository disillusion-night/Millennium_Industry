package kivo.millennium.milltek.entity.special;

/**
 * 枚举所有可发射的导弹类型（用于命令参数与调试发射）。
 */
public enum MissileType {
    AIR("air"),
    GROUND("ground");

    private final String name;

    MissileType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static MissileType fromName(String s) {
        for (MissileType t : values()) if (t.name.equalsIgnoreCase(s)) return t;
        return null;
    }
}

