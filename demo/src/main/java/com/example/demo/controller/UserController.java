package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.UserRegisterDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.validation.ValidationGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
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
@Tag(name = "用户管理", description = "用户的增删改查接口")
public class UserController {

    private final UserService userService;

    /**
     * 查询单个用户
     */
    @GetMapping("/get")
    @Operation(
            summary = "查询单个用户",
            description = "根据用户ID查询用户信息",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "查询成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            )
                    )
            }
    )
    public Result<User> getUser(
            @Parameter(description = "用户ID", required = true) @RequestParam Long id) {
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
    @Operation(summary = "查询用户列表", description = "查询所有用户信息，支持分页")
    @ApiResponse(responseCode = "200", description = "查询成功")
    public Result<List<User>> getUserList() {
        List<User> users = userService.list();
        return Result.success(users);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册（密码使用BCrypt加密）")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserRegisterDTO.class)
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "注册成功"),
            @ApiResponse(responseCode = "400", description = "参数校验失败"),
            @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    public Result<String> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        userService.register(userRegisterDTO);
        return Result.success("注册成功");
    }

    /**
     * 创建用户（使用 Create 分组校验）
     */
    @PostMapping("/create")
    @Operation(summary = "创建用户", description = "使用Create分组校验：用户名和邮箱必填")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "参数校验失败")
    })
    public Result<String> createUser(@Validated(ValidationGroup.Create.class) @RequestBody UserUpdateDTO dto) {
        userService.create(dto);
        log.info("创建用户: {}", dto.getUsername());
        return Result.success("用户创建成功");
    }

    /**
     * 更新用户（使用 Update 分组校验）
     */
    @PutMapping("/update")
    @Operation(summary = "更新用户", description = "使用Update分组校验：用户ID必填")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "参数校验失败")
    })
    public Result<String> updateUser(@Validated(ValidationGroup.Update.class) @RequestBody UserUpdateDTO dto) {
        userService.update(dto);
        log.info("更新用户ID: {}", dto.getId());
        return Result.success("用户更新成功");
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "验证用户名和密码（BCrypt校验）")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginDTO.class)
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "400", description = "用户名或密码错误")
    })
    public Result<String> login(@Valid @RequestBody LoginDTO loginDTO) {
        userService.login(loginDTO);
        log.info("用户登录成功: {}", loginDTO.getUsername());
        return Result.success("登录成功");
    }
}
