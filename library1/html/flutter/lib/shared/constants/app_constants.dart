// 应用常量定义
class AppConstants {
  // 应用信息
  static const String appName = '图书管理系统';
  static const String appVersion = '1.0.0';

  // 存储键名
  static const String tokenKey = 'token';
  static const String roleKey = 'userRole';
  static const String apiUrlKey = 'apiUrl';

  // 默认值
  static const int pageSize = 20;
  static const int borrowDays = 90;

  // 错误消息
  static const String networkError = '网络连接失败，请检查网络设置';
  static const String serverError = '服务器错误，请稍后重试';
  static const String authError = '登录已过期，请重新登录';
  static const String unknownError = '未知错误';
}
