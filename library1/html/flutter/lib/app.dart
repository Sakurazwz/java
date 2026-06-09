import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'core/storage/token_storage.dart';
import 'shared/theme/app_theme.dart';
import 'features/auth/presentation/login/login_screen.dart';
import 'features/auth/presentation/register/register_screen.dart';
import 'features/books/presentation/list/books_screen.dart';
import 'features/books/presentation/detail/book_detail_screen.dart';
import 'features/books/presentation/form/book_form_screen.dart';
import 'features/borrow/presentation/borrow_screen.dart';
import 'features/history/presentation/history_screen.dart';
import 'features/profile/presentation/profile_screen.dart';
import 'features/users/presentation/users_screen.dart';

// 应用主组件
class LibraryApp extends StatelessWidget {
  const LibraryApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: '图书管理系统',
      theme: AppTheme.lightTheme,
      routerConfig: _router,
      debugShowCheckedModeBanner: false,
    );
  }
}

// 路由配置
final _router = GoRouter(
  initialLocation: '/login',
  redirect: (context, state) {
    final isAuthenticated = TokenStorage.isAuthenticated();
    final isLoginRoute = state.matchedLocation == '/login' ||
        state.matchedLocation == '/register';

    // 未登录时访问受保护页面，重定向到登录页
    if (!isAuthenticated && !isLoginRoute) {
      return '/login';
    }

    // 已登录时访问登录页，重定向到图书列表
    if (isAuthenticated && isLoginRoute) {
      return '/books';
    }

    return null;
  },
  routes: [
    GoRoute(
      path: '/login',
      builder: (context, state) => const LoginScreen(),
    ),
    GoRoute(
      path: '/register',
      builder: (context, state) => const RegisterScreen(),
    ),
    GoRoute(
      path: '/books',
      builder: (context, state) => const BooksScreen(),
    ),
    GoRoute(
      path: '/books/add',
      builder: (context, state) => const BookFormScreen(),
    ),
    GoRoute(
      path: '/books/:id',
      builder: (context, state) {
        final id = int.parse(state.pathParameters['id']!);
        return BookDetailScreen(bookId: id);
      },
    ),
    GoRoute(
      path: '/books/:id/edit',
      builder: (context, state) {
        final id = int.parse(state.pathParameters['id']!);
        return BookFormScreen(bookId: id);
      },
    ),
    GoRoute(
      path: '/borrow',
      builder: (context, state) => const BorrowScreen(),
    ),
    GoRoute(
      path: '/history',
      builder: (context, state) => const HistoryScreen(),
    ),
    GoRoute(
      path: '/profile',
      builder: (context, state) => const ProfileScreen(),
    ),
    GoRoute(
      path: '/users',
      builder: (context, state) => const UsersScreen(),
    ),
  ],
);
