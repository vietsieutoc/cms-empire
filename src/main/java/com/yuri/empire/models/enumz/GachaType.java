package com.yuri.empire.models.enumz;

public enum GachaType implements BaseEnum{
    HERO_NORMAL,
    HERO_RARE,
    HERO_MYTHIC,
    MINION_NORMAL,
    MINION_RARE,
    MINION_MYTHIC;
    @Override
    public int valueSize() {
        return values().length;
    }
}
