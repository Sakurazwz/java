import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';
import '../data/history_repository.dart';
import '../domain/borrow_history.dart';
import '../../../core/storage/token_storage.dart';
import '../../../core/utils/jwt_utils.dart';

// 历史事件
abstract class HistoryEvent extends Equatable {
  @override
  List<Object?> get props => [];
}

// 加载历史记录事件
class HistoryLoadRequested extends HistoryEvent {}

// 历史状态
abstract class HistoryState extends Equatable {
  @override
  List<Object?> get props => [];
}

// 初始状态
class HistoryInitial extends HistoryState {}

// 加载中状态
class HistoryLoading extends HistoryState {}

// 加载完成状态
class HistoryLoaded extends HistoryState {
  final List<BorrowHistory> records;

  HistoryLoaded({required this.records});

  @override
  List<Object?> get props => [records];
}

// 错误状态
class HistoryError extends HistoryState {
  final String message;

  HistoryError({required this.message});

  @override
  List<Object?> get props => [message];
}

// 历史 Bloc
class HistoryBloc extends Bloc<HistoryEvent, HistoryState> {
  final HistoryRepository _historyRepository = HistoryRepository();

  HistoryBloc() : super(HistoryInitial()) {
    on<HistoryLoadRequested>(_onLoadRequested);
  }

  // 处理加载历史记录事件
  Future<void> _onLoadRequested(
    HistoryLoadRequested event,
    Emitter<HistoryState> emit,
  ) async {
    emit(HistoryLoading());
    try {
      // 获取 token
      final token = TokenStorage.getToken();
      if (token == null) {
        emit(HistoryError(message: '未登录'));
        return;
      }
      // 从 token 中提取用户 ID
      final userId = JwtUtils.getUserId(token);
      if (userId == null) {
        emit(HistoryError(message: '无法获取用户信息'));
        return;
      }
      // 获取该用户的历史记录
      final records = await _historyRepository.getHistoryByUserId(userId);
      emit(HistoryLoaded(records: records));
    } catch (e) {
      emit(HistoryError(message: e.toString()));
    }
  }
}
