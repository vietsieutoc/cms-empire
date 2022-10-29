package com.yuri.empire.models.master;

import org.springframework.stereotype.Component;

@Component
public class MPvpSkill extends MasterModel {
    public String effectGrid;
    public boolean passive;
    public String name;
    public String description;
    public String iconPath;
    public float damage;
    public float extraValue;
    public float effectRound;
    public String effectType;
    public String targetType;
    public int roundLimit;
    public int battleLimit;

}