package com.example.jpaadvanceddemo.dto;

import com.example.jpaadvanceddemo.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 学生 DTO（包含课程列表）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentWithCoursesDTO {

    private Long id;
    private String studentName;
    private String studentNo;
    private Set<Course> courses;
}
