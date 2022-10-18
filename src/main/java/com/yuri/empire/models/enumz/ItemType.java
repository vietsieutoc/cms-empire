package com.yuri.empire.models.enumz;

public enum ItemType implements BaseEnum{
    HERO_SHARD,
    MINION_SHARD,
    TOKEN,
    PORTION,
    EXP,
    CLOCK;
    @Override
    public int valueSize() {
        return values().length;
    }
}
