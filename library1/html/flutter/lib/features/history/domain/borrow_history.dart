// 借阅历史实体
class BorrowHistory {
  final int id;
  final int bookId;
  final int userId;
  final String bookTitle;
  final String userName;
  final String behaviour;
  final DateTime date;

  const BorrowHistory({
    required this.id,
    required this.bookId,
    required this.userId,
    required this.bookTitle,
    required this.userName,
    required this.behaviour,
    required this.date,
  });

  // 从 JSON 构造实体
  factory BorrowHistory.fromJson(Map<String, dynamic> json) {
    return BorrowHistory(
      id: json['id'] ?? 0,
      bookId: json['bookId'] ?? 0,
      userId: json['userId'] ?? 0,
      bookTitle: json['bookTitle'] ?? '',
      userName: json['userName'] ?? '',
      behaviour: json['behaviour'] ?? '',
      date: DateTime.parse(json['date'] ?? DateTime.now().toIso8601String()),
    );
  }
}
