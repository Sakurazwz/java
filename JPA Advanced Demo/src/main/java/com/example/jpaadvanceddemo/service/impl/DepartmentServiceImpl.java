package com.example.jpaadvanceddemo.service.impl;

import com.example.jpaadvanceddemo.dto.DepartmentWithEmployeesDTO;
import com.example.jpaadvanceddemo.entity.Department;
import com.example.jpaadvanceddemo.entity.Employee;
import com.example.jpaadvanceddemo.repository.DepartmentRepository;
import com.example.jpaadvanceddemo.service.DepartmentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public Department createDepartment(Department department) {
        // 手动设置双向关联关系
        if (department.getEmployees() != null) {
            for (Employee employee : department.getEmployees()) {
                employee.setDepartment(department);
            }
        }
        return departmentRepository.save(department);
    }

    @Override
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("部门不存在"));
    }

    @Override
    public Department getDepartmentByIdWithEmployees(Long id) {
        return departmentRepository.findByIdWithEmployees(id)
                .orElseThrow(() -> new RuntimeException("部门不存在"));
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    @Transactional
    public Department updateDepartment(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }

    public DepartmentWithEmployeesDTO getDepartmentByIdWithEmployeesDTO(Long id) {
        Department department = departmentRepository.findByIdWithEmployees(id)
                .orElseThrow(() -> new RuntimeException("部门不存在"));

        // 手动构建 DTO
        DepartmentWithEmployeesDTO dto = new DepartmentWithEmployeesDTO();
        dto.setId(department.getId());
        dto.setDeptName(department.getDeptName());
        dto.setDescription(department.getDescription());
        dto.setEmployees(department.getEmployees());

        return dto;
    }
}
