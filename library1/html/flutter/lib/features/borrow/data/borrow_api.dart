import '../../../core/network/api_service.dart';

// 借阅 API 接口封装
class BorrowApi {
  final ApiService _apiService = ApiService();

  // 获取所有借阅记录
  Future<List<dynamic>> getAllBorrows({int? userId}) async {
    final response = await _apiService.getAllBorrows(userId: userId);
    return response.data;
  }

  // 借阅图书
  Future<void> borrowBook(int bookId, int userId) async {
    await _apiService.borrowBook(bookId, userId);
  }

  // 归还图书
  Future<void> returnBook(int bookId, int userId) async {
    await _apiService.returnBook(bookId, userId);
  }

  // 续借图书
  Future<void> renewBook(int bookId, int userId) async {
    await _apiService.renewBook(bookId, userId);
  }

  // 获取用户借阅记录
  Future<List<dynamic>> getUserBorrows(int userId) async {
    final response = await _apiService.getUserBorrows(userId);
    return response.data;
  }
}
