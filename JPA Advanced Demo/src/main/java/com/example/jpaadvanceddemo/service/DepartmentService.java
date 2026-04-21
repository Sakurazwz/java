package com.example.jpaadvanceddemo.service;

import com.example.jpaadvanceddemo.dto.DepartmentWithEmployeesDTO;
import com.example.jpaadvanceddemo.entity.Department;

import java.util.List;

/**
 * 部门 Service 接口
 */
public interface DepartmentService {

    /**
     * 创建部门（包含员工）
     */
    Department createDepartment(Department department);

    /**
     * 根据ID查询部门
     */
    Department getDepartmentById(Long id);

    /**
     * 根据ID查询部门及其员工
     */
    Department getDepartmentByIdWithEmployees(Long id);

    /**
     * 查询所有部门
     */
    List<Department> getAllDepartments();

    /**
     * 更新部门
     */
    Department updateDepartment(Department department);

    /**
     * 删除部门
     */
    void deleteDepartment(Long id);
    /**
     * 根据ID查询部门及其员工（返回 DTO）
     */
    DepartmentWithEmployeesDTO getDepartmentByIdWithEmployeesDTO(Long id);
}
