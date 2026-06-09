import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/storage/token_storage.dart';
import '../../../../shared/widgets/app_drawer.dart';
import 'books_bloc.dart';

// 图书列表页面
class BooksScreen extends StatefulWidget {
  const BooksScreen({super.key});

  @override
  State<BooksScreen> createState() => _BooksScreenState();
}

class _BooksScreenState extends State<BooksScreen> {
  final _searchController = TextEditingController();

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  // 显示智能推荐对话框
  void _showRecommendDialog(BuildContext context) {
    final recommendController = TextEditingController();

    showDialog(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: const Row(
          children: [
            Icon(Icons.auto_awesome, color: Colors.orange),
            SizedBox(width: 8),
            Text('智能图书推荐'),
          ],
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Text('告诉我你的兴趣或需求，我为你推荐图书'),
            const SizedBox(height: 16),
            TextField(
              controller: recommendController,
              decoration: const InputDecoration(
                hintText: '例如：科幻小说、编程入门、历史类...',
                border: OutlineInputBorder(),
              ),
              autofocus: true,
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(dialogContext),
            child: const Text('取消'),
          ),
          ElevatedButton(
            onPressed: () {
              final query = recommendController.text.trim();
              if (query.isNotEmpty) {
                context.read<BooksBloc>().add(
                      BooksRecommendRequested(query: query),
                    );
                Navigator.pop(dialogContext);
              }
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.orange,
              foregroundColor: Colors.white,
            ),
            child: const Text('推荐'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final isAdmin = TokenStorage.isAdmin();

    return Scaffold(
      appBar: AppBar(
        title: const Text('图书列表'),
        actions: [
          // 管理员可添加图书
          if (isAdmin)
            IconButton(
              icon: const Icon(Icons.add),
              onPressed: () => context.push('/books/add'),
            ),
        ],
      ),
      drawer: const AppDrawer(),
      body: BlocProvider(
        create: (context) => BooksBloc()..add(BooksLoadRequested()),
        child: Builder(
          builder: (builderContext) => Column(
            children: [
              // 搜索栏
              Padding(
                padding: const EdgeInsets.all(16.0),
                child: Row(
                  children: [
                    Expanded(
                      child: TextField(
                        controller: _searchController,
                        decoration: const InputDecoration(
                          hintText: '搜索图书...',
                          prefixIcon: Icon(Icons.search),
                          border: OutlineInputBorder(),
                        ),
                      ),
                    ),
                    const SizedBox(width: 8),
                    ElevatedButton(
                      onPressed: () {
                        final query = _searchController.text.trim();
                        if (query.isNotEmpty) {
                          builderContext.read<BooksBloc>().add(
                                BooksSearchRequested(query: query),
                              );
                        }
                      },
                      child: const Text('搜索'),
                    ),
                    const SizedBox(width: 8),
                    // 智能推荐按钮
                    ElevatedButton.icon(
                      onPressed: () => _showRecommendDialog(builderContext),
                      icon: const Icon(Icons.auto_awesome),
                      label: const Text('推荐'),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.orange,
                        foregroundColor: Colors.white,
                      ),
                    ),
                  ],
                ),
              ),
              // 图书列表
              Expanded(
                child: BlocBuilder<BooksBloc, BooksState>(
                  builder: (context, state) {
                  if (state is BooksLoading) {
                    return const Center(child: CircularProgressIndicator());
                  } else if (state is BooksLoaded) {
                    if (state.books.isEmpty) {
                      return const Center(child: Text('暂无图书'));
                    }
                    return RefreshIndicator(
                      onRefresh: () async {
                        context.read<BooksBloc>().add(BooksRefreshRequested());
                      },
                      child: ListView.builder(
                        itemCount: state.books.length,
                        itemBuilder: (context, index) {
                          final book = state.books[index];
                          return Card(
                            margin: const EdgeInsets.symmetric(
                              horizontal: 16,
                              vertical: 4,
                            ),
                            child: ListTile(
                              leading: book.cover != null
                                  ? Image.network(
                                      book.cover!,
                                      width: 50,
                                      height: 50,
                                      fit: BoxFit.cover,
                                      errorBuilder: (context, error,
                                              stackTrace) =>
                                          const Icon(Icons.book, size: 50),
                                    )
                                  : const Icon(Icons.book, size: 50),
                              title: Text(book.title),
                              subtitle:
                                  Text('${book.author} - ${book.publisher}'),
                              trailing: Text(
                                '库存: ${book.count}',
                                style: TextStyle(
                                  color: book.isAvailable
                                      ? Colors.green
                                      : Colors.red,
                                ),
                              ),
                              onTap: () => context.go('/books/${book.id}'),
                            ),
                          );
                        },
                      ),
                    );
                  } else if (state is BooksError) {
                    return Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Text(state.message),
                          ElevatedButton(
                            onPressed: () {
                              context
                                  .read<BooksBloc>()
                                  .add(BooksLoadRequested());
                            },
                            child: const Text('重试'),
                          ),
                        ],
                      ),
                    );
                  }
                  return const SizedBox.shrink();
                },
              ),
            ),
          ],
        ),
      ),
      ),
    );
  }
}
