package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.annotation.Log;
import com.example.demo.dto.UserRegisterDTO;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 查询单个用户
     */

    @GetMapping("/get")
    @Log("查询单个用户")
    public Result<User> getUser(@RequestParam Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    /**
     * 查询用户列表
     */
    @GetMapping("/list")
    @Log("查询用户列表")
    public Result<List<User>> getUserList() {
        List<User> users = userService.list();
        return Result.success(users);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Log("用户注册")
    public Result<String> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        userService.register(userRegisterDTO);
        return Result.success("注册成功");
    }


}
