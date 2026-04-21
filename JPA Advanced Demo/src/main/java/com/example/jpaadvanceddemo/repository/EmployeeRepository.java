package com.example.jpaadvanceddemo.repository;

import com.example.jpaadvanceddemo.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // 查询员工及其部门（使用 JOIN FETCH 避免N+1问题）
    @Query("SELECT DISTINCT e FROM Employee e LEFT JOIN FETCH e.department WHERE e.id = :id")
    Optional<Employee> findByIdWithDepartment(@Param("id") Long id);

    // 查询所有员工及其部门
    @Query("SELECT DISTINCT e FROM Employee e LEFT JOIN FETCH e.department")
    List<Employee> findAllWithDepartment();
}
