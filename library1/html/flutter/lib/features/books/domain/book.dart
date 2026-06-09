// 图书实体
class Book {
  final int id;
  final String title;
  final String author;
  final String isbn;
  final String publisher;
  final String category;
  final String? cover;
  final int count;
  final int borrowCount;

  const Book({
    required this.id,
    required this.title,
    required this.author,
    required this.isbn,
    required this.publisher,
    required this.category,
    this.cover,
    required this.count,
    required this.borrowCount,
  });

  // 从 JSON 反序列化
  factory Book.fromJson(Map<String, dynamic> json) {
    return Book(
      id: json['id'] ?? 0,
      title: json['title'] ?? '',
      author: json['author'] ?? '',
      isbn: json['isbn'] ?? '',
      publisher: json['publisher'] ?? '',
      category: json['category'] ?? '',
      cover: json['cover'],
      count: json['count'] ?? 0,
      borrowCount: json['borrowCount'] ?? 0,
    );
  }

  // 序列化为 JSON
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'title': title,
      'author': author,
      'isbn': isbn,
      'publisher': publisher,
      'category': category,
      'cover': cover,
      'count': count,
      'borrowCount': borrowCount,
    };
  }

  // 是否可借阅
  bool get isAvailable => count > 0;
}
