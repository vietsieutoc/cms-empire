package com.yuri.empire.models.master;

import com.yuri.empire.models.BaseModel;
import com.yuri.empire.models.enumz.AchievementType;
import com.yuri.empire.models.enumz.RarityType;
import com.yuri.empire.models.enumz.TraderType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MAchievement extends MasterModel {
    public AchievementType type;
    public String groupName;
    public int value;
    public String regionId;
    public RarityType rarity;
    public TraderType traderType;
    public String name;
    public String description;
    public List<String> rewardIds; // list item
}
