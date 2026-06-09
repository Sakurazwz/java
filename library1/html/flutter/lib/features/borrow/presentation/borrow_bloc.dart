import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';
import '../data/borrow_repository.dart';
import '../domain/borrow_record.dart';
import '../../../core/storage/token_storage.dart';
import '../../../core/utils/jwt_utils.dart';

// 借阅事件
abstract class BorrowEvent extends Equatable {
  @override
  List<Object?> get props => [];
}

// 加载借阅记录
class BorrowLoadRequested extends BorrowEvent {}

// 归还图书
class BorrowReturnRequested extends BorrowEvent {
  final int bookId;

  BorrowReturnRequested({required this.bookId});

  @override
  List<Object?> get props => [bookId];
}

// 续借图书
class BorrowRenewRequested extends BorrowEvent {
  final int bookId;

  BorrowRenewRequested({required this.bookId});

  @override
  List<Object?> get props => [bookId];
}

// 借阅状态
abstract class BorrowState extends Equatable {
  @override
  List<Object?> get props => [];
}

// 初始状态
class BorrowInitial extends BorrowState {}

// 加载中
class BorrowLoading extends BorrowState {}

// 加载完成
class BorrowLoaded extends BorrowState {
  final List<BorrowRecord> records;

  BorrowLoaded({required this.records});

  @override
  List<Object?> get props => [records];
}

// 操作成功
class BorrowSuccess extends BorrowState {
  final String message;

  BorrowSuccess({required this.message});

  @override
  List<Object?> get props => [message];
}

// 加载失败
class BorrowError extends BorrowState {
  final String message;

  BorrowError({required this.message});

  @override
  List<Object?> get props => [message];
}

// 借阅 Bloc，处理加载、归还、续借逻辑
class BorrowBloc extends Bloc<BorrowEvent, BorrowState> {
  final BorrowRepository _borrowRepository = BorrowRepository();

  BorrowBloc() : super(BorrowInitial()) {
    on<BorrowLoadRequested>(_onLoadRequested);
    on<BorrowReturnRequested>(_onReturnRequested);
    on<BorrowRenewRequested>(_onRenewRequested);
  }

  // 获取当前用户 ID
  int? _getUserId() {
    final token = TokenStorage.getToken();
    if (token == null) return null;
    return JwtUtils.getUserId(token);
  }

  // 处理加载事件
  Future<void> _onLoadRequested(
    BorrowLoadRequested event,
    Emitter<BorrowState> emit,
  ) async {
    emit(BorrowLoading());
    try {
      final userId = _getUserId();
      if (userId == null) {
        emit(BorrowError(message: '未登录'));
        return;
      }
      final records = await _borrowRepository.getUserBorrows(userId);
      emit(BorrowLoaded(records: records));
    } catch (e) {
      emit(BorrowError(message: e.toString()));
    }
  }

  // 处理归还事件
  Future<void> _onReturnRequested(
    BorrowReturnRequested event,
    Emitter<BorrowState> emit,
  ) async {
    try {
      final userId = _getUserId();
      if (userId == null) {
        emit(BorrowError(message: '未登录'));
        return;
      }
      await _borrowRepository.returnBook(event.bookId, userId);
      emit(BorrowSuccess(message: '还书成功'));
      // 重新加载列表
      add(BorrowLoadRequested());
    } catch (e) {
      emit(BorrowError(message: e.toString()));
    }
  }

  // 处理续借事件
  Future<void> _onRenewRequested(
    BorrowRenewRequested event,
    Emitter<BorrowState> emit,
  ) async {
    try {
      final userId = _getUserId();
      if (userId == null) {
        emit(BorrowError(message: '未登录'));
        return;
      }
      await _borrowRepository.renewBook(event.bookId, userId);
      emit(BorrowSuccess(message: '续借成功'));
      // 重新加载列表
      add(BorrowLoadRequested());
    } catch (e) {
      emit(BorrowError(message: e.toString()));
    }
  }
}
