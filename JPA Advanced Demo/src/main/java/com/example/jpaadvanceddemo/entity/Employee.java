package com.example.jpaadvanceddemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 员工实体
 */
@Data
@Entity
@Table(name = "employee")
@EqualsAndHashCode(of = "id")  // 只使用 id 字段生成 hashCode 和 equals
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "员工姓名不能为空")
    @Column(nullable = false, length = 32)
    private String empName;

    private String position;
    private Double salary;

    // 多对一关联：所属部门
    // 使用 @JsonIgnore 避免JSON序列化无限递归
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id", nullable = false)
    private Department department;
}
