package com.yuri.empire.models.enumz;

public enum ItemCurrency implements BaseEnum {
    GOLD,
    TOKEN;

    @Override
    public int valueSize() {
        return values().length;
    }

    @Override
    public <T> T valueOf(String type, Class<T> clazz) {
        return (T) ItemCurrency.valueOf(type);
    }
}
