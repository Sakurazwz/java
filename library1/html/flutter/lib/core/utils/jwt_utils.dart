import 'dart:convert';

// JWT Token 解析工具
class JwtUtils {
  // 解析 JWT Token
  static Map<String, dynamic>? parseToken(String token) {
    try {
      final parts = token.split('.');
      if (parts.length != 3) return null;

      final payload = parts[1];
      final normalized = base64Url.normalize(payload);
      final decoded = utf8.decode(base64Url.decode(normalized));
      return json.decode(decoded);
    } catch (e) {
      return null;
    }
  }

  // 获取用户 ID
  static int? getUserId(String token) {
    final payload = parseToken(token);
    return payload?['userId'];
  }

  // 获取用户名
  static String? getUsername(String token) {
    final payload = parseToken(token);
    return payload?['sub'];
  }

  // 获取用户角色
  static String? getRole(String token) {
    final payload = parseToken(token);
    return payload?['role'];
  }
}