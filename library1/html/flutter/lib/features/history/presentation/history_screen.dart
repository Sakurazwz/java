import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:intl/intl.dart';
import '../../../shared/widgets/app_drawer.dart';
import 'history_bloc.dart';

// 借阅历史页面
class HistoryScreen extends StatefulWidget {
  const HistoryScreen({super.key});

  @override
  State<HistoryScreen> createState() => _HistoryScreenState();
}

class _HistoryScreenState extends State<HistoryScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('借阅历史'),
      ),
      drawer: const AppDrawer(),
      body: BlocProvider(
        create: (context) => HistoryBloc()..add(HistoryLoadRequested()),
        child: BlocBuilder<HistoryBloc, HistoryState>(
          builder: (context, state) {
            // 加载中状态
            if (state is HistoryLoading) {
              return const Center(child: CircularProgressIndicator());
            }
            // 加载完成状态
            else if (state is HistoryLoaded) {
              if (state.records.isEmpty) {
                return const Center(child: Text('暂无借阅历史'));
              }
              return RefreshIndicator(
                onRefresh: () async {
                  context.read<HistoryBloc>().add(HistoryLoadRequested());
                },
                child: ListView.builder(
                  itemCount: state.records.length,
                  itemBuilder: (context, index) {
                    final record = state.records[index];
                    final dateFormat = DateFormat('yyyy-MM-dd HH:mm');
                    return Card(
                      margin: const EdgeInsets.symmetric(
                        horizontal: 16,
                        vertical: 4,
                      ),
                      child: ListTile(
                        leading: Icon(
                          _getBehaviourIcon(record.behaviour),
                          color: _getBehaviourColor(record.behaviour),
                        ),
                        title: Text('图书 ID: ${record.bookId}'),
                        subtitle: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text('操作: ${record.behaviour}'),
                            Text('时间: ${dateFormat.format(record.date)}'),
                          ],
                        ),
                      ),
                    );
                  },
                ),
              );
            }
            // 错误状态
            else if (state is HistoryError) {
              return Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(state.message),
                    ElevatedButton(
                      onPressed: () {
                        context.read<HistoryBloc>().add(HistoryLoadRequested());
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
    );
  }

  // 根据操作类型返回对应图标
  IconData _getBehaviourIcon(String behaviour) {
    switch (behaviour.toLowerCase()) {
      case 'borrow':
      case '借阅':
        return Icons.book;
      case 'return':
      case '归还':
        return Icons.keyboard_return;
      case 'renew':
      case '续借':
        return Icons.autorenew;
      default:
        return Icons.history;
    }
  }

  // 根据操作类型返回对应颜色
  Color _getBehaviourColor(String behaviour) {
    switch (behaviour.toLowerCase()) {
      case 'borrow':
      case '借阅':
        return Colors.blue;
      case 'return':
      case '归还':
        return Colors.green;
      case 'renew':
      case '续借':
        return Colors.orange;
      default:
        return Colors.grey;
    }
  }
}
