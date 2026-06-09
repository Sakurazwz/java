// 借阅记录实体
class BorrowRecord {
  final int id;
  final int bookId;
  final int userId;
  final String bookTitle;
  final String userName;
  final DateTime borrowDate;
  final DateTime returnDate;

  const BorrowRecord({
    required this.id,
    required this.bookId,
    required this.userId,
    required this.bookTitle,
    required this.userName,
    required this.borrowDate,
    required this.returnDate,
  });

  // 从 JSON 创建借阅记录
  factory BorrowRecord.fromJson(Map<String, dynamic> json) {
    return BorrowRecord(
      id: json['id'] ?? 0,
      bookId: json['bookId'] ?? 0,
      userId: json['userId'] ?? 0,
      bookTitle: json['bookTitle'] ?? '',
      userName: json['userName'] ?? '',
      borrowDate: DateTime.parse(
          json['borrowDate'] ?? DateTime.now().toIso8601String()),
      returnDate: DateTime.parse(
          json['returnDate'] ?? DateTime.now().toIso8601String()),
    );
  }

  // 判断是否已逾期
  bool get isOverdue => returnDate.isBefore(DateTime.now());
}
