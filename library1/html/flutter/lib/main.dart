import 'package:flutter/material.dart';
import 'app.dart';
import 'core/storage/token_storage.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // 初始化本地存储
  await TokenStorage.init();

  runApp(const LibraryApp());
}
