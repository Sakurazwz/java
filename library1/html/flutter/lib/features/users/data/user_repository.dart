import '../../auth/domain/user.dart';
import 'user_api.dart';

// 用户管理仓库，封装用户数据操作
class UserRepository {
  final UserApi _userApi = UserApi();

  // 获取所有用户列表，支持按名称筛选
  Future<List<User>> getAllUsers({String? name}) async {
    final data = await _userApi.getAllUsers(name: name);
    return data.map((json) => User(
      id: json['id'] ?? 0,
      name: json['name'] ?? '',
      role: json['role'] ?? 'USER',
    )).toList();
  }

  // 删除指定用户
  Future<void> deleteUser(int id) async {
    await _userApi.deleteUser(id);
  }

  // 更新用户角色
  Future<void> updateRole(int id, String role) async {
    await _userApi.updateRole(id, role);
  }
}
