import '../../../core/network/api_service.dart';

// 图书 API 接口封装
class BookApi {
  final ApiService _apiService = ApiService();

  // 获取所有图书
  Future<List<dynamic>> getAllBooks() async {
    final response = await _apiService.getAllBooks();
    return response.data;
  }

  // 根据 ID 获取图书
  Future<Map<String, dynamic>> getBookById(int id) async {
    final response = await _apiService.getBookById(id);
    return response.data;
  }

  // 按书名搜索图书
  Future<List<dynamic>> searchBooks(String title) async {
    final response = await _apiService.searchBooks(title);
    return response.data;
  }

  // 添加图书
  Future<Map<String, dynamic>> addBook(Map<String, dynamic> book) async {
    final response = await _apiService.addBook(book);
    return response.data;
  }

  // 更新图书
  Future<Map<String, dynamic>> updateBook(Map<String, dynamic> book) async {
    final response = await _apiService.updateBook(book);
    return response.data;
  }

  // 删除图书
  Future<void> deleteBook(int id) async {
    await _apiService.deleteBook(id);
  }

  // 智能推荐图书
  Future<List<dynamic>> recommend(String query) async {
    final response = await _apiService.recommend(query);
    return response.data;
  }
}
