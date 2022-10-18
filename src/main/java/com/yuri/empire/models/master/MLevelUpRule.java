package com.yuri.empire.models.master;

import com.yuri.empire.models.BaseModel;
import org.springframework.stereotype.Component;

@Component
public class MLevelUpRule extends MasterModel {
    public float start;
    public float end;
    public float rate;
    public float atkSpeed;
    public float speed;
    public float critRate;
    public float critDamage;
    public float evasion;
}
