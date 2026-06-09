import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../../../shared/widgets/app_drawer.dart';
import '../../auth/domain/user.dart';
import 'users_bloc.dart';

// 用户管理页面（管理员专用）
class UsersScreen extends StatefulWidget {
  const UsersScreen({super.key});

  @override
  State<UsersScreen> createState() => _UsersScreenState();
}

class _UsersScreenState extends State<UsersScreen> {
  final _searchController = TextEditingController();

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('用户管理'),
      ),
      drawer: const AppDrawer(),
      body: BlocProvider(
        create: (context) => UsersBloc()..add(UsersLoadRequested()),
        child: BlocListener<UsersBloc, UsersState>(
          listener: (context, state) {
            if (state is UsersSuccess) {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text(state.message)),
              );
            } else if (state is UsersError) {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text(state.message)),
              );
            }
          },
          child: Column(
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
                          hintText: '搜索用户...',
                          prefixIcon: Icon(Icons.search),
                          border: OutlineInputBorder(),
                        ),
                      ),
                    ),
                    const SizedBox(width: 8),
                    ElevatedButton(
                      onPressed: () {
                        final query = _searchController.text.trim();
                        context.read<UsersBloc>().add(
                              UsersSearchRequested(query: query),
                            );
                      },
                      child: const Text('搜索'),
                    ),
                  ],
                ),
              ),
              // 用户列表
              Expanded(
                child: BlocBuilder<UsersBloc, UsersState>(
                  builder: (context, state) {
                    if (state is UsersLoading) {
                      return const Center(child: CircularProgressIndicator());
                    } else if (state is UsersLoaded) {
                      if (state.users.isEmpty) {
                        return const Center(child: Text('暂无用户'));
                      }
                      return RefreshIndicator(
                        onRefresh: () async {
                          context.read<UsersBloc>().add(UsersLoadRequested());
                        },
                        child: ListView.builder(
                          itemCount: state.users.length,
                          itemBuilder: (context, index) {
                            final user = state.users[index];
                            return Card(
                              margin: const EdgeInsets.symmetric(
                                horizontal: 16,
                                vertical: 4,
                              ),
                              child: ListTile(
                                leading: CircleAvatar(
                                  child: Text(user.name[0].toUpperCase()),
                                ),
                                title: Text(user.name),
                                subtitle: Text('角色: ${user.role}'),
                                trailing: PopupMenuButton<String>(
                                  onSelected: (value) {
                                    if (value == 'delete') {
                                      _showDeleteDialog(context, user);
                                    } else if (value == 'admin' || value == 'user') {
                                      context.read<UsersBloc>().add(
                                            UsersRoleUpdateRequested(
                                              userId: user.id,
                                              role: value == 'admin' ? 'ADMIN' : 'USER',
                                            ),
                                          );
                                    }
                                  },
                                  itemBuilder: (context) => [
                                    const PopupMenuItem(
                                      value: 'admin',
                                      child: Text('设为管理员'),
                                    ),
                                    const PopupMenuItem(
                                      value: 'user',
                                      child: Text('设为普通用户'),
                                    ),
                                    const PopupMenuItem(
                                      value: 'delete',
                                      child: Text('删除用户'),
                                    ),
                                  ],
                                ),
                              ),
                            );
                          },
                        ),
                      );
                    } else if (state is UsersError) {
                      return Center(
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Text(state.message),
                            ElevatedButton(
                              onPressed: () {
                                context.read<UsersBloc>().add(UsersLoadRequested());
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

  // 显示删除确认对话框
  void _showDeleteDialog(BuildContext context, User user) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('确认删除'),
        content: Text('确定要删除用户 "${user.name}" 吗？'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              context.read<UsersBloc>().add(
                    UsersDeleteRequested(userId: user.id),
                  );
            },
            child: const Text('删除'),
          ),
        ],
      ),
    );
  }
}
