package com.yuri.empire.models.master;

import com.yuri.empire.models.BaseModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MRegion extends MasterModel {
    public String name;
    public String path;
    public String trailerPath;
    public String story;
    public int index;
    public List<String> stageIds;
}
