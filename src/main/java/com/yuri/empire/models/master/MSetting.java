package com.yuri.empire.models.master;

import com.yuri.empire.models.BaseModel;
import com.yuri.empire.models.enumz.RarityType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MSetting extends MasterModel {
    public int maxLevel;
    public List<String> levelUpRules;
    public List<RarityType> baseStats;
}
