package com.yuri.empire.models.enumz;

public enum SkillAnimationType implements BaseEnum{
    CHANGE_ANIMATION,
    CHANGE_SKELETON,
    MOVE_TO_TARGET,
    MOVE_BACK,
    DELAY,
    CREATE_EFFECT,
    PLAY_SOUND,
    DEAL_DAMAGE,
    SPAWN_BULLET,
    SPAWN_MINION,
    BUFF_STATUS,
    BAD_STATUS;
    @Override
    public int valueSize() {
        return values().length;
    }
}
