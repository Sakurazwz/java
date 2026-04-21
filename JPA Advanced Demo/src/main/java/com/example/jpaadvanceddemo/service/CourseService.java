package com.example.jpaadvanceddemo.service;

import com.example.jpaadvanceddemo.entity.Course;

import java.util.List;

public interface CourseService {

    Course createCourse(Course course);
    Course getCourseById(Long id);
    Course getCourseByIdWithStudents(Long id);
    List<Course> getAllCourses();
    List<Course> getAllCoursesWithStudents();
    Course updateCourse(Course course);
    void deleteCourse(Long id);
}
