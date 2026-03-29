//import com.example.demo.common.Result;
//import io.swagger.v3.oas.annotations.parameters.RequestBody;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/auth")
//public class AuthController {
//
//    @PostMapping("/login")
//    public Result<Map<String, String>> login(@RequestBody LoginRequest request) {
//        // 1. 验证用户名密码
//        // 2. 生成 JWT Token
//        String token = generateToken(request.getUsername());
//
//        Map<String, String> data = new HashMap<>();
//        data.put("token", token);
//        data.put("type", "Bearer");
//
//        return Result.success(data);
//    }
//
//    private String generateToken(String username) {
//        // 实际应使用 JWT 库（如 io.jsonwebtoken:jjwt）
//        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
//                "eyJ1c2VybmFtZSI6Ii" + username + "In0." +
//                "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
//    }
//}
