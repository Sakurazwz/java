// 基础组件测试
import 'package:flutter_test/flutter_test.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:library1_flutter/app.dart';
import 'package:library1_flutter/core/storage/token_storage.dart';

void main() {
  testWidgets('应用启动测试', (WidgetTester tester) async {
    // 初始化 SharedPreferences mock（测试环境）
    SharedPreferences.setMockInitialValues({});
    await TokenStorage.init();

    // 构建应用并触发一帧渲染
    await tester.pumpWidget(const LibraryApp());

    // 等待路由和页面构建完成
    await tester.pumpAndSettle();

    // 验证登录页面能正常显示
    expect(find.text('登录'), findsWidgets);
  });
}
