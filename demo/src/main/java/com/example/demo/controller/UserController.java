package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.common.ResultCode;
import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.UserQueryDTO;
import com.example.demo.dto.UserRegisterDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.BusinessException;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 用户控制器
 */
@Slf4j
@Validated
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
     * 无数据返回
     */
    @GetMapping("/empty")
    @Operation(summary = "无数据返回", description = "返回成功但没有数据")
    public Result<String> empty() {
        return Result.success();
    }

    /**
     * 模拟失败
     */
    @GetMapping("/fail")
    @Operation(summary = "模拟失败", description = "返回用户不存在错误")
    public Result<String> fail() {
        return Result.error(ResultCode.USER_NOT_FOUND);
    }

    /**
     * 自定义错误消息
     */
    @GetMapping("/custom-error")
    @Operation(summary = "自定义错误消息", description = "返回自定义错误提示")
    public Result<String> customError() {
        return Result.error("操作失败，请稍后重试");
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
        // 校验两次密码是否一致
        if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())) {
            return Result.error("两次密码不一致");
        }
        userService.register(userRegisterDTO);
        log.info("用户注册: {}", userRegisterDTO.getUsername());
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
        return Result.success("新增用户成功: " + dto.getUsername());
    }

    /**
     * 更新用户（使用 Update 分组校验）
     */
    @PostMapping("/update")
    @Operation(summary = "更新用户", description = "使用Update分组校验：用户ID必填")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "参数校验失败")
    })
    public Result<String> updateUser(@Validated(ValidationGroup.Update.class) @RequestBody UserUpdateDTO dto) {
        userService.update(dto);
        log.info("更新用户ID: {}", dto.getId());
        return Result.success("更新用户成功: ID=" + dto.getId());
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

    /**
     * 测试业务异常
     */
    @GetMapping("/business-exception")
    @Operation(summary = "测试业务异常", description = "抛出 BusinessException(USER_NOT_FOUND)")
    public Result<String> businessException() {
        throw new BusinessException(ResultCode.USER_NOT_FOUND);
    }

    /**
     * 测试自定义业务异常
     */
    @GetMapping("/business-exception-custom")
    @Operation(summary = "测试自定义业务异常", description = "抛出自定义消息的 BusinessException")
    public Result<String> businessExceptionCustom() {
        throw new BusinessException("用户名或密码错误");
    }

    /**
     * 测试算术异常
     */
    @GetMapping("/arithmetic-exception")
    @Operation(summary = "测试算术异常", description = "触发除零 ArithmeticException")
    public Result<String> arithmeticException() {
        // 故意触发 ArithmeticException 以演示全局异常处理
        @SuppressWarnings("unused")
        int i = 1 / 0;
        return Result.success();
    }

    /**
     * 测试空指针异常
     */
    @GetMapping("/null-exception")
    @Operation(summary = "测试空指针异常", description = "触发 NullPointerException")
    public Result<String> nullException() {
        // 故意触发 NullPointerException 以演示全局异常处理
        String str = null;
        str.length();
        return Result.success();
    }

    /**
     * 测试非法参数异常
     */
    @GetMapping("/illegal-argument")
    @Operation(summary = "测试非法参数异常", description = "抛出 IllegalArgumentException")
    public Result<String> illegalArgument() {
        throw new IllegalArgumentException("用户名不能为空");
    }

    /**
     * Query 参数校验
     * 注意：需要在类级别添加 @Validated
     */
    @GetMapping("/query")
    @Operation(summary = "RequestParam参数校验", description = "校验 username 查询参数")
    public Result<String> query(
            @NotBlank(message = "用户名不能为空")
            @Size(min = 3, max = 20, message = "用户名长度必须在3-20之间")
            @RequestParam String username) {
        return Result.success("查询用户: " + username);
    }

    /**
     * ModelAttribute 校验（表单提交或 URL 参数）
     */
    @GetMapping("/search")
    @Operation(summary = "ModelAttribute参数校验", description = "校验 UserQueryDTO 查询参数")
    public Result<String> search(@Validated UserQueryDTO dto) {
        return Result.success("搜索用户: " + dto.getUsername());
    }

    /**
     * 测试枚举校验
     */
    @PostMapping("/test-enum")
    @Operation(summary = "测试枚举校验", description = "校验 gender 字段是否为有效的 Gender 枚举值")
    public Result<String> testEnum(@Valid @RequestBody UserRegisterDTO dto) {
        log.info("性别: {}", dto.getGender());
        return Result.success("测试成功");
    }
}
