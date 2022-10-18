package com.yuri.empire.models.master;

import com.yuri.empire.models.enumz.BuffStatusType;
import org.springframework.stereotype.Component;

@Component
public class MSkillABuffStatus extends MSkillAnimation {
    public BuffStatusType type;
    public int turn;
    public float value;
    public String path;
}