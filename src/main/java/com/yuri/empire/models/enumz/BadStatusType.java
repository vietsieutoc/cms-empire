package com.yuri.empire.models.enumz;

public enum BadStatusType implements BaseEnum {
    FIRE,
    STUN,
    PARALYZE,
    DOWN_HP_PERCENT;

    @Override
    public int valueSize() {
        return values().length;
    }
}
