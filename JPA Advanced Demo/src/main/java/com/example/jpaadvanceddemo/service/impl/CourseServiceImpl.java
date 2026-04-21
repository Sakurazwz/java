package com.example.jpaadvanceddemo.service.impl;

import com.example.jpaadvanceddemo.dto.CourseWithStudentsDTO;
import com.example.jpaadvanceddemo.entity.Course;
import com.example.jpaadvanceddemo.repository.CourseRepository;
import com.example.jpaadvanceddemo.service.CourseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
    }

    @Override
    @Transactional
    public CourseWithStudentsDTO getCourseByIdWithStudents(Long id) {
        Course course = courseRepository.findByIdWithStudents(id)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
        return convertToDTO(course);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    @Transactional
    public List<CourseWithStudentsDTO> getAllCoursesWithStudents() {
        List<Course> courses = courseRepository.findAllWithStudents();
        return courses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Course updateCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    private CourseWithStudentsDTO convertToDTO(Course course) {
        return new CourseWithStudentsDTO(
                course.getId(),
                course.getCourseName(),
                course.getCredit(),
                course.getStudents()
        );
    }
}
