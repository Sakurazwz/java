import 'package:dio/dio.dart';
import '../storage/token_storage.dart';
import '../../config/api_config.dart';

// Dio HTTP 客户端（单例模式）
class DioClient {
  static DioClient? _instance;
  late Dio _dio;

  DioClient._() {
    _dio = Dio(
      BaseOptions(
        baseUrl: ApiConfig.baseUrl,
        connectTimeout: const Duration(seconds: 10),
        receiveTimeout: const Duration(seconds: 10),
        contentType: 'application/json',
      ),
    );

    // 添加请求拦截器
    _dio.interceptors.add(
      InterceptorsWrapper(
        onRequest: (options, handler) {
          // 自动注入 Token
          final token = TokenStorage.getToken();
          if (token != null) {
            options.headers['Authorization'] = 'Bearer $token';
          }
          handler.next(options);
        },
        onResponse: (response, handler) {
          handler.next(response);
        },
        onError: (error, handler) {
          // 处理 401/403 错误
          if (error.response?.statusCode == 401 ||
              error.response?.statusCode == 403) {
            TokenStorage.deleteToken();
            // 跳转到登录页（需要在 UI 层处理）
          }
          handler.next(error);
        },
      ),
    );
  }

  // 获取单例
  static DioClient get instance {
    _instance ??= DioClient._();
    return _instance!;
  }

  // 获取 Dio 实例
  Dio get dio => _dio;

  // 更新 baseUrl
  void updateBaseUrl(String url) {
    _dio.options.baseUrl = url;
  }
}