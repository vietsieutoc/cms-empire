package com.yuri.empire.models.master;

import com.yuri.empire.models.enumz.ItemCurrency;
import com.yuri.empire.models.enumz.ItemType;
import com.yuri.empire.models.enumz.RarityType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MItem extends MasterModel {
    public String name;
    public String modelName;
    public String description;
    public ItemType type;
    public List<Integer> source;
    public String targetId;
    public float price;
    public float value;
    public float effectValue;
    public String icon;
    public RarityType rarity;
    public float sellValue;
    public ItemCurrency sellCurrency;
}
