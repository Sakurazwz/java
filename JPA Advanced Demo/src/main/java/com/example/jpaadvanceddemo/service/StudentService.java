package com.example.jpaadvanceddemo.service;

import com.example.jpaadvanceddemo.entity.Student;

import java.util.List;

public interface StudentService {

    Student createStudent(Student student);
    Student getStudentById(Long id);
    Student getStudentByIdWithCourses(Long id);
    List<Student> getAllStudents();
    List<Student> getAllStudentsWithCourses();
    Student addCourseToStudent(Long studentId, Long courseId);
    Student updateStudent(Student student);
    void deleteStudent(Long id);
}
