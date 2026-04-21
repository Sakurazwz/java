package com.example.jpaadvanceddemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门实体
 */
@Data
@Entity
@Table(name = "department")
@EqualsAndHashCode(of = "id")  // 只使用 id 字段生成 hashCode 和 equals
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "部门名称不能为空")
    @Column(nullable = false, length = 64)
    private String deptName;

    private String description;

    // 一对多关联：员工列表
    // mappedBy = "department" 表示关系由 Employee 的 department 属性维护
    // 使用 @JsonIgnore 避免以下问题：
    // 1. JSON序列化无限递归（Department→Employee→Department→Employee...）
    // 2. 懒加载导致的 LazyInitializationException（当 open-in-view=false 时）
    // 3. 如果需要返回关联数据，应使用 DTO（Data Transfer Object）
    @JsonIgnore
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Employee> employees = new ArrayList<>();
}
