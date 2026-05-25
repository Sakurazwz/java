package com.gcc.library1.auth;

import com.gcc.library1.model.User;
import com.gcc.library1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // 使用 BCrypt 验证用户凭据
            User user = userService.authenticate(request.username(), request.password());
            // 生成 JWT 令牌（包含用户名、用户ID和角色）
            String token = jwtService.generateToken(user.getName(), user.getId(), user.getRole());
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("用户名或密码错误");
        }
    }

    public record LoginRequest(String username, String password) {
    }

    public record JwtResponse(String token) {
    }
}
