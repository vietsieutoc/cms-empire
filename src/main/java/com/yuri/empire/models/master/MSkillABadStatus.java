package com.yuri.empire.models.master;

import com.yuri.empire.models.enumz.BadStatusType;
import org.springframework.stereotype.Component;

@Component
public class MSkillABadStatus extends MSkillAnimation {
    public BadStatusType type;
    public int turn;
    public float value;
    public String path;
}
