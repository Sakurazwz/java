package com.example.jpaadvanceddemo.service.impl;

import com.example.jpaadvanceddemo.dto.StudentWithCoursesDTO;
import com.example.jpaadvanceddemo.entity.Course;
import com.example.jpaadvanceddemo.entity.Student;
import com.example.jpaadvanceddemo.repository.CourseRepository;
import com.example.jpaadvanceddemo.repository.StudentRepository;
import com.example.jpaadvanceddemo.service.StudentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    @Transactional
    public StudentWithCoursesDTO getStudentByIdWithCourses(Long id) {
        Student student = studentRepository.findByIdWithCourses(id)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
        return convertToDTO(student);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    @Transactional
    public List<StudentWithCoursesDTO> getAllStudentsWithCourses() {
        List<Student> students = studentRepository.findAllWithCourses();
        return students.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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

    private StudentWithCoursesDTO convertToDTO(Student student) {
        return new StudentWithCoursesDTO(
                student.getId(),
                student.getStudentName(),
                student.getStudentNo(),
                student.getCourses()
        );
    }
}
