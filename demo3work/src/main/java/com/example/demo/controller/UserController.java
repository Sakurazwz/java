package com.example.demo.controller;

import com.example.demo.annotation.Log;
import com.example.demo.common.Result;
import com.example.demo.common.ResultCode;
import com.example.demo.dto.UserRegisterDTO;
import com.example.demo.entity.User;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:5173")
@Validated
public class UserController {

    /**
     * 查询单个用户
     */
    @Log(value = "查询单个用户", module = "用户管理")
    @GetMapping("/get")
    public Result<User> getUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("张三");
        user.setEmail("zhangsan@example.com");
        user.setAge(20);
        return Result.success(user);
    }

    /**
     * 查询用户列表
     */
    @Log(value = "查询用户列表", module = "用户管理")
    @GetMapping("/list")
    public Result<List<User>> getUserList() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("张三");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("李四");

        List<User> users = Arrays.asList(user1, user2);
        return Result.success(users);
    }

    /**
     * 用户注册
     */
    @Log(value = "用户注册", module = "用户管理")
    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody UserRegisterDTO dto) {
        // 校验两次密码是否一致
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            return Result.error("两次密码不一致");
        }

        log.info("用户注册: {}", dto.getUsername());

        return Result.success("注册成功");
    }

    /**
     * 测试异常
     */
    @Log(value = "测试异常", module = "测试")
    @GetMapping("/exception")
    public Result<String> exception() {
        int i = 1 / 0;
        return Result.success();
    }
}
