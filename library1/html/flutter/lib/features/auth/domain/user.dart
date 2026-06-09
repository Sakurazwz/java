// 用户实体
class User {
  final int id;
  final String name;
  final String role;

  const User({
    required this.id,
    required this.name,
    required this.role,
  });

  // 判断是否为管理员
  bool get isAdmin => role == 'ADMIN' || role == 'ROLE_ADMIN';
}
