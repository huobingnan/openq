package org.openq.vasp.bean;

/**
 * 坐标类型枚举类
 *  Fraction：分数坐标
 *  Cartesian：笛卡尔坐标
 */
public enum CoordinateType
{
    Fractional("Direct"),
    Cartesian("Cartesian");

    private final String key;

    CoordinateType(String key) { this.key = key; }

    public String getKey()
    {
        return key;
    }
}
