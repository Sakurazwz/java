import '../domain/book.dart';
import 'book_api.dart';

// 图书数据仓库，负责将 API 数据转换为领域实体
class BookRepository {
  final BookApi _bookApi = BookApi();

  // 获取所有图书
  Future<List<Book>> getAllBooks() async {
    final data = await _bookApi.getAllBooks();
    return data.map((json) => Book.fromJson(json)).toList();
  }

  // 根据 ID 获取图书
  Future<Book> getBookById(int id) async {
    final data = await _bookApi.getBookById(id);
    return Book.fromJson(data);
  }

  // 按书名搜索图书
  Future<List<Book>> searchBooks(String title) async {
    final data = await _bookApi.searchBooks(title);
    return data.map((json) => Book.fromJson(json)).toList();
  }

  // 添加图书
  Future<Book> addBook(Book book) async {
    final data = await _bookApi.addBook(book.toJson());
    return Book.fromJson(data);
  }

  // 更新图书
  Future<Book> updateBook(Book book) async {
    final data = await _bookApi.updateBook(book.toJson());
    return Book.fromJson(data);
  }

  // 删除图书
  Future<void> deleteBook(int id) async {
    await _bookApi.deleteBook(id);
  }

  // 智能推荐图书
  Future<List<Book>> recommend(String query) async {
    final data = await _bookApi.recommend(query);
    return data.map((json) => Book.fromJson(json)).toList();
  }
}
