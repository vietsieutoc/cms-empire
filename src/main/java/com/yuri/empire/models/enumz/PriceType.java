package com.yuri.empire.models.enumz;

public enum PriceType implements BaseEnum{
    TOKEN,
    GOLD;
    @Override
    public int valueSize() {
        return values().length;
    }
}
