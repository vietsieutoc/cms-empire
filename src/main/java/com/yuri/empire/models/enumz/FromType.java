package com.yuri.empire.models.enumz;

public enum FromType implements BaseEnum {
    KINGDOM,
    MERC;

    @Override
    public int valueSize() {
        return values().length;
    }
}
