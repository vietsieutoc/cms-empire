package com.yuri.empire.models.master;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MRewardBox extends MasterModel {
    public List<String> rewardIds;
    public List<Float> rates;

}
