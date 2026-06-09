import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../data/book_repository.dart';
import '../../domain/book.dart';

// 图书表单页面（添加/编辑）
class BookFormScreen extends StatefulWidget {
  final int? bookId;

  const BookFormScreen({super.key, this.bookId});

  @override
  State<BookFormScreen> createState() => _BookFormScreenState();
}

class _BookFormScreenState extends State<BookFormScreen> {
  final _formKey = GlobalKey<FormState>();
  final _titleController = TextEditingController();
  final _authorController = TextEditingController();
  final _isbnController = TextEditingController();
  final _publisherController = TextEditingController();
  final _categoryController = TextEditingController();
  final _countController = TextEditingController();

  final BookRepository _bookRepository = BookRepository();
  bool _isLoading = false;
  bool _isEditing = false;

  @override
  void initState() {
    super.initState();
    _isEditing = widget.bookId != null;
    if (_isEditing) {
      _loadBook();
    }
  }

  // 加载已有图书数据（编辑模式）
  Future<void> _loadBook() async {
    try {
      final book = await _bookRepository.getBookById(widget.bookId!);
      _titleController.text = book.title;
      _authorController.text = book.author;
      _isbnController.text = book.isbn;
      _publisherController.text = book.publisher;
      _categoryController.text = book.category;
      _countController.text = book.count.toString();
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('加载失败: $e')),
        );
      }
    }
  }

  // 提交表单
  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _isLoading = true);

    try {
      final book = Book(
        id: widget.bookId ?? 0,
        title: _titleController.text,
        author: _authorController.text,
        isbn: _isbnController.text,
        publisher: _publisherController.text,
        category: _categoryController.text,
        count: int.parse(_countController.text),
        borrowCount: 0,
      );

      if (_isEditing) {
        await _bookRepository.updateBook(book);
      } else {
        await _bookRepository.addBook(book);
      }

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(_isEditing ? '更新成功' : '添加成功')),
        );
        context.go('/books');
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('操作失败: $e')),
        );
      }
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  @override
  void dispose() {
    _titleController.dispose();
    _authorController.dispose();
    _isbnController.dispose();
    _publisherController.dispose();
    _categoryController.dispose();
    _countController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(_isEditing ? '编辑图书' : '添加图书'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              // 书名输入
              TextFormField(
                controller: _titleController,
                decoration: const InputDecoration(labelText: '书名'),
                validator: (value) {
                  if (value == null || value.isEmpty) return '请输入书名';
                  return null;
                },
              ),
              const SizedBox(height: 16),
              // 作者输入
              TextFormField(
                controller: _authorController,
                decoration: const InputDecoration(labelText: '作者'),
                validator: (value) {
                  if (value == null || value.isEmpty) return '请输入作者';
                  return null;
                },
              ),
              const SizedBox(height: 16),
              // ISBN 输入
              TextFormField(
                controller: _isbnController,
                decoration: const InputDecoration(labelText: 'ISBN'),
                validator: (value) {
                  if (value == null || value.isEmpty) return '请输入ISBN';
                  return null;
                },
              ),
              const SizedBox(height: 16),
              // 出版社输入
              TextFormField(
                controller: _publisherController,
                decoration: const InputDecoration(labelText: '出版社'),
                validator: (value) {
                  if (value == null || value.isEmpty) return '请输入出版社';
                  return null;
                },
              ),
              const SizedBox(height: 16),
              // 分类输入
              TextFormField(
                controller: _categoryController,
                decoration: const InputDecoration(labelText: '分类'),
                validator: (value) {
                  if (value == null || value.isEmpty) return '请输入分类';
                  return null;
                },
              ),
              const SizedBox(height: 16),
              // 库存数量输入
              TextFormField(
                controller: _countController,
                decoration: const InputDecoration(labelText: '库存数量'),
                keyboardType: TextInputType.number,
                validator: (value) {
                  if (value == null || value.isEmpty) return '请输入库存数量';
                  if (int.tryParse(value) == null) return '请输入有效数字';
                  return null;
                },
              ),
              const SizedBox(height: 24),
              // 提交按钮
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: _isLoading ? null : _submit,
                  child: _isLoading
                      ? const CircularProgressIndicator()
                      : Text(_isEditing ? '更新' : '添加'),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
