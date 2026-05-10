package com.example.redisdemo.controller;

import com.example.redisdemo.entity.User;
import com.example.redisdemo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户控制器
 * 演示 Redis 缓存功能
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "演示 Redis 缓存功能")
public class UserController {

    private final UserService userService;

    /**
     * 创建用户
     */
    @PostMapping
    @Operation(summary = "创建用户")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    /**
     * 查询用户（演示缓存）
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询用户（演示缓存）")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    /**
     * 更新用户（演示缓存更新）
     */
    @PutMapping
    @Operation(summary = "更新用户")
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    /**
     * 删除用户（演示缓存删除）
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    /**
     * 获取所有用户
     */
    @GetMapping
    @Operation(summary = "获取所有用户")
    public Map<Long, User> getAllUsers() {
        return userService.getAllUsers();
    }
}
