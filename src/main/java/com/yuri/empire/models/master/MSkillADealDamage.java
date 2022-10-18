package com.yuri.empire.models.master;

import com.yuri.empire.models.enumz.DealDamageType;
import org.springframework.stereotype.Component;

@Component
public class MSkillADealDamage extends MSkillAnimation {
    public DealDamageType type;
    public float value;
    public String hitEffectPath;
}