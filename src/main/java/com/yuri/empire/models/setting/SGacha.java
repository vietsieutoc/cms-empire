package com.yuri.empire.models.setting;

import com.yuri.empire.models.enumz.GachaType;
import com.yuri.empire.models.enumz.PriceType;
import com.yuri.empire.models.master.MasterModel;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SGacha extends MasterModel {
    public float price;
    public PriceType priceType;
    public GachaType type;
    public int unitAmount;
    public List<String> gift;
    public String poolId;
    public Date startAt;
    public Date endAt;
    public String description;
    public String title;
    public String poolTitle;
}
