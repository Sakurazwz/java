package com.example.jpaadvanceddemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

/**
 * 学生实体
 */
@Data
@Entity
@Table(name = "student")
@EqualsAndHashCode(of = "id")  // 只使用 id 字段生成 hashCode 和 equals
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "学生姓名不能为空")
    @Column(nullable = false, length = 32)
    private String studentName;

    private String studentNo;

    // 多对多关联：课程列表
    // 使用 @JsonIgnore 避免JSON序列化无限递归
    // 1. 当返回实体时，不会包含 courses 字段，避免序列化问题
    // 2. 如果需要返回关联数据，应使用 DTO（Data Transfer Object）
    // 3. 配合 @EqualsAndHashCode(of = "id") 避免集合字段导致 equals/hashCode 问题
    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "student_course",  // 中间表名
            joinColumns = @JoinColumn(name = "student_id"),  // 当前实体在中间表的外键
            inverseJoinColumns = @JoinColumn(name = "course_id")  // 关联实体在中间表的外键
    )
    private Set<Course> courses = new HashSet<>();
}
