package com.yuri.empire.models.enumz;

public enum RarityType implements BaseEnum {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC,
    LEGEND,
    MYTHIC;

    @Override
    public int valueSize() {
        return values().length;
    }

    @Override
    public <T> T valueOf(String type, Class<T> clazz) {
        return (T) RarityType.valueOf(type);
    }
}
