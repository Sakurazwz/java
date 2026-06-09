import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'login_bloc.dart';

// 登录页面
class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _formKey = GlobalKey<FormState>();
  final _usernameController = TextEditingController();
  final _passwordController = TextEditingController();

  @override
  void dispose() {
    _usernameController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('登录'),
      ),
      body: BlocProvider(
        create: (context) => LoginBloc(),
        child: BlocListener<LoginBloc, LoginState>(
          listener: (context, state) {
            // 登录成功跳转到图书列表页
            if (state is LoginSuccess) {
              context.go('/books');
            } else if (state is LoginFailure) {
              // 登录失败显示错误提示
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text(state.error)),
              );
            }
          },
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Form(
              key: _formKey,
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  // 用户名输入框
                  TextFormField(
                    controller: _usernameController,
                    decoration: const InputDecoration(
                      labelText: '用户名',
                      prefixIcon: Icon(Icons.person),
                    ),
                    validator: (value) {
                      if (value == null || value.isEmpty) {
                        return '请输入用户名';
                      }
                      return null;
                    },
                  ),
                  const SizedBox(height: 16),
                  // 密码输入框
                  TextFormField(
                    controller: _passwordController,
                    decoration: const InputDecoration(
                      labelText: '密码',
                      prefixIcon: Icon(Icons.lock),
                    ),
                    obscureText: true,
                    validator: (value) {
                      if (value == null || value.isEmpty) {
                        return '请输入密码';
                      }
                      return null;
                    },
                  ),
                  const SizedBox(height: 24),
                  // 登录按钮
                  BlocBuilder<LoginBloc, LoginState>(
                    builder: (context, state) {
                      return SizedBox(
                        width: double.infinity,
                        child: ElevatedButton(
                          onPressed: state is LoginLoading
                              ? null
                              : () {
                                  if (_formKey.currentState!.validate()) {
                                    context.read<LoginBloc>().add(
                                          LoginSubmitted(
                                            username: _usernameController.text,
                                            password: _passwordController.text,
                                          ),
                                        );
                                  }
                                },
                          child: state is LoginLoading
                              ? const CircularProgressIndicator()
                              : const Text('登录'),
                        ),
                      );
                    },
                  ),
                  const SizedBox(height: 16),
                  // 跳转注册页面
                  TextButton(
                    onPressed: () => context.go('/register'),
                    child: const Text('没有账号？立即注册'),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
