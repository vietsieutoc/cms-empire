package com.yuri.empire.models.enumz;

public enum DealDamageType implements BaseEnum{
    NORMAL,
    PERCENT,
    PERCENT_HP;
    @Override
    public int valueSize() {
        return values().length;
    }
}
