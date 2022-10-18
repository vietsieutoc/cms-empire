package com.yuri.empire.models.setting;

import com.yuri.empire.models.enumz.FromType;
import org.springframework.stereotype.Component;

@Component
public class SHeroPoolConfig extends SettingModel {
    public long size;
    public FromType poolType;
    public String extraPoolId;
}
