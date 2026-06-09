import '../domain/borrow_history.dart';
import 'history_api.dart';

// 历史仓库
class HistoryRepository {
  final HistoryApi _historyApi = HistoryApi();

  // 获取所有历史记录
  Future<List<BorrowHistory>> getAllHistory({
    int? userId,
    String? startDate,
    String? endDate,
  }) async {
    final data = await _historyApi.getAllHistory(
      userId: userId,
      startDate: startDate,
      endDate: endDate,
    );
    return data.map((json) => BorrowHistory.fromJson(json)).toList();
  }

  // 根据用户 ID 获取历史记录
  Future<List<BorrowHistory>> getHistoryByUserId(int userId) async {
    final data = await _historyApi.getHistoryByUserId(userId);
    return data.map((json) => BorrowHistory.fromJson(json)).toList();
  }
}
