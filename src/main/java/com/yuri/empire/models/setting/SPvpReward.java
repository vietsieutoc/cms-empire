package com.yuri.empire.models.setting;

import com.yuri.empire.models.enumz.PVPMode;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SPvpReward extends SettingModel {
    public PVPMode mode;
    public List<String> rewardBoxIds;
}
