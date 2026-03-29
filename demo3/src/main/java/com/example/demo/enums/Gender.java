package com.example.demo.enums;

/**
 * 性别枚举
 */
public enum Gender {
    MALE("男", 1),

    FEMALE("女", 0),

    OTHER("其他", 2);

    private final String description;
    private final Integer code;

    Gender(String description, Integer code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public Integer getCode() {
        return code;
    }
}
