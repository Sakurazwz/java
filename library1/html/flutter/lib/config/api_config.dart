// API 地址配置
class ApiConfig {
  // 默认 API 地址
  static const String defaultBaseUrl = 'http://8.163.28.84:1100/api';

  // 当前 API 地址（可通过设置修改）
  static String baseUrl = defaultBaseUrl;

  // 设置 API 地址
  static void setBaseUrl(String url) {
    baseUrl = url;
  }

  // 重置为默认地址
  static void resetBaseUrl() {
    baseUrl = defaultBaseUrl;
  }
}
