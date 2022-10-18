package com.yuri.empire.models.master;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MScenario extends MasterModel {
    public String bgID;
    public List<MScenarioContent> scenarioContents;// list item
}

