package com.example.jpaadvanceddemo.controller;

import com.example.jpaadvanceddemo.dto.DepartmentWithEmployeesDTO;
import com.example.jpaadvanceddemo.entity.Department;
import com.example.jpaadvanceddemo.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "部门管理", description = "部门相关接口")
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @Operation(summary = "创建部门")
    public Department createDepartment(@Valid @RequestBody Department department) {
        return departmentService.createDepartment(department);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询部门")
    public Department getDepartmentById(
            @Parameter(description = "部门ID") @PathVariable Long id
    ) {
        return departmentService.getDepartmentById(id);
    }

    @GetMapping("/{id}/with-employees")
    @Operation(summary = "查询部门及其员工")
    public Department getDepartmentByIdWithEmployees(
            @Parameter(description = "部门ID") @PathVariable Long id
    ) {
        return departmentService.getDepartmentByIdWithEmployees(id);
    }

    @GetMapping
    @Operation(summary = "查询所有部门")
    public List<Department> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    @PutMapping
    @Operation(summary = "更新部门")
    public Department updateDepartment(@RequestBody Department department) {
        return departmentService.updateDepartment(department);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除部门")
    public void deleteDepartment(
            @Parameter(description = "部门ID") @PathVariable Long id
    ) {
        departmentService.deleteDepartment(id);
    }

    @GetMapping("/{id}/with-employees-dto")
    @Operation(summary = "查询部门及其员工（返回DTO）")
    public DepartmentWithEmployeesDTO getDepartmentByIdWithEmployeesDTO(
            @Parameter(description = "部门ID") @PathVariable Long id
    ) {
        return departmentService.getDepartmentByIdWithEmployeesDTO(id);
    }
}
