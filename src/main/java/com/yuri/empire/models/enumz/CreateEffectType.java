package com.yuri.empire.models.enumz;

public enum CreateEffectType implements BaseEnum{
    OWNER,
    TARGET;
    @Override
    public int valueSize() {
        return values().length;
    }
}
