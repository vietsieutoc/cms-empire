package com.yuri.empire.models.enumz;

public interface BaseEnum {
    int valueSize();
     default <T> T valueOf(String type, Class<T> clazz) {
        return null;
    }
}
