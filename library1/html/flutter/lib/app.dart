import 'package:flutter/material.dart';

class LibraryApp extends StatelessWidget {
  const LibraryApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: '图书管理系统',
      home: Scaffold(
        appBar: AppBar(title: const Text('图书管理系统')),
        body: const Center(child: Text('正在加载...')),
      ),
    );
  }
}
