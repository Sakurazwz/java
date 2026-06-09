import '../../../core/network/api_service.dart';

// 用户管理 API
class UserApi {
  final ApiService _apiService = ApiService();

  // 获取所有用户，支持按名称筛选
  Future<List<dynamic>> getAllUsers({String? name}) async {
    final response = await _apiService.getAllUsers(name: name);
    return response.data;
  }

  // 删除指定用户
  Future<void> deleteUser(int id) async {
    await _apiService.deleteUser(id);
  }

  // 更新用户角色
  Future<Map<String, dynamic>> updateRole(int id, String role) async {
    final response = await _apiService.updateRole(id, role);
    return response.data;
  }
}
