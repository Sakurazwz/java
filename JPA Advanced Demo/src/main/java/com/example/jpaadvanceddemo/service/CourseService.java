package com.example.jpaadvanceddemo.service;

import com.example.jpaadvanceddemo.dto.CourseWithStudentsDTO;
import com.example.jpaadvanceddemo.entity.Course;

import java.util.List;

public interface CourseService {

    Course createCourse(Course course);
    Course getCourseById(Long id);
    CourseWithStudentsDTO getCourseByIdWithStudents(Long id);
    List<Course> getAllCourses();
    List<CourseWithStudentsDTO> getAllCoursesWithStudents();
    Course updateCourse(Course course);
    void deleteCourse(Long id);
}
