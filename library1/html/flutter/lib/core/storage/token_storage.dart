import 'package:shared_preferences/shared_preferences.dart';

// Token 本地存储管理
class TokenStorage {
  static late SharedPreferences _prefs;

  // 初始化
  static Future<void> init() async {
    _prefs = await SharedPreferences.getInstance();
  }

  // 获取 Token
  static String? getToken() {
    return _prefs.getString('token');
  }

  // 保存 Token
  static Future<void> saveToken(String token) async {
    await _prefs.setString('token', token);
  }

  // 删除 Token
  static Future<void> deleteToken() async {
    await _prefs.remove('token');
    await _prefs.remove('userRole');
  }

  // 获取用户角色
  static String? getRole() {
    return _prefs.getString('userRole');
  }

  // 保存用户角色
  static Future<void> saveRole(String role) async {
    await _prefs.setString('userRole', role);
  }

  // 判断是否已登录
  static bool isAuthenticated() {
    return getToken() != null;
  }

  // 判断是否为管理员
  static bool isAdmin() {
    final role = getRole();
    return role == 'ADMIN' || role == 'ROLE_ADMIN';
  }
}