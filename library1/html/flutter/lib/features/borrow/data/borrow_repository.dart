import '../domain/borrow_record.dart';
import 'borrow_api.dart';

// 借阅数据仓库，负责将 API 数据转换为领域实体
class BorrowRepository {
  final BorrowApi _borrowApi = BorrowApi();

  // 获取所有借阅记录
  Future<List<BorrowRecord>> getAllBorrows({int? userId}) async {
    final data = await _borrowApi.getAllBorrows(userId: userId);
    return data.map((json) => BorrowRecord.fromJson(json)).toList();
  }

  // 借阅图书
  Future<void> borrowBook(int bookId, int userId) async {
    await _borrowApi.borrowBook(bookId, userId);
  }

  // 归还图书
  Future<void> returnBook(int bookId, int userId) async {
    await _borrowApi.returnBook(bookId, userId);
  }

  // 续借图书
  Future<void> renewBook(int bookId, int userId) async {
    await _borrowApi.renewBook(bookId, userId);
  }

  // 获取用户借阅记录
  Future<List<BorrowRecord>> getUserBorrows(int userId) async {
    final data = await _borrowApi.getUserBorrows(userId);
    return data.map((json) => BorrowRecord.fromJson(json)).toList();
  }
}
