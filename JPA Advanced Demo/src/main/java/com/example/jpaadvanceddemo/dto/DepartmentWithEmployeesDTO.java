package com.example.jpaadvanceddemo.dto;

import com.example.jpaadvanceddemo.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 部门 DTO（包含员工列表）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentWithEmployeesDTO {

    private Long id;
    private String deptName;
    private String description;
    private List<Employee> employees;
}
