package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.common.ResultCode;
import com.example.demo.dto.UserQueryDTO;
import com.example.demo.dto.UserRegisterDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.BusinessException;
import com.example.demo.validation.ValidationGroup;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    /**
     * 查询单个用户
     */
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
     * 无数据返回
     */
    @GetMapping("/empty")
    public Result<String> empty() {
        return Result.success();
    }

    /**
     * 模拟失败
     */
    @GetMapping("/fail")
    public Result<String> fail() {
        return Result.error(ResultCode.USER_NOT_FOUND);
    }

    /**
     * 自定义错误消息
     */
    @GetMapping("/custom-error")
    public Result<String> customError() {
        return Result.error("操作失败，请稍后重试");
    }

    /**
     * 测试业务异常
     */
    @GetMapping("/business-exception")
    public Result<String> businessException() {
        throw new BusinessException(ResultCode.USER_NOT_FOUND);
    }

    /**
     * 测试自定义业务异常
     */
    @GetMapping("/business-exception-custom")
    public Result<String> businessExceptionCustom() {
        throw new BusinessException("用户名或密码错误");
    }

    /**
     * 测试算术异常
     */
    @GetMapping("/arithmetic-exception")
    public Result<String> arithmeticException() {
        int i = 1 / 0;
        return Result.success();
    }

    /**
     * 测试空指针异常
     */
    @GetMapping("/null-exception")
    public Result<String> nullException() {
        String str = null;
        str.length();
        return Result.success();
    }

    /**
     * 测试非法参数异常
     */
    @GetMapping("/illegal-argument")
    public Result<String> illegalArgument() {
        throw new IllegalArgumentException("用户名不能为空");
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody UserRegisterDTO dto) {
        // 校验两次密码是否一致
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            return Result.error("两次密码不一致");
        }

        // TODO: 检查用户名是否已存在
        // TODO: 检查邮箱是否已注册
        // TODO: 密码加密
        // TODO: 保存用户

        log.info("用户注册: {}", dto.getUsername());

        return Result.success("注册成功");
    }

    /**
     * Query 参数校验
     * 注意：需要在类级别添加 @Validated
     */
    @GetMapping("/query")
    public Result<String> query(
            @NotBlank(message = "用户名不能为空")
            @Size(min = 3, max = 20, message = "用户名长度必须在3-20之间")
            String username) {

        return Result.success("查询用户: " + username);
    }


    /**
     * ModelAttribute 校验（表单提交或 URL 参数）
     */
    @GetMapping("/search")
    public Result<String> search(@Validated UserQueryDTO dto) {
        return Result.success("搜索用户: " + dto.getUsername());
    }


    /**
     * 新增用户（使用 Create 分组）
     */
    @PostMapping("/create")
    public Result<String> createUser(@Validated(ValidationGroup.Create.class) @RequestBody UserUpdateDTO dto) {
        return Result.success("新增用户成功: " + dto.getUsername());
    }

    /**
     * 更新用户（使用 Update 分组）
     */
    @PostMapping("/update")
    public Result<String> updateUser(@Validated(ValidationGroup.Update.class) @RequestBody UserUpdateDTO dto) {
        return Result.success("更新用户成功: ID=" + dto.getId());
    }

}
