import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';
import '../../data/book_repository.dart';
import '../../domain/book.dart';

// 图书列表事件
abstract class BooksEvent extends Equatable {
  @override
  List<Object?> get props => [];
}

// 加载全部图书
class BooksLoadRequested extends BooksEvent {}

// 搜索图书
class BooksSearchRequested extends BooksEvent {
  final String query;

  BooksSearchRequested({required this.query});

  @override
  List<Object?> get props => [query];
}

// 刷新图书列表
class BooksRefreshRequested extends BooksEvent {}

// 智能推荐图书
class BooksRecommendRequested extends BooksEvent {
  final String query;

  BooksRecommendRequested({required this.query});

  @override
  List<Object?> get props => [query];
}

// 图书列表状态
abstract class BooksState extends Equatable {
  @override
  List<Object?> get props => [];
}

// 初始状态
class BooksInitial extends BooksState {}

// 加载中
class BooksLoading extends BooksState {}

// 加载完成
class BooksLoaded extends BooksState {
  final List<Book> books;

  BooksLoaded({required this.books});

  @override
  List<Object?> get props => [books];
}

// 加载失败
class BooksError extends BooksState {
  final String message;

  BooksError({required this.message});

  @override
  List<Object?> get props => [message];
}

// 图书列表 Bloc，处理加载、搜索、刷新逻辑
class BooksBloc extends Bloc<BooksEvent, BooksState> {
  final BookRepository _bookRepository = BookRepository();

  BooksBloc() : super(BooksInitial()) {
    on<BooksLoadRequested>(_onLoadRequested);
    on<BooksSearchRequested>(_onSearchRequested);
    on<BooksRefreshRequested>(_onRefreshRequested);
    on<BooksRecommendRequested>(_onRecommendRequested);
  }

  // 处理加载事件
  Future<void> _onLoadRequested(
    BooksLoadRequested event,
    Emitter<BooksState> emit,
  ) async {
    emit(BooksLoading());
    try {
      final books = await _bookRepository.getAllBooks();
      emit(BooksLoaded(books: books));
    } catch (e) {
      emit(BooksError(message: e.toString()));
    }
  }

  // 处理搜索事件
  Future<void> _onSearchRequested(
    BooksSearchRequested event,
    Emitter<BooksState> emit,
  ) async {
    emit(BooksLoading());
    try {
      final books = await _bookRepository.searchBooks(event.query);
      emit(BooksLoaded(books: books));
    } catch (e) {
      emit(BooksError(message: e.toString()));
    }
  }

  // 处理刷新事件
  Future<void> _onRefreshRequested(
    BooksRefreshRequested event,
    Emitter<BooksState> emit,
  ) async {
    try {
      final books = await _bookRepository.getAllBooks();
      emit(BooksLoaded(books: books));
    } catch (e) {
      emit(BooksError(message: e.toString()));
    }
  }

  // 处理智能推荐事件
  Future<void> _onRecommendRequested(
    BooksRecommendRequested event,
    Emitter<BooksState> emit,
  ) async {
    emit(BooksLoading());
    try {
      final books = await _bookRepository.recommend(event.query);
      emit(BooksLoaded(books: books));
    } catch (e) {
      emit(BooksError(message: e.toString()));
    }
  }
}
