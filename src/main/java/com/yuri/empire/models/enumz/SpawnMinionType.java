package com.yuri.empire.models.enumz;

public enum SpawnMinionType implements BaseEnum{
    NORMAL,
    PERCENT;
    @Override
    public int valueSize() {
        return values().length;
    }
}
