package com.example.jpaadvanceddemo.controller;

import com.example.jpaadvanceddemo.dto.UserCreateRequestDTO;
import com.example.jpaadvanceddemo.dto.UserWithProfileDTO;
import com.example.jpaadvanceddemo.entity.User;
import com.example.jpaadvanceddemo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@Tag(name = "用户管理", description = "用户CRUD操作")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

//    @PostMapping
//    @Operation(summary = "创建用户")
//    public User createUser(@RequestBody User user) {
//        return userService.createUser(user);
//    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询用户")
    public User getUserById(
            @Parameter(description = "用户ID") @PathVariable Long id
    ) {
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/with-profile")
    @Operation(summary = "查询用户及其详情")
    public User getUserByIdWithProfile(
            @Parameter(description = "用户ID") @PathVariable Long id
    ) {
        return userService.getUserByIdWithProfile(id);
    }

    @GetMapping
    @Operation(summary = "查询所有用户")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping
    @Operation(summary = "更新用户")
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public void deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long id
    ) {
        userService.deleteUser(id);
    }

    @PostMapping
    @Operation(summary = "创建用户")
    public User createUser(@RequestBody UserCreateRequestDTO dto) {
        return userService.createUserWithDTO(dto);
    }

    @GetMapping("/{id}/with-profile-dto")
    @Operation(summary = "查询用户及其详情（返回DTO）")
    public UserWithProfileDTO getUserByIdWithProfileDTO(
            @Parameter(description = "用户ID") @PathVariable Long id
    ) {
        return userService.getUserByIdWithProfileDTO(id);
    }
}
