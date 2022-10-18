
package com.yuri.empire.models.enumz;

public enum JobType implements BaseEnum {
    WARRIOR,
    BEAST,
    UNDEAD,
    ROUGE,
    MYSTIC,
    ORDER,
    CHAOS;

    @Override
    public int valueSize() {
        return values().length;
    }

    public <T> T valueOf(String type, Class<T> clazz) {
        return (T) JobType.valueOf(type);
    }
}
