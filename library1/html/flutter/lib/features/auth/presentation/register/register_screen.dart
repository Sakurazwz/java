import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'register_bloc.dart';

// 注册页面
class RegisterScreen extends StatefulWidget {
  const RegisterScreen({super.key});

  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final _formKey = GlobalKey<FormState>();
  final _usernameController = TextEditingController();
  final _passwordController = TextEditingController();
  final _confirmPasswordController = TextEditingController();

  @override
  void dispose() {
    _usernameController.dispose();
    _passwordController.dispose();
    _confirmPasswordController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('注册'),
      ),
      body: BlocProvider(
        create: (context) => RegisterBloc(),
        child: BlocListener<RegisterBloc, RegisterState>(
          listener: (context, state) {
            // 注册成功跳转登录页
            if (state is RegisterSuccess) {
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('注册成功，请登录')),
              );
              context.go('/login');
            } else if (state is RegisterFailure) {
              // 注册失败显示错误提示
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
                  const SizedBox(height: 16),
                  // 确认密码输入框
                  TextFormField(
                    controller: _confirmPasswordController,
                    decoration: const InputDecoration(
                      labelText: '确认密码',
                      prefixIcon: Icon(Icons.lock),
                    ),
                    obscureText: true,
                    validator: (value) {
                      if (value == null || value.isEmpty) {
                        return '请确认密码';
                      }
                      if (value != _passwordController.text) {
                        return '两次密码不一致';
                      }
                      return null;
                    },
                  ),
                  const SizedBox(height: 24),
                  // 注册按钮
                  BlocBuilder<RegisterBloc, RegisterState>(
                    builder: (context, state) {
                      return SizedBox(
                        width: double.infinity,
                        child: ElevatedButton(
                          onPressed: state is RegisterLoading
                              ? null
                              : () {
                                  if (_formKey.currentState!.validate()) {
                                    context.read<RegisterBloc>().add(
                                          RegisterSubmitted(
                                            username: _usernameController.text,
                                            password: _passwordController.text,
                                          ),
                                        );
                                  }
                                },
                          child: state is RegisterLoading
                              ? const CircularProgressIndicator()
                              : const Text('注册'),
                        ),
                      );
                    },
                  ),
                  const SizedBox(height: 16),
                  // 跳转登录页面
                  TextButton(
                    onPressed: () => context.go('/login'),
                    child: const Text('已有账号？立即登录'),
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
