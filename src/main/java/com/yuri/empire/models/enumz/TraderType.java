package com.yuri.empire.models.enumz;

public enum TraderType implements BaseEnum {
    HERO,
    MINION,
    ITEM;

    @Override
    public int valueSize() {
        return values().length;
    }
}
