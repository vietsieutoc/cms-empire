package com.yuri.empire.models.master;

import com.yuri.empire.models.enumz.JobType;
import com.yuri.empire.models.enumz.RarityType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MHero extends MasterModel {
    public String avatarPath;
    public String modelName;
    public String name;
    public String description;
    public JobType jobType;
    public RarityType rarity;
    public String classId;
    public boolean isMinion;
    public float pvpPoint;
    public float pvpAtk;
    public float pvpHp;
    public float pvpSpeed;
    public List<String> skillIds;
}
