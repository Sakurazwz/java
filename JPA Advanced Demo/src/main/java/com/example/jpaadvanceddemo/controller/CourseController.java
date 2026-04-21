package com.example.jpaadvanceddemo.controller;

import com.example.jpaadvanceddemo.dto.CourseWithStudentsDTO;
import com.example.jpaadvanceddemo.entity.Course;
import com.example.jpaadvanceddemo.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "课程管理", description = "课程相关接口")
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @Operation(summary = "创建课程")
    public Course createCourse(@Valid @RequestBody Course course) {
        return courseService.createCourse(course);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询课程")
    public Course getCourseById(
            @Parameter(description = "课程ID") @PathVariable Long id
    ) {
        return courseService.getCourseById(id);
    }

    @GetMapping("/{id}/with-students")
    @Operation(summary = "查询课程及其学生")
    public CourseWithStudentsDTO getCourseByIdWithStudents(
            @Parameter(description = "课程ID") @PathVariable Long id
    ) {
        return courseService.getCourseByIdWithStudents(id);
    }

    @GetMapping
    @Operation(summary = "查询所有课程")
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/all-with-students")
    @Operation(summary = "查询所有课程及其学生")
    public List<CourseWithStudentsDTO> getAllCoursesWithStudents() {
        return courseService.getAllCoursesWithStudents();
    }

    @PutMapping
    @Operation(summary = "更新课程")
    public Course updateCourse(@RequestBody Course course) {
        return courseService.updateCourse(course);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程")
    public void deleteCourse(
            @Parameter(description = "课程ID") @PathVariable Long id
    ) {
        courseService.deleteCourse(id);
    }
}
