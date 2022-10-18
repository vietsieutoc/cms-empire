package com.yuri.empire.models.enumz;

public enum AchievementType implements BaseEnum{
    POWER_UP,
    REGION,
    GOLD,
    SUMMON,
    EMPLOYED,
    LANDLORD,
    TRADER,
    PVP,
    PRACTICE_MODE,
    RANK_MODE,
    CUSTOM_MODE,
    RANK;

    @Override
    public int valueSize() {
        return values().length;
    }
}
