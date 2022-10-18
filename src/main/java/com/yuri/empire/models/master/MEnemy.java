package com.yuri.empire.models.master;

import com.yuri.empire.models.BaseModel;
import org.springframework.stereotype.Component;

@Component
public class MEnemy extends MasterModel {
    public String heroId;
    public int level;
    public int slot;
}
