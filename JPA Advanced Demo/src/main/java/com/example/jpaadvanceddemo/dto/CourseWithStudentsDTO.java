package com.example.jpaadvanceddemo.dto;

import com.example.jpaadvanceddemo.entity.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 课程 DTO（包含学生列表）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseWithStudentsDTO {

    private Long id;
    private String courseName;
    private Integer credit;
    private Set<Student> students;
}
