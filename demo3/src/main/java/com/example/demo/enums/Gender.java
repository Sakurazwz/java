package com.example.demo.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 性别枚举
 */
@Schema(description = "性别枚举")
public enum Gender {
    @Schema(description = "男")
    MALE("男", 1),

    @Schema(description = "女")
    FEMALE("女", 0),

    @Schema(description = "其他")
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