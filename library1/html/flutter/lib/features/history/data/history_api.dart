import '../../../core/network/api_service.dart';

// 历史 API
class HistoryApi {
  final ApiService _apiService = ApiService();

  // 获取所有历史记录（支持筛选）
  Future<List<dynamic>> getAllHistory({
    int? userId,
    String? startDate,
    String? endDate,
  }) async {
    final response = await _apiService.getAllHistory(
      userId: userId,
      startDate: startDate,
      endDate: endDate,
    );
    return response.data;
  }

  // 根据用户 ID 获取历史记录
  Future<List<dynamic>> getHistoryByUserId(int userId) async {
    final response = await _apiService.getHistoryByUserId(userId);
    return response.data;
  }
}
