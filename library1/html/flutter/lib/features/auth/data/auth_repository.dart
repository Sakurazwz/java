import '../../../core/storage/token_storage.dart';
import '../../../core/utils/jwt_utils.dart';
import '../domain/user.dart';
import 'auth_api.dart';

// 认证仓库，处理登录/注册/登出业务逻辑
class AuthRepository {
  final AuthApi _authApi = AuthApi();

  // 用户登录，保存 Token 并返回用户信息
  Future<User> login(String username, String password) async {
    final data = await _authApi.login(username, password);
    final token = data['token'] as String;

    // 保存 Token
    await TokenStorage.saveToken(token);

    // 解析用户信息
    final userId = JwtUtils.getUserId(token) ?? 0;
    final name = JwtUtils.getUsername(token) ?? username;
    final role = JwtUtils.getRole(token) ?? 'USER';

    // 保存角色
    await TokenStorage.saveRole(role);

    return User(id: userId, name: name, role: role);
  }

  // 用户注册
  Future<void> register(String name, String password) async {
    await _authApi.register(name, password);
  }

  // 用户登出，清除本地 Token
  Future<void> logout() async {
    await TokenStorage.deleteToken();
  }

  // 获取当前登录用户信息
  User? getCurrentUser() {
    final token = TokenStorage.getToken();
    if (token == null) return null;

    final userId = JwtUtils.getUserId(token) ?? 0;
    final name = JwtUtils.getUsername(token) ?? '';
    final role = JwtUtils.getRole(token) ?? 'USER';

    return User(id: userId, name: name, role: role);
  }
}
