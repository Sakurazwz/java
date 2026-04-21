package com.example.jpaadvanceddemo.controller;

import com.example.jpaadvanceddemo.dto.StudentWithCoursesDTO;
import com.example.jpaadvanceddemo.entity.Student;
import com.example.jpaadvanceddemo.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "学生管理", description = "学生相关接口")
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @Operation(summary = "创建学生")
    public Student createStudent(@Valid @RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询学生")
    public Student getStudentById(
            @Parameter(description = "学生ID") @PathVariable Long id
    ) {
        return studentService.getStudentById(id);
    }

    @GetMapping("/{id}/with-courses")
    @Operation(summary = "查询学生及其课程")
    public StudentWithCoursesDTO getStudentByIdWithCourses(
            @Parameter(description = "学生ID") @PathVariable Long id
    ) {
        return studentService.getStudentByIdWithCourses(id);
    }

    @GetMapping
    @Operation(summary = "查询所有学生")
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/all-with-courses")
    @Operation(summary = "查询所有学生及其课程")
    public List<StudentWithCoursesDTO> getAllStudentsWithCourses() {
        return studentService.getAllStudentsWithCourses();
    }

    @PostMapping("/{studentId}/courses/{courseId}")
    @Operation(summary = "为学生添加课程")
    public Student addCourseToStudent(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @Parameter(description = "课程ID") @PathVariable Long courseId
    ) {
        return studentService.addCourseToStudent(studentId, courseId);
    }

    @PutMapping
    @Operation(summary = "更新学生")
    public Student updateStudent(@RequestBody Student student) {
        return studentService.updateStudent(student);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除学生")
    public void deleteStudent(
            @Parameter(description = "学生ID") @PathVariable Long id
    ) {
        studentService.deleteStudent(id);
    }
}
