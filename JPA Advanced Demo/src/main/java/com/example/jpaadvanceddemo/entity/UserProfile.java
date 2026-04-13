package com.example.jpaadvanceddemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 用户详情实体
 */
@Data
@Entity
@Table(name = "user_profile")
@EqualsAndHashCode(of = "id")  // 只使用 id 字段生成 hashCode 和 equals
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String realName;
    private String idCard;
    private LocalDate birthday;

    @Min(value = 0, message = "性别值不正确")
    @Max(value = 2, message = "性别值不正确")
    @Column(columnDefinition = "TINYINT DEFAULT 2")
    private Integer gender = 2;  // 0-女，1-男，2-未知

    private String address;
    private String avatar;

    // 一对一关联：用户
    // @JoinColumn 指定外键列名
    // unique = true 确保一对一关系
    @JsonIgnore  // 避免JSON序列化无限递归
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;
}
