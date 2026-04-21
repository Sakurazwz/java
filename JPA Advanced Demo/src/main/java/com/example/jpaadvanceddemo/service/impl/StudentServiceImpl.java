package com.example.jpaadvanceddemo.service.impl;

import com.example.jpaadvanceddemo.entity.Course;
import com.example.jpaadvanceddemo.entity.Student;
import com.example.jpaadvanceddemo.repository.CourseRepository;
import com.example.jpaadvanceddemo.repository.StudentRepository;
import com.example.jpaadvanceddemo.service.StudentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
    }

    @Override
    public Student getStudentByIdWithCourses(Long id) {
        return studentRepository.findByIdWithCourses(id)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public List<Student> getAllStudentsWithCourses() {
        return studentRepository.findAllWithCourses();
    }

    @Override
    @Transactional
    public Student addCourseToStudent(Long studentId, Long courseId) {
        Student student = getStudentById(studentId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("课程不存在"));

        student.getCourses().add(course);
        course.getStudents().add(student);

        return studentRepository.save(student);
    }

    @Override
    @Transactional
    public Student updateStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}
