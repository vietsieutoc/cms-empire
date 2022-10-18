package com.yuri.empire.models.master;

import com.yuri.empire.models.enumz.MoveToTargetType;
import org.springframework.stereotype.Component;

@Component
public class MSkillAMoveBack extends MSkillAnimation{
    public MoveToTargetType type;
    public float duration;
}
