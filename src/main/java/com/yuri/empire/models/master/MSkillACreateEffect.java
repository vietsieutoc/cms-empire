package com.yuri.empire.models.master;

import com.yuri.empire.models.enumz.CreateEffectType;
import org.springframework.stereotype.Component;

@Component
public class MSkillACreateEffect extends MSkillAnimation {
    public CreateEffectType type;
    public String path;
    public float duration;
}
