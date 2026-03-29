package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.UserRegisterDTO;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "用户管理", description = "用户的增删改查接口")
public class UserController {

    private final UserService userService;

    /**
     * 查询单个用户
     */

    @GetMapping("/get")
    @Operation(summary = "查询单个用户", description = "根据用户ID查询用户信息")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "查询成功",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "请求参数错误",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            name = "参数错误示例",
                            value = "{\"code\":400,\"message\":\"请求参数错误\",\"data\":null}"
                    ))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "服务器内部错误",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            name = "服务器错误示例",
                            value = "{\"code\":500,\"message\":\"服务器内部错误\",\"data\":null}"
                    ))
            )
    })
    public Result<User> getUser(
            @Parameter(name = "id", description = "用户ID", in = ParameterIn.QUERY, required = true, example = "1")
            @RequestParam Long id) {
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(
                    responseCode = "500",
                    description = "服务器内部错误",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            name = "服务器错误示例",
                            value = "{\"code\":500,\"message\":\"服务器内部错误\",\"data\":null}"
                    ))
            )
    })
    public Result<List<User>> getUserList() {
        List<User> users = userService.list();
        return Result.success(users);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "用户注册信息",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserRegisterDTO.class)
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "注册成功"),
            @ApiResponse(
                    responseCode = "400",
                    description = "参数校验失败",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            name = "参数校验失败示例",
                            value = "{\"code\":400,\"message\":\"用户名不能为空; 邮箱格式不正确\",\"data\":null}"
                    ))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "服务器错误",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            name = "服务器错误示例",
                            value = "{\"code\":500,\"message\":\"服务器内部错误\",\"data\":null}"
                    ))
            )
    })
    public Result<String> register(
            @Parameter(description = "用户注册信息", required = true)
            @Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        userService.register(userRegisterDTO);
        return Result.success("注册成功");
    }


}
