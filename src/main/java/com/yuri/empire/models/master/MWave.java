
package com.yuri.empire.models.master;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MWave extends MasterModel {
    public List<String> enemies;
    public boolean isBoss;
}
