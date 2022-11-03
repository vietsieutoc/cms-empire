package com.yuri.empire.models.enumz;

public enum PVPMode implements BaseEnum {
    NORMAL,
    BET,
    EVENT;

    @Override
    public int valueSize() {
        return values().length;
    }

    @Override
    public <T> T valueOf(String type, Class<T> clazz) {
        return (T) RarityType.valueOf(type);
    }
}
