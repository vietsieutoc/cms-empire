package com.yuri.empire.models.master;

import com.yuri.empire.models.enumz.SpawnMinionType;
import org.springframework.stereotype.Component;

@Component
public class MSkillASpawnMinion extends MSkillAnimation {
    public SpawnMinionType type;
    public int numberMinion;
    public String minionID;
    public float value;
}
