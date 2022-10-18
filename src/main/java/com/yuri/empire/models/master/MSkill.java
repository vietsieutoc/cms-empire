package com.yuri.empire.models.master;

import com.yuri.empire.models.enumz.SkillType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MSkill extends MasterModel {
    public int stat;
    public int targetType;
    public int targetCount;
    public List<MSkillAnimation> animations; // refer to
    public SkillType skillType;
}
