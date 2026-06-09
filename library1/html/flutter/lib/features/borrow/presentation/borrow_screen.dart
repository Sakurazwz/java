import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:intl/intl.dart';
import '../../../shared/widgets/app_drawer.dart';
import 'borrow_bloc.dart';

// 借阅管理页面
class BorrowScreen extends StatefulWidget {
  const BorrowScreen({super.key});

  @override
  State<BorrowScreen> createState() => _BorrowScreenState();
}

class _BorrowScreenState extends State<BorrowScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('我的借阅'),
      ),
      drawer: const AppDrawer(),
      body: BlocProvider(
        create: (context) => BorrowBloc()..add(BorrowLoadRequested()),
        child: BlocListener<BorrowBloc, BorrowState>(
          listener: (context, state) {
            if (state is BorrowSuccess) {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text(state.message)),
              );
            } else if (state is BorrowError) {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text(state.message)),
              );
            }
          },
          child: BlocBuilder<BorrowBloc, BorrowState>(
            builder: (context, state) {
              if (state is BorrowLoading) {
                return const Center(child: CircularProgressIndicator());
              } else if (state is BorrowLoaded) {
                if (state.records.isEmpty) {
                  return const Center(child: Text('暂无借阅记录'));
                }
                return RefreshIndicator(
                  onRefresh: () async {
                    context.read<BorrowBloc>().add(BorrowLoadRequested());
                  },
                  child: ListView.builder(
                    itemCount: state.records.length,
                    itemBuilder: (context, index) {
                      final record = state.records[index];
                      final dateFormat = DateFormat('yyyy-MM-dd');
                      return Card(
                        margin: const EdgeInsets.symmetric(
                          horizontal: 16,
                          vertical: 4,
                        ),
                        child: ListTile(
                          title: Text(record.bookTitle),
                          subtitle: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                  '借阅日期: ${dateFormat.format(record.borrowDate)}'),
                              Text(
                                '应还日期: ${dateFormat.format(record.returnDate)}',
                                style: TextStyle(
                                  color:
                                      record.isOverdue ? Colors.red : null,
                                ),
                              ),
                              if (record.isOverdue)
                                const Text(
                                  '已逾期',
                                  style: TextStyle(
                                    color: Colors.red,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                            ],
                          ),
                          trailing: Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              IconButton(
                                icon: const Icon(Icons.autorenew),
                                tooltip: '续借',
                                onPressed: () {
                                  context.read<BorrowBloc>().add(
                                        BorrowRenewRequested(
                                            bookId: record.bookId),
                                      );
                                },
                              ),
                              IconButton(
                                icon: const Icon(Icons.keyboard_return),
                                tooltip: '还书',
                                onPressed: () {
                                  context.read<BorrowBloc>().add(
                                        BorrowReturnRequested(
                                            bookId: record.bookId),
                                      );
                                },
                              ),
                            ],
                          ),
                        ),
                      );
                    },
                  ),
                );
              } else if (state is BorrowError) {
                return Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(state.message),
                      ElevatedButton(
                        onPressed: () {
                          context
                              .read<BorrowBloc>()
                              .add(BorrowLoadRequested());
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
      ),
    );
  }
}
