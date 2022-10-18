package com.yuri.empire.models.setting;

import com.yuri.empire.models.enumz.JobType;
import com.yuri.empire.models.enumz.RarityType;
import org.springframework.stereotype.Component;

@Component
public class SHeroPool extends SettingModel {
    public JobType jobType;
    public RarityType rarityType;
    public String classId;
    public String poolId;
    public boolean isActive;
    public boolean isMinion;
}
