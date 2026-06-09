import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../../core/storage/token_storage.dart';

// 个人中心页面
class ProfileScreen extends StatelessWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('个人中心'),
      ),
      body: ListView(
        children: [
          // 用户信息头部
          Container(
            padding: const EdgeInsets.all(24),
            color: Theme.of(context).primaryColor.withOpacity(0.1),
            child: Column(
              children: [
                const CircleAvatar(
                  radius: 40,
                  child: Icon(Icons.person, size: 40),
                ),
                const SizedBox(height: 12),
                Text(
                  TokenStorage.isAdmin() ? '管理员' : '普通用户',
                  style: Theme.of(context).textTheme.titleLarge,
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),
          // 菜单项
          ListTile(
            leading: const Icon(Icons.history),
            title: const Text('借阅历史'),
            trailing: const Icon(Icons.chevron_right),
            onTap: () => context.push('/history'),
          ),
          ListTile(
            leading: const Icon(Icons.book),
            title: const Text('我的借阅'),
            trailing: const Icon(Icons.chevron_right),
            onTap: () => context.push('/borrow'),
          ),
          const Divider(),
          ListTile(
            leading: const Icon(Icons.logout, color: Colors.red),
            title: const Text('退出登录', style: TextStyle(color: Colors.red)),
            onTap: () async {
              await TokenStorage.deleteToken();
              if (context.mounted) {
                context.go('/login');
              }
            },
          ),
        ],
      ),
    );
  }
}
