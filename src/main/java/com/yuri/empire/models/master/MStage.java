package com.yuri.empire.models.master;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MStage extends MasterModel {
    public float x;
    public float y;
    public String scenarioId;
    public boolean isBoss;
    public List<String> waves;
    public List<String> rewardBoxIds;
    public float rewardRate;
    public int exp;
    public int gold;
}
