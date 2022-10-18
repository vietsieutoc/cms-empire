package com.yuri.empire.models.master;

import com.yuri.empire.models.enumz.FromType;
import org.springframework.stereotype.Component;

@Component
public class MClass extends MasterModel {
    public String name;
    public FromType fromType;
    public float hp;
    public float atk;
    public float def;
    public float atkSpeed;
    public float speed;
    public float critRate;
    public float critDamage;
}
