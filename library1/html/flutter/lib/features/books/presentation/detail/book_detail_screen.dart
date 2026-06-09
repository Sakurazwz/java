import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/storage/token_storage.dart';
import '../../data/book_repository.dart';
import '../../domain/book.dart';

// 图书详情页面
class BookDetailScreen extends StatefulWidget {
  final int bookId;

  const BookDetailScreen({super.key, required this.bookId});

  @override
  State<BookDetailScreen> createState() => _BookDetailScreenState();
}

class _BookDetailScreenState extends State<BookDetailScreen> {
  final BookRepository _bookRepository = BookRepository();
  Book? _book;
  bool _isLoading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _loadBook();
  }

  // 加载图书详情
  Future<void> _loadBook() async {
    try {
      final book = await _bookRepository.getBookById(widget.bookId);
      setState(() {
        _book = book;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _error = e.toString();
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final isAdmin = TokenStorage.isAdmin();

    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go('/books'),
        ),
        title: const Text('图书详情'),
        actions: [
          // 管理员可编辑图书
          if (isAdmin && _book != null)
            IconButton(
              icon: const Icon(Icons.edit),
              onPressed: () => context.go('/books/${widget.bookId}/edit'),
            ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(_error!),
                      ElevatedButton(
                        onPressed: _loadBook,
                        child: const Text('重试'),
                      ),
                    ],
                  ),
                )
              : _book != null
                  ? SingleChildScrollView(
                      padding: const EdgeInsets.all(16),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          // 封面图片
                          if (_book!.cover != null)
                            Center(
                              child: Image.network(
                                _book!.cover!,
                                height: 200,
                                fit: BoxFit.cover,
                                errorBuilder: (context, error, stackTrace) =>
                                    const Icon(Icons.book, size: 100),
                              ),
                            ),
                          const SizedBox(height: 16),
                          // 书名
                          Text(
                            _book!.title,
                            style: const TextStyle(
                              fontSize: 24,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          const SizedBox(height: 8),
                          // 图书信息
                          Text('作者: ${_book!.author}'),
                          Text('ISBN: ${_book!.isbn}'),
                          Text('出版社: ${_book!.publisher}'),
                          Text('分类: ${_book!.category}'),
                          Text('库存: ${_book!.count}'),
                          Text('借阅次数: ${_book!.borrowCount}'),
                          const SizedBox(height: 16),
                          // 借阅状态
                          Text(
                            _book!.isAvailable ? '可借阅' : '已借完',
                            style: TextStyle(
                              color: _book!.isAvailable
                                  ? Colors.green
                                  : Colors.red,
                              fontSize: 18,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ],
                      ),
                    )
                  : const SizedBox.shrink(),
    );
  }
}
