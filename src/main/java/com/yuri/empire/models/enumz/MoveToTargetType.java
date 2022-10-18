package com.yuri.empire.models.enumz;

public enum MoveToTargetType implements BaseEnum{
    NORMAL,
    FLASH;
    @Override
    public int valueSize() {
        return values().length;
    }
}
