package com.yuri.empire.models.enumz;

public enum BuffStatusType implements BaseEnum{
    UP_HP,
    UP_MAX_HP,
    UP_ATK;

    @Override
    public int valueSize() {
        return values().length;
    }
}
