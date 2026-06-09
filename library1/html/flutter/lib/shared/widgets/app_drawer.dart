import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../core/storage/token_storage.dart';

// 应用侧边栏
class AppDrawer extends StatelessWidget {
  const AppDrawer({super.key});

  @override
  Widget build(BuildContext context) {
    final isAuthenticated = TokenStorage.isAuthenticated();
    final isAdmin = TokenStorage.isAdmin();

    return Drawer(
      child: ListView(
        padding: EdgeInsets.zero,
        children: [
          const DrawerHeader(
            decoration: BoxDecoration(
              color: Colors.blue,
            ),
            child: Text(
              '图书管理系统',
              style: TextStyle(
                color: Colors.white,
                fontSize: 24,
              ),
            ),
          ),
          if (isAuthenticated) ...[
            ListTile(
              leading: const Icon(Icons.book),
              title: const Text('图书列表'),
              onTap: () {
                context.go('/books');
                Navigator.pop(context);
              },
            ),
            ListTile(
              leading: const Icon(Icons.library_books),
              title: const Text('我的借阅'),
              onTap: () {
                context.go('/borrow');
                Navigator.pop(context);
              },
            ),
            ListTile(
              leading: const Icon(Icons.history),
              title: const Text('借阅历史'),
              onTap: () {
                context.go('/history');
                Navigator.pop(context);
              },
            ),
            ListTile(
              leading: const Icon(Icons.person),
              title: const Text('个人中心'),
              onTap: () {
                context.go('/profile');
                Navigator.pop(context);
              },
            ),
            if (isAdmin) ...[
              const Divider(),
              ListTile(
                leading: const Icon(Icons.people),
                title: const Text('用户管理'),
                onTap: () {
                  context.go('/users');
                  Navigator.pop(context);
                },
              ),
            ],
            const Divider(),
            ListTile(
              leading: const Icon(Icons.logout),
              title: const Text('退出登录'),
              onTap: () async {
                await TokenStorage.deleteToken();
                if (context.mounted) {
                  context.go('/login');
                }
              },
            ),
          ] else ...[
            ListTile(
              leading: const Icon(Icons.login),
              title: const Text('登录'),
              onTap: () {
                context.go('/login');
                Navigator.pop(context);
              },
            ),
            ListTile(
              leading: const Icon(Icons.person_add),
              title: const Text('注册'),
              onTap: () {
                context.go('/register');
                Navigator.pop(context);
              },
            ),
          ],
        ],
      ),
    );
  }
}
