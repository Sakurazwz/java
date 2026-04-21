package com.example.jpaadvanceddemo.service.impl;

import com.example.jpaadvanceddemo.entity.Course;
import com.example.jpaadvanceddemo.repository.CourseRepository;
import com.example.jpaadvanceddemo.service.CourseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public Course getCourseByIdWithStudents(Long id) {
        return courseRepository.findByIdWithStudents(id)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public List<Course> getAllCoursesWithStudents() {
        return courseRepository.findAllWithStudents();
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
}
