package com.yuri.empire.models.master;

import com.yuri.empire.models.BaseModel;
import com.yuri.empire.models.enumz.RarityType;
import org.springframework.stereotype.Component;

@Component
public class MBaseStat extends MasterModel {
    public RarityType rarity;
    public float rateMin;
    public float rateMax;
}
