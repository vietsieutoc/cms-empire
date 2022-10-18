package com.yuri.empire.models.enumz;

public enum SkillType implements BaseEnum {
    ATK1,
    ATK2,
    SKILL;

    @Override
    public int valueSize() {
        return values().length;
    }
}
