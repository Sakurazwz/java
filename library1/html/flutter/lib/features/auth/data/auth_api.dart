import '../../../core/network/api_service.dart';

// 认证 API 服务
class AuthApi {
  final ApiService _apiService = ApiService();

  // 用户登录
  Future<Map<String, dynamic>> login(String username, String password) async {
    final response = await _apiService.login(username, password);
    return response.data;
  }

  // 用户注册
  Future<Map<String, dynamic>> register(String name, String password) async {
    final response = await _apiService.register(name, password);
    return response.data;
  }
}
