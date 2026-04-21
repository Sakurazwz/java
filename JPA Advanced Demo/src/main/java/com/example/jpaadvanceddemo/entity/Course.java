package com.example.jpaadvanceddemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

/**
 * 课程实体
 */
@Data
@Entity
@Table(name = "course")
@EqualsAndHashCode(of = "id")  // 只使用 id 字段生成 hashCode 和 equals
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "课程名称不能为空")
    @Column(nullable = false, length = 64)
    private String courseName;

    private Integer credit;

    // 多对多关联：学生列表
    // 使用 @JsonIgnore 避免JSON序列化无限递归
    // 1. 当返回实体时，不会包含 students 字段，避免序列化问题
    // 2. 如果需要返回关联数据，应使用 DTO（Data Transfer Object）
    // 3. 配合 @EqualsAndHashCode(of = "id") 避免集合字段导致 equals/hashCode 问题
    @JsonIgnore
    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
    private Set<Student> students = new HashSet<>();
}
