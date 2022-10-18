package com.yuri.empire.models.master;

import com.yuri.empire.models.BaseModel;
import org.springframework.stereotype.Component;

@Component
public class MReward extends MasterModel {
    public String itemId;
    public float amount;
}
