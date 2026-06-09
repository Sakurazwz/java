import 'package:dio/dio.dart';
import 'dio_client.dart';

// API 接口服务
class ApiService {
  final Dio _dio = DioClient.instance.dio;

  // 认证相关
  Future<Response> login(String username, String password) {
    return _dio.post('/auth/login', data: {
      'username': username,
      'password': password,
    });
  }

  Future<Response> register(String name, String password) {
    return _dio.post('/users/register', data: {
      'name': name,
      'password': password,
    });
  }

  // 图书相关
  Future<Response> getAllBooks() {
    return _dio.get('/books/getAllBooks');
  }

  Future<Response> getBookById(int id) {
    return _dio.get('/books/getBookById/$id');
  }

  Future<Response> searchBooks(String title) {
    return _dio.get('/books/getBookByTitle', queryParameters: {'title': title});
  }

  Future<Response> addBook(Map<String, dynamic> book) {
    return _dio.post('/books/addBook', data: book);
  }

  Future<Response> updateBook(Map<String, dynamic> book) {
    return _dio.put('/books/updateBook', data: book);
  }

  Future<Response> deleteBook(int id) {
    return _dio.delete('/books/deleteBook/$id');
  }

  Future<Response> recommend(String query) {
    return _dio.get('/books/recommend', queryParameters: {'query': query});
  }

  // 借阅相关
  Future<Response> getAllBorrows({int? userId}) {
    final params = <String, dynamic>{};
    if (userId != null) params['userId'] = userId;
    return _dio.get('/borrow/all', queryParameters: params);
  }

  Future<Response> borrowBook(int bookId, int userId) {
    return _dio.post('/borrow/add', data: {
      'bookId': bookId,
      'userId': userId,
    });
  }

  Future<Response> returnBook(int bookId, int userId) {
    return _dio.delete('/borrow/back', data: {
      'bookId': bookId,
      'userId': userId,
    });
  }

  Future<Response> renewBook(int bookId, int userId) {
    return _dio.post('/borrow/updateBorrow', data: {
      'bookId': bookId,
      'userId': userId,
    });
  }

  Future<Response> getUserBorrows(int userId) {
    return _dio.get('/borrow/user', queryParameters: {'userId': userId});
  }

  // 历史相关
  Future<Response> getAllHistory(
      {int? userId, String? startDate, String? endDate}) {
    final params = <String, dynamic>{};
    if (userId != null) params['userId'] = userId;
    if (startDate != null) params['startDate'] = startDate;
    if (endDate != null) params['endDate'] = endDate;
    return _dio.get('/borrowhistory/all', queryParameters: params);
  }

  Future<Response> getHistoryByUserId(int userId) {
    return _dio
        .get('/borrowhistory/getBorrowHistoryByUserId/$userId');
  }

  // 用户管理相关
  Future<Response> getAllUsers({String? name}) {
    final params = <String, dynamic>{};
    if (name != null) params['name'] = name;
    return _dio.get('/users/all', queryParameters: params);
  }

  Future<Response> deleteUser(int id) {
    return _dio.delete('/users/delete/$id');
  }

  Future<Response> updateRole(int id, String role) {
    return _dio.put('/users/updateRole/$id', data: {'role': role});
  }
}