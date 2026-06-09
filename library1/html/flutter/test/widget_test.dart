// 基础组件测试
import 'package:flutter_test/flutter_test.dart';
import 'package:library1_flutter/app.dart';

void main() {
  testWidgets('应用启动测试', (WidgetTester tester) async {
    // 构建应用并触发一帧渲染
    await tester.pumpWidget(const LibraryApp());

    // 验证应用能正常启动（登录页面应显示）
    expect(find.text('图书管理系统'), findsOneWidget);
  });
}
